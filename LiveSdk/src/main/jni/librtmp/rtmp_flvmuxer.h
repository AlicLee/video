//
// Created by lisaipeng on 2018/12/10.
//

#ifndef LIVESDK_RTMP_FLVMUXER_H
#define LIVESDK_RTMP_FLVMUXER_H

#include <stdint.h>
#include <stdbool.h>
#include <android/log.h>
#ifdef __cplusplus
extern "C"{
#endif

#define  LOG_TAG    "rtmp-muxer"
#define  LOGD(...)  ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))
#define RTMP_STREAM_PROPERTY_PUBLIC      0x00000001
#define RTMP_STREAM_PROPERTY_ALARM       0x00000002
#define RTMP_STREAM_PROPERTY_RECORD      0x00000004


int rtmp_open_for_write(const char *url, uint32_t video_width, uint32_t video_height);

int rtmp_close();

int rtmp_is_connected();

// @brief send audio frame
// @param [in] rtmp_sender handler
// @param [in] data       : AACAUDIODATA
// @param [in] size       : AACAUDIODATA size
// @param [in] dts_us     : decode timestamp of frame
int rtmp_sender_write_audio_frame(uint8_t *data,
                                  int size,
                                  uint64_t dts_us,
                                  uint32_t abs_ts);

// @brief send video frame, now only H264 supported
// @param [in] rtmp_sender handler
// @param [in] data       : video data, (Full frames are required)
// @param [in] size       : video data size
// @param [in] dts_us     : decode timestamp of frame
// @param [in] key        : key frame indicate, [0: non key] [1: key]
int rtmp_sender_write_video_frame(uint8_t *data,
                                  int size,
                                  uint64_t dts_us,
                                  int key,
                                  uint32_t abs_ts);

int rtmp_read_date(uint8_t* data, int size);

void flv_file_open(const char *filename);

void flv_file_close();

void write_flv_header(bool is_have_audio, bool is_have_video);

#ifdef __cplusplus
}
#endif
#endif //LIVESDK_RTMP_FLVMUXER_H

