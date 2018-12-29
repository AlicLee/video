LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)

LOCAL_CPP_EXTEMSION:=.c
LOCAL_SRC_FILES :=\
RtmpClient.c      \
RtmpMuxer.c       \
rtmp_flvmuxer.c   \
include/amf.c     \
include/hashswf.c \
include/log.c     \
include/parseurl.c\
include/rtmp.c    \

LOCAL_MODULE:=librtmp

LOCAL_CFLAGS:=
LOCAL_C_INCLUDES+=$(LOCAL_PATH)/include
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog

include $(BUILD_SHARED_LIBRARY)