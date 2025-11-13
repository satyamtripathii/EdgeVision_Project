# EdgeVision_Project — Android + OpenCV (C++) + OpenGL ES + Web (TypeScript)

EdgeVision_Project is a real-time Android-based edge detection and visualization project built using:
- Android Camera2 (NV21 frame stream)
- Native C++ (JNI + OpenCV 4.x)
- OpenGL ES 2.0 (texture rendering pipeline)
- TypeScript-based web viewer for displaying saved processed frames

This project was developed as part of an R&D Technical Assessment requiring:
**Android + NDK, C++, OpenCV, OpenGL ES, and TypeScript integration.**

---

## ✨ Features

### 📱 Android App
- Camera2 → NV21 → JNI → Native C++ pipeline  
- Native image processing (Grayscale + Canny Edge Detection using OpenCV)  
- OpenGL ES 2.0 textured quad rendering (real-time)  
- Mode toggle: **RAW → GRAY → EDGE**  
- FPS overlay (runtime performance stats)  
- Save Frame (internal storage)  
- Save to Gallery (via MediaStore)  
- Auto-save mode (every 5 seconds)  
- Android instrumentation tests for native pipeline (NV21 → RGBA validation)

### 🌐 Web Viewer
- TypeScript + simple build setup  
- Displays `web/public/frame.png` as processed output  
- Fallback sample image if no frame is available  
- FPS + metadata overlay  
- Node-based unit tests for utility functions  

---

## 📂 Project Structure

```
EdgeVision_Project/
├── app/
│   ├── src/main/java/com/example/edgevision_project/          # Kotlin UI
│   ├── src/main/java/com/example/edgevision_project/camera    # Camera2 handler
│   ├── src/main/java/com/example/edgevision_project/gl        # OpenGL renderer
│   └── src/main/cpp/                                          # JNI + OpenCV C++
│
├── web/                                                       # TypeScript web viewer
└── scripts/                                                   # Helper scripts
```

---

## 🚀 Quick Start — Android

1. Open the project in **Android Studio Flamingo+**
2. Install required components if prompted:
   - **NDK r26+**
   - **CMake**
3. Download **OpenCV Android SDK (4.8+)**, unzip it, and set in `local.properties`:

```
OPENCV_SDK=C:/sdk/opencv-4.8.0-android-sdk
```

4. Build & run on a real Android device  
5. Press **Save Frame** to export processed frame  
6. (Optional) Press **Save to Gallery** for saving the image into the Pictures directory  
7. Try switching modes: RAW → GRAY → EDGE  

---

## 🌐 Quick Start — Web Viewer

### **Option A: Open static HTML directly**
Open in browser:
```
web/public/index.html
```

### **Option B: Run the viewer with a development server**
```
cd web
npm install
npm run build
npm run serve
```

Now open:
```
http://localhost:5173
```

### **Display a real Android-processed frame**
1. On the Android app → Tap **Save Frame**  
2. Pull the frame:

```
pwsh scripts/pull_frame.ps1
```

3. Open the viewer:

```
pwsh scripts/open_web.ps1
```

4. Refresh the browser → `frame.png` will load.

---

## 🧪 Tests

### ✔ Android Instrumentation Tests
Validates:
- Synthetic NV21 generation  
- JNI + native pipeline correctness  
- Grayscale + Canny output integrity  

Run in Android Studio:
```
Run > androidTest
```

### ✔ Web Unit Tests
```
cd web
npm test
```

---

## 🔧 Scripts

| Script | Purpose |
|--------|---------|
| `scripts/pull_frame.ps1` | Pulls saved frame from device → `web/public/frame.png` |
| `scripts/open_web.ps1` | Opens the local web viewer |

---

## 🧩 Architecture (High-Level)

```
Camera2 (NV21 Frames)
        ↓
    Kotlin Layer
        ↓ JNI
Native C++ (OpenCV)
  - NV21 → RGBA conversion
  - GRAY / CANNY processing
        ↓
OpenGL ES (Texture Rendering)
        ↓
    Android Screen

Optional Path:
Save Frame → scripts → Web Viewer (TypeScript)
```

---

## ✔ Assessment Coverage

- ✓ Native-C++ integration (JNI)  
- ✓ OpenCV usage (C++ only, NDK-based)  
- ✓ OpenGL ES rendering ≥ 10–15 FPS  
- ✓ Clean Android modular structure  
- ✓ Working TypeScript web viewer  
- ✓ Automated tests (web + instrumentation)  
- ✓ Full Documentation (README, scripts, structure)  

**EdgeVision_Project meets all required criteria for the R&D Technical Assessment.**

---

## 📝 Notes
- `local.properties` is intentionally excluded using `.gitignore`  
- OpenCV must be downloaded separately (license-compliant)  
- All native libs are built via NDK during Gradle sync  
- Web viewer supports fallback mode for testing without Android device  

---

## 🧾 Summary
EdgeVision_Project demonstrates a complete real-time mobile vision pipeline:
- **Camera2** frame acquisition  
- **JNI + Native C++** high-performance processing  
- **OpenCV** grayscale and Canny operations  
- **OpenGL ES** rendering at interactive frame rates  
- **Web viewer** for remote validation and debugging  


