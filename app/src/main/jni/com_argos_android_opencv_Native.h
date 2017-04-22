#include <jni.h>

#ifndef _Included_com_argos_android_opencv_Native
#define _Included_com_argos_android_opencv_Native
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_argos_android_opencv_Native_convertGray
  (JNIEnv *, jclass, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif
