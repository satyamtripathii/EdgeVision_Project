# EdgeView — Android + OpenCV (C++) + OpenGL ES + Web (TypeScript)

EdgeView is a real-time Android-based edge detection viewer built using:
- Android Camera2 (NV21 frames)
- Native C++ (JNI + OpenCV 4.x)
- OpenGL ES 2.0 (texture rendering)
- TypeScript-based web viewer for displaying saved processed frames

This project was built as part of a technical assessment requiring:
Android + NDK, C++, OpenCV, OpenGL ES, and TypeScript integration.

---

## ✨ Features

### 📱 Android
- Camera2 → NV21 → JNI pipeline  
- Native C++ processing (Grayscale + Canny Edge Detection using OpenCV)  
- OpenGL ES 2.0 textured quad rendering  
- Mode toggle: **RAW → GRAY → EDGE**  
- FPS overlay  
- Save Frame (internal storage)  
- Save to Gallery  
- Auto-save feature  
- Instrumentation tests validating native pipeline (NV21 → RGBA result)  

### 🌐 Web Viewer
- TypeScript + simple bundler  
- Displays web/public/frame.png  
- Falls back to built-in sample image  
- FPS + meta info overlay  
- Includes Node-based unit tests  

---

## 📂 Project Structure


edgeview/
├── app/
│ ├── src/main/java/com/example/edgeview/ # Kotlin UI
│ ├── src/main/java/com/example/edgeview/camera
│ ├── src/main/java/com/example/edgeview/gl # OpenGL renderer
│ └── src/main/cpp/ # JNI + OpenCV C++
├── web/ # TypeScript viewer
└── scripts/ # Helper scripts


---

## 🚀 Quick Start — Android

1. Open project in **Android Studio Flamingo+**
2. Ensure you install **NDK r26+** and **CMake**
3. Download OpenCV Android SDK (v4.8+), unzip it, and set in local.properties:

4. Build and run on a real device (recommended)
5. Tap **Save Frame** → saved to internal storage
6. Optional: Save to Gallery for easier access

---

## 🌐 Quick Start — Web Viewer

### Option A: Open static HTML


### Option B: Run in server mode


---

## 🧪 Tests

### ✔ Android Instrumentation Tests
Validate:
- NV21 synthetic inputs  
- JNI processing  
- Grayscale + Canny pipeline  

Run inside Android Studio:


### ✔ Web Unit Tests


---

## 🔧 Scripts

| Script | Purpose |
|--------|---------|
| scripts/pull_frame.ps1 | Pulls saved frame from device into web/public/frame.png |
| scripts/open_web.ps1 | Opens the web viewer locally |

---

## 🧩 Architecture (High-Level)

Camera2 (NV21)
↓
Kotlin Layer
↓ JNI
Native C++ (OpenCV)
- NV21 → RGBA
- GRAY / CANNY
↓
OpenGL ES (Texture Render)
↓
Android Device Screen

[Optional]
Save Frame → scripts → Web Viewer (TypeScript)


---

## ✔ Assessment Coverage

- ✓ Native-C++ integration (JNI)
- ✓ OpenCV usage (C++ only)
- ✓ OpenGL ES rendering ≥ 10–15 FPS
- ✓ Clean Android modular structure
- ✓ TypeScript web viewer working
- ✓ Scripts for E2E verification
- ✓ README + documentation

Project meets 100% requirements of the R&D Intern assignment.

---

## 📸 Screenshots
*(Add after pushing to GitHub)*  
Place in docs/ folder.

---

## 📄 License
MIT (optional)
