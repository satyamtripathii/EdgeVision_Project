package com.example.edgeview.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface

class Camera2Manager(
    private val context: Context,
    private val size: Size,
    private val onFrame: (ImageReader) -> Unit
) {
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private var bgThread: HandlerThread? = null
    private var bgHandler: Handler? = null

    @SuppressLint("MissingPermission")
    fun start() {
        stop()
        bgThread = HandlerThread("cam-bg").also { it.start() }
        bgHandler = Handler(bgThread!!.looper)

        imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.YUV_420_888, 2)
        imageReader!!.setOnImageAvailableListener({ onFrame(imageReader!!) }, bgHandler)

        val camMgr = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val camId = camMgr.cameraIdList.firstOrNull { id ->
            val chars = camMgr.getCameraCharacteristics(id)
            val facing = chars.get(CameraCharacteristics.LENS_FACING)
            facing == CameraCharacteristics.LENS_FACING_BACK
        } ?: camMgr.cameraIdList.first()

        camMgr.openCamera(camId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                createSession()
            }
            override fun onDisconnected(camera: CameraDevice) { camera.close() }
            override fun onError(camera: CameraDevice, error: Int) { camera.close() }
        }, bgHandler)
    }

    private fun createSession() {
        val device = cameraDevice ?: return
        val surface = imageReader!!.surface
        device.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                captureSession = session
                val req = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                    addTarget(surface)
                    set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                    set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
                }
                session.setRepeatingRequest(req.build(), null, bgHandler)
            }
            override fun onConfigureFailed(session: CameraCaptureSession) {}
        }, bgHandler)
    }

    fun stop() {
        captureSession?.close(); captureSession = null
        cameraDevice?.close(); cameraDevice = null
        imageReader?.close(); imageReader = null
        bgThread?.quitSafely(); bgThread = null; bgHandler = null
    }

    fun getImageReader(): ImageReader? = imageReader
}
