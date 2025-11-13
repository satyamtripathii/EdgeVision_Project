package com.example.edgeview

class NativeBridge {
    init {
        System.loadLibrary("native-lib")
    }

    external fun init(width: Int, height: Int): Long
    external fun release(ctx: Long)
    external fun processNV21ToRGBA(
        ctx: Long,
        inNV21: java.nio.ByteBuffer,
        width: Int,
        height: Int,
        outRGBA: java.nio.ByteBuffer,
        mode: Int
    ): Int
}
