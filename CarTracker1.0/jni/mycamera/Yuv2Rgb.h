#ifndef _YUV_2_RGB_H_
#define _YUV_2_RGB_H_

void yuv422_to_rgb(int *rgb_dst,const unsigned char *yuv_src,
					int width,int height);

#endif
