package com.example.edgeview

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer

@RunWith(AndroidJUnit4::class)
class EdgeNativeTest {
    private fun makeNv21(w: Int, h: Int, y: Int = 128, uv: Int = 128): ByteBuffer {
        val buf = ByteBuffer.allocateDirect(w*h*3/2)
        for (i in 0 until w*h) buf.put(y.toByte())
        for (i in 0 until w*h/2) buf.put(uv.toByte())
        buf.position(0)
        return buf
    }

    @Test
    fun process_nv21_to_rgba_gray_ok() {
        val w = 4; val h = 4
        val native = NativeBridge()
        val ctx = native.init(w, h)
        val inBuf = makeNv21(w, h)
        val out = ByteBuffer.allocateDirect(w*h*4)
        val rc = native.processNV21ToRGBA(ctx, inBuf, w, h, out, 1)
        assertEquals(0, rc)
        out.position(0)
        var sum = 0
        while (out.remaining() > 0) sum += out.get().toInt() and 0xFF
        assertTrue(sum > 0)
        native.release(ctx)
    }

    @Test
    fun process_nv21_to_rgba_raw_ok() {
        val w = 4; val h = 4
        val native = NativeBridge()
        val ctx = native.init(w, h)
        val inBuf = makeNv21(w, h, 200, 128)
        val out = ByteBuffer.allocateDirect(w*h*4)
        val rc = native.processNV21ToRGBA(ctx, inBuf, w, h, out, 0)
        assertEquals(0, rc)
        out.position(0)
        var max = 0
        while (out.remaining() > 0) { val v = out.get().toInt() and 0xFF; if (v > max) max = v }
        assertTrue(max >= 200)
        native.release(ctx)
    }

    @Test
    fun process_nv21_to_rgba_edge_ok() {
        val w = 4; val h = 4
        val native = NativeBridge()
        val ctx = native.init(w, h)
        // Create a simple gradient that should yield some edges
        val buf = ByteBuffer.allocateDirect(w*h*3/2)
        for (y in 0 until h) {
            for (x in 0 until w) buf.put(((x+y)%256).toByte())
        }
        for (i in 0 until w*h/2) buf.put(128.toByte())
        buf.position(0)
        val out = ByteBuffer.allocateDirect(w*h*4)
        val rc = native.processNV21ToRGBA(ctx, buf, w, h, out, 2)
        assertEquals(0, rc)
        out.position(0)
        var sum = 0
        while (out.remaining() > 0) sum += out.get().toInt() and 0xFF
        assertTrue(sum > 0)
        native.release(ctx)
    }
}
