#include "native-lib.h"
#include <android/log.h>
#include <cstring>
#include <vector>
#include <chrono>

#define LOG_TAG "NativeProc"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

struct ProcContext {
    int w{0};
    int h{0};
};

#ifdef USE_OPENCV
#include <opencv2/imgproc.hpp>
#include <opencv2/core.hpp>
#endif

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_edgeview_NativeBridge_init(JNIEnv* env, jobject, jint width, jint height) {
    auto* ctx = new ProcContext();
    ctx->w = width; ctx->h = height;
    LOGI("Init context %dx%d", ctx->w, ctx->h);
    return reinterpret_cast<jlong>(ctx);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_edgeview_NativeBridge_release(JNIEnv*, jobject, jlong ctxPtr) {
    auto* ctx = reinterpret_cast<ProcContext*>(ctxPtr);
    delete ctx;
}

static inline void nv21_to_rgba_naive(uint8_t* nv21, int w, int h, uint8_t* out) {
    // Very naive grayscale from Y plane -> RGBA
    const int y_plane = w * h;
    for (int i = 0; i < y_plane; ++i) {
        uint8_t y = nv21[i];
        out[4*i + 0] = y;
        out[4*i + 1] = y;
        out[4*i + 2] = y;
        out[4*i + 3] = 255;
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_com_example_edgeview_NativeBridge_processNV21ToRGBA(JNIEnv* env, jobject,
                                                         jlong ctxPtr,
                                                         jobject inNV21Direct,
                                                         jint width, jint height,
                                                         jobject outRGBADirect,
                                                         jint mode) {
    auto* ctx = reinterpret_cast<ProcContext*>(ctxPtr);
    if (!ctx) return -1;
    auto* inPtr = static_cast<uint8_t*>(env->GetDirectBufferAddress(inNV21Direct));
    auto* outPtr = static_cast<uint8_t*>(env->GetDirectBufferAddress(outRGBADirect));
    if (!inPtr || !outPtr) return -2;

    int w = width; int h = height;

#ifdef USE_OPENCV
    // Build NV21 Mat and convert
    cv::Mat yuv(h + h/2, w, CV_8UC1, inPtr);
    cv::Mat bgr;
    cv::cvtColor(yuv, bgr, cv::COLOR_YUV2BGR_NV21);

    cv::Mat out;
    if (mode == 2) {
        cv::Mat edges; cv::Canny(bgr, edges, 80, 160);
        cv::cvtColor(edges, out, cv::COLOR_GRAY2RGBA);
    } else if (mode == 1) {
        cv::Mat gray; cv::cvtColor(bgr, gray, cv::COLOR_BGR2GRAY);
        cv::cvtColor(gray, out, cv::COLOR_GRAY2RGBA);
    } else {
        cv::cvtColor(bgr, out, cv::COLOR_BGR2RGBA);
    }

    if (out.cols != w || out.rows != h || out.type() != CV_8UC4) {
        return -3;
    }
    std::memcpy(outPtr, out.data, w*h*4);
#else
    // Fallback: just grayscale from Y plane
    nv21_to_rgba_naive(inPtr, w, h, outPtr);
#endif
    return 0;
}
