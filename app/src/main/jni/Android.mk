LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OpenCv
OPENCV_SDK:=/Users/patriccorletto/Documents/uni/Bachelor/android_studio/android-OpenCV/sdkopencv34
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED

include $(OPENCV_SDK)/native/jni/OpenCV.mk

LOCAL_MODULE    := NativeArgOS
LOCAL_SRC_FILES := com_argos_android_opencv_driving_AutoDrive.cpp
LOCAL_LDLIBS += -llog -ldl -landroid -lGLESv2 -lEGL

include $(BUILD_SHARED_LIBRARY)