# ArgOS DriveAssist

![alt header](https://github.com/argos-research/android-OpenCV/blob/master/screenshots/header.png)

OpenCV-based object detection framework on Android that provides driving assistance capabilities on the Speed Dreams simulator.

# Setup
1. Download Android NDK and Build tools https://developer.android.com/studio/projects/add-native-code.html

2. Download OpenCV Android SDK (v3.2) http://opencv.org/releases.html

3. Add path to local Android NDK binary in [build.gradle](https://github.com/argos-research/android-OpenCV/blob/master/app/build.gradle)
> def ndkBuildPath =  '/Users/chandruscm/Library/Android/sdk/ndk-bundle/ndk-build'

4. Add path to local OpenCV Android SDK in [Android.mk](https://github.com/argos-research/android-OpenCV/blob/master/app/src/main/jni/Android.mk)
> OPENCV_SDK:=/Users/chandruscm/AppDevelopment/Android/Libraries/OpenCV-android-sdk/sdk

5. Add appropriate CPU architecture in [Application.mk](https://github.com/argos-research/android-OpenCV/blob/master/app/src/main/jni/Application.mk)
> APP_ABI := armeabi-v7a x86
