#jscese add this android.mk to compile libusb.so for usb_modelswitch 140819
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

common_src :=\
	libusb/core.c \
	mylibusb.c \
	libusb/descriptor.c \
	libusb/io.c \
	libusb/sync.c \
	libusb/os/linux_usbfs.c \
	
common_include :=\
	$(LOCAL_PATH)/ \
	$(LOCAL_PATH)/libusb \
	$(LOCAL_PATH)/libusb/os
	
LOCAL_MODULE := libusb
LOCAL_SRC_FILES :=$(common_src)
LOCAL_LDLIBS    := -llog -ljnigraphics

LOCAL_C_INCLUDES +=$(common_include)
include $(BUILD_SHARED_LIBRARY)

