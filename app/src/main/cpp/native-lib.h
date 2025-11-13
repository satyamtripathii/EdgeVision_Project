#pragma once
#include <jni.h>
#include <cstdint>

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_edgeview_NativeBridge_init(JNIEnv* env, jobject thiz, jint width, jint height);

extern "C" JNIEXPORT void JNICALL
Java_com_example_edgeview_NativeBridge_release(JNIEnv* env, jobject thiz, jlong ctxPtr);

extern "C" JNIEXPORT jint JNICALL
Java_com_example_edgeview_NativeBridge_processNV21ToRGBA(JNIEnv* env, jobject thiz,
                                                         jlong ctxPtr,
                                                         jobject inNV21Direct,
                                                         jint width, jint height,
                                                         jobject outRGBADirect,
                                                         jint mode /*0=raw,1=gray,2=edge*/);
