# EdgeView: Android + OpenCV (C++) + OpenGL ES + Web (TypeScript)

This repository implements the assessment:
- Android app capturing camera frames
- Processing in native C++ via JNI using OpenCV (Canny/Grayscale)
- Rendering via OpenGL ES 2.0 as a texture
- Minimal TypeScript web viewer to display a sample processed frame and stats

Project layout
- app/ (Android app: Kotlin UI + Camera2, JNI bridge)
- app/src/main/cpp (C++ OpenCV processing via NDK + CMake)
- app/src/main/java/com/example/edgeview (Android code)
- app/src/main/java/com/example/edgeview/gl (OpenGL renderer)
- app/src/main/java/com/example/edgeview/camera (Camera2 manager)
- web/ (TypeScript-based demo web viewer)

Prereqs
- Android Studio (Flamingo+), Android SDK, NDK (r26+), CMake
- OpenCV Android SDK (4.8+). Download and unzip, e.g. C:/sdk/opencv-4.8.0-android-sdk
- Java 17 (recommended by recent AGP)
- Node.js (optional; for the web viewer build)

Quick start (Android)
1) Open this project in Android Studio
2) Configure local OpenCV SDK path in `local.properties` (create if missing):
   OPENCV_SDK=C:/sdk/opencv-4.8.0-android-sdk
3) Let Android Studio download the NDK/CMake if prompted
4) Build and run on a device

Notes
- The JNI pipeline expects NV21 frames from Camera2 ImageReader, converts to RGBA, applies grayscale or Canny (mode), and fills an output buffer. The GLSurfaceView uploads this buffer to a texture each frame.
- Toggle button cycles between RAW → GRAY → EDGE.
- Minimal FPS overlay TextView shows approximate render fps.

Quick start (Web)
- Open `web/public/index.html` in a browser directly, or
- Install Node, then: `cd web && npm install && npm run build && npm run serve`

Evaluation checklist
- Native-C++ integration (JNI): C++ in app/src/main/cpp with JNI functions
- OpenCV usage: used in native for color conversion and Canny/gray
- OpenGL rendering: GLSL shaders draw textured quad at 10–15+ FPS
- TypeScript web viewer: `web/` with tsc config, DOM updates, FPS/res text
- Structure/docs/commits: clear modules and README

