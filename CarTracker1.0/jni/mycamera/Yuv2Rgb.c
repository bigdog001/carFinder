#include "Yuv2Rgb.h"

static int yuv_tbl_ready=0;
static int y1192_tbl[256];
static int v1634_tbl[256];
static int v833_tbl[256];
static int u400_tbl[256];
static int u2066_tbl[256];

void yuv422_to_rgb(int *rgb_dst,const unsigned char *yuv_src,
					int width,int height)
{
	int frameSize =width*height*2;
	int i;
	if(!yuv_tbl_ready){
		for(i=0 ; i<256 ; i++){
			y1192_tbl[i] = 1192*(i-16);
			if(y1192_tbl[i]<0){
				y1192_tbl[i]=0;
			}

			v1634_tbl[i] = 1634*(i-128);
			v833_tbl[i] = 833*(i-128);
			u400_tbl[i] = 400*(i-128);
			u2066_tbl[i] = 2066*(i-128);
		}
		yuv_tbl_ready=1;
	}

	for(i=0 ; i<frameSize ; i+=4){
		unsigned char y1, y2, u, v;
		y1 = yuv_src[i];
		u = yuv_src[i+1];
		y2 = yuv_src[i+2];
		v = yuv_src[i+3];

		int y1192_1=y1192_tbl[y1];
		int r1 = (y1192_1 + v1634_tbl[v])>>10;
		int g1 = (y1192_1 - v833_tbl[v] - u400_tbl[u])>>10;
		int b1 = (y1192_1 + u2066_tbl[u])>>10;

		int y1192_2=y1192_tbl[y2];
		int r2 = (y1192_2 + v1634_tbl[v])>>10;
		int g2 = (y1192_2 - v833_tbl[v] - u400_tbl[u])>>10;
		int b2 = (y1192_2 + u2066_tbl[u])>>10;

		r1 = r1>255 ? 255 : r1<0 ? 0 : r1;
		g1 = g1>255 ? 255 : g1<0 ? 0 : g1;
		b1 = b1>255 ? 255 : b1<0 ? 0 : b1;
		r2 = r2>255 ? 255 : r2<0 ? 0 : r2;
		g2 = g2>255 ? 255 : g2<0 ? 0 : g2;
		b2 = b2>255 ? 255 : b2<0 ? 0 : b2;

		*rgb_dst++ = 0xff000000 | b1<<16 | g1<<8 | r1;
		*rgb_dst++ = 0xff000000 | b2<<16 | g2<<8 | r2;
	}
}
