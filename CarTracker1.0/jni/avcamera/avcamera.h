#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <string.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <fcntl.h>              /* low-level i/o */
#include <unistd.h>
#include <errno.h>
#include <malloc.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/mman.h>
#include <sys/ioctl.h>

#include <asm/types.h>          /* for videodev2.h */

#include <linux/videodev2.h>

#include <jpeglib.h>
#include <jerror.h>
#include <setjmp.h>


#define  LOG_TAG    "avcamera"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#define IMG_WIDTH 640
#define IMG_HEIGHT 480


jint Java_com_av_camera_android_AvCameraUtil_prepareCamera(JNIEnv* env,jobject thiz, jint videoid);
jint Java_com_av_camera_android_CameraUtil_checkCamera(JNIEnv* env,jobject thiz);
void Java_com_av_camera_android_CameraUtil_takeImage(JNIEnv* env,jobject thiz, jobject bitmap1, jobject bitmap2,jint videoflag);
void Java_com_av_camera_android_CameraUtil_stopCamera(JNIEnv* env,jobject thiz);
