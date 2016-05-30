#include "mylibusb.h"
#include <stdio.h>
#include <sys/types.h>

libusb_device **devs;

static void print_devs(libusb_device **devs)
{
	libusb_device *dev;
	int i = 0;

	while ((dev = devs[i++]) != NULL) {
		struct libusb_device_descriptor desc;
		int r = libusb_get_device_descriptor(dev, &desc);
		if (r < 0) {
			fprintf(stderr, "failed to get device descriptor");
			return;
		}

		printf("%04x:%04x (bus %d, device %d)\n",
			desc.idVendor, desc.idProduct,
			libusb_get_bus_number(dev), libusb_get_device_address(dev));
		LOGE("%04x:%04x (bus %d, device-biggod %d)",
             			desc.idVendor, desc.idProduct,
             			libusb_get_bus_number(dev), libusb_get_device_address(dev));
	}
}

void usbLists(){
	int r;
	ssize_t cnt;

	r = libusb_init(NULL);
	LOGD("libusb_init result:%d",r);
	if (r < 0)
		return ;

	cnt = libusb_get_device_list(NULL, &devs);
	LOGD("libusb_get_device_list result:%d",cnt);
	if (cnt < 0)
		return ;

	print_devs(devs);
	libusb_free_device_list(devs, 1);

	libusb_exit(NULL);
}

void Java_com_cartracker_mobile_android_util_libUsb_LibUsbConnector_ListUsbDevices(JNIEnv* env, jobject thiz, jobjectArray devices){
      LOGD("libusb init........");

      usbLists();
      //get the usb devices from system
      //jstring * dev_item;
}