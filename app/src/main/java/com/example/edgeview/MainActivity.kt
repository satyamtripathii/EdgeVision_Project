package com.example.edgeview

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.os.Bundle
import android.util.Size
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.edgeview.camera.Camera2Manager
import com.example.edgeview.gl.GLRenderer
import android.opengl.GLSurfaceView
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : ComponentActivity() {
    private lateinit var glView: GLSurfaceView
    private lateinit var renderer: GLRenderer
    private lateinit var cam: Camera2Manager
    private lateinit var fpsText: TextView
    private lateinit var toggleBtn: Button
    private lateinit var saveBtn: Button

    private val native = NativeBridge()
    private var ctx: Long = 0L

    private val size = Size(640, 480)

    private var mode = AtomicInteger(0)
    private val saveNext = AtomicBoolean(false)

    private lateinit var inBuffer: ByteBuffer
    private lateinit var outBuffer: ByteBuffer

    private var frames = 0
    private var lastTime = System.currentTimeMillis()

    private val requestPerms = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera() else finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glView = findViewById(R.id.gl_view)
        fpsText = findViewById(R.id.fps_text)
        toggleBtn = findViewById(R.id.toggle_btn)
        saveBtn = findViewById(R.id.save_btn)

        renderer = GLRenderer(size.width, size.height)
        glView.setEGLContextClientVersion(2)
        glView.setRenderer(renderer)
        glView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        toggleBtn.setOnClickListener {
            val next = (mode.get() + 1) % 3
            mode.set(next)
            toggleBtn.text = when(next){0->"Mode: RAW";1->"Mode: GRAY";else->"Mode: EDGE"}
        }
        saveBtn.setOnClickListener {
            saveNext.set(true)
            Toast.makeText(this, "Will save next frame…", Toast.LENGTH_SHORT).show()
        }

        inBuffer = ByteBuffer.allocateDirect(size.width*size.height*3/2)
        outBuffer = ByteBuffer.allocateDirect(size.width*size.height*4)
        ctx = native.init(size.width, size.height)

        ensureCameraPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        cam.stop()
        native.release(ctx)
    }

    private fun ensureCameraPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPerms.launch(Manifest.permission.CAMERA)
        } else startCamera()
    }

    private fun startCamera() {
        cam = Camera2Manager(this, size) { reader ->
            val image = reader.acquireLatestImage() ?: return@Camera2Manager
            processImage(image)
            image.close()
        }
        cam.start()
    }

    private fun processImage(image: Image) {
        if (image.format != ImageFormat.YUV_420_888) return
        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]
        val w = image.width; val h = image.height
        if (w != size.width || h != size.height) return
        inBuffer.position(0)
        // Copy Y
        copyPlane(yPlane, w, h, inBuffer)
        // Interleave VU for NV21
        val chromaHeight = h/2
        val chromaWidth = w/2
        val uBuf = uPlane.buffer; val vBuf = vPlane.buffer
        val uRowStride = uPlane.rowStride; val vRowStride = vPlane.rowStride
        val uPixStride = uPlane.pixelStride; val vPixStride = vPlane.pixelStride
        for (row in 0 until chromaHeight) {
            val uRow = row * uRowStride
            val vRow = row * vRowStride
            for (col in 0 until chromaWidth) {
                val u = uBuf.get(uRow + col * uPixStride)
                val v = vBuf.get(vRow + col * vPixStride)
                inBuffer.put(v) // V first
                inBuffer.put(u) // then U -> NV21
            }
        }
        outBuffer.position(0)
        native.processNV21ToRGBA(ctx, inBuffer, w, h, outBuffer, mode.get())
        renderer.updateFrame(outBuffer)

        if (saveNext.getAndSet(false)) {
            saveFramePng(outBuffer, w, h)
        }

        frames++
        val now = System.currentTimeMillis()
        if (now - lastTime >= 1000) {
            fpsText.text = "FPS: $frames  ${w}x${h}"
            frames = 0
            lastTime = now
        }
        glView.requestRender()
    }

    private fun copyPlane(plane: Image.Plane, width: Int, height: Int, out: ByteBuffer) {
        val buf = plane.buffer
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride
        val row = ByteArray(width)
        for (r in 0 until height) {
            val rowStart = r * rowStride
            if (pixelStride == 1) {
                buf.position(rowStart)
                buf.get(row, 0, width)
                out.put(row)
            } else {
                for (c in 0 until width) {
                    val index = rowStart + c * pixelStride
                    out.put(buf.get(index))
                }
            }
        }
    }

    private fun saveFramePng(rgba: ByteBuffer, w: Int, h: Int) {
        // Convert RGBA -> ARGB for Bitmap
        val bytes = ByteArray(w*h*4)
        val dup = rgba.duplicate()
        dup.position(0)
        dup.get(bytes)
        val argb = IntArray(w*h)
        var bi = 0
        var pi = 0
        while (bi < bytes.size) {
            val r = bytes[bi].toInt() and 0xFF
            val g = bytes[bi+1].toInt() and 0xFF
            val b = bytes[bi+2].toInt() and 0xFF
            val a = bytes[bi+3].toInt() and 0xFF
            argb[pi++] = (a shl 24) or (r shl 16) or (g shl 8) or b
            bi += 4
        }
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bmp.setPixels(argb, 0, w, 0, 0, w, h)
        val outFile = File(filesDir, "edgeview_frame.png")
        outFile.outputStream().use { os ->
            bmp.compress(Bitmap.CompressFormat.PNG, 100, os)
        }
        runOnUiThread {
            Toast.makeText(this, "Saved: ${outFile.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }
}
