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
    @Test
    fun process_nv21_to_rgba_gray_ok() {
        val w = 4; val h = 4
        val native = NativeBridge()
        val ctx = native.init(w, h)
        val inBuf = ByteBuffer.allocateDirect(w*h*3/2)
        // Fill Y plane with mid gray and UV with 128
        for (i in 0 until w*h) inBuf.put(128.toByte())
        for (i in 0 until w*h/2) inBuf.put(128.toByte())
        val out = ByteBuffer.allocateDirect(w*h*4)
        val rc = native.processNV21ToRGBA(ctx, inBuf, w, h, out, 1)
        assertEquals(0, rc)
        out.position(0)
        var sum = 0
        while (out.remaining() > 0) sum += out.get().toInt() and 0xFF
        assertTrue(sum > 0)
        native.release(ctx)
    }
}