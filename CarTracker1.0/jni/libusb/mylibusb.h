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

#include <setjmp.h>

#include <libusb/libusb.h>

#define  LOG_TAG    "bigdog"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

JNIEXPORT void Java_com_cartracker_mobile_android_util_libUsb_LibUsbConnector_ListUsbDevices(JNIEnv*, jobject, jobjectArray);