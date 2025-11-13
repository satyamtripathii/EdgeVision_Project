cmake_minimum_required(VERSION 3.22.1)
project(native_processing)

add_library(native-lib SHARED native-lib.cpp)

find_library(log-lib log)
find_library(android-lib android)
find_library(jnigraphics-lib jnigraphics)
find_library(glesv2-lib GLESv2)

# OpenCV optional discovery (set OPENCV_SDK to Android SDK root)
if(DEFINED OPENCV_SDK)
    message(STATUS "Using OpenCV from ${OPENCV_SDK}")
    set(OpenCV_DIR ${OPENCV_SDK}/sdk/native/jni)
    find_package(OpenCV REQUIRED)
    target_include_directories(native-lib PRIVATE ${OpenCV_INCLUDE_DIRS})
    target_link_libraries(native-lib PRIVATE ${OpenCV_LIBS})
    target_compile_definitions(native-lib PRIVATE USE_OPENCV=1)
else()
    message(WARNING "OPENCV_SDK not set. Building with naive fallback (no OpenCV).")
endif()

target_compile_features(native-lib PRIVATE cxx_std_17)

target_link_libraries(
    native-lib
    PRIVATE
    ${log-lib}
    ${android-lib}
    ${jnigraphics-lib}
    ${glesv2-lib}
)
