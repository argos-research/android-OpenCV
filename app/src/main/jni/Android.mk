LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OpenCv
OPENCV_SDK:=/Users/chandruscm/AppDevelopment/Android/Libraries/OpenCV-android-sdk/sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED

include $(OPENCV_SDK)/native/jni/OpenCV.mk

LOCAL_MODULE    := NativeArgOS
LOCAL_SRC_FILES := com_argos_android_opencv_Native.cpp
LOCAL_LDLIBS += -llog -ldl -landroid -lGLESv2 -lEGL

include $(BUILD_SHARED_LIBRARY)