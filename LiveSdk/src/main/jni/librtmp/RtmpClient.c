//
// Created by lisaipeng on 2018/12/10.
//

#include "include/rtmp.h"
#include "RtmpClient.h"
#include "include/bytes.h"
#include <jni.h>
#include <malloc.h>

/*
 * Class:     com_example_lee_livesdk_RtmpClient
 * Method:    nativeAlloc
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_example_lee_livesdk_RtmpClient_nativeAlloc
        (JNIEnv *jniEnv, jobject jobject) {
    RTMP *rtmp = RTMP_Alloc();
    if (rtmp == NULL) {
        return -1;
    }
    return (long) rtmp;
}

/*
 * Class:     com_example_lee_livesdk_RtmpClient
 * Method:    nativeOpen
 * Signature: (Ljava/lang/String;ZJ)I
 */
JNIEXPORT jint JNICALL Java_com_example_lee_livesdk_RtmpClient_nativeOpen
        (JNIEnv *jniEnv, jobject jobject, jstring url, jboolean isPublishMode, jlong rtmpPoint) {
    const char *charUrl = (*jniEnv)->GetStringChars(jniEnv, url, 0);
    RTMP *rtmp = (RTMP *) rtmpPoint;
    if(rtmp==NULL){
        return -1;
    }
    RTMP_Init(rtmp);
    int ret=RTMP_SetupURL(rtmp,charUrl);
    if(!ret){
        RTMP_Free(rtmp);
        return -2;
    }
    if(isPublishMode){
        RTMP_EnableWrite(rtmp);
    }
    ret=RTMP_Connect(rtmp,NULL);
    if(!ret){
        RTMP_Free(rtmp);
        return -3;
    }
    ret=RTMP_ConnectStream(rtmp,0);
    if(!ret){
        return -4;
    }
    (*jniEnv)->ReleaseStringUTFChars(jniEnv,url,charUrl);
    return 1;
}

/*
 * Class:     com_example_lee_livesdk_RtmpClient
 * Method:    nativeRead
 * Signature: ([BIIJ)I
 */
JNIEXPORT jint JNICALL Java_com_example_lee_livesdk_RtmpClient_nativeRead
        (JNIEnv * env, jobject thiz, jbyteArray data_, jint offset, jint size, jlong rtmpPointer) {
    RTMP *rtmp = (RTMP *) rtmpPointer;
    if (rtmp == NULL) {
        throwIOException(env, "First call open function");
    }
    int connected = RTMP_IsConnected(rtmp);
    if (!connected) {
        throwIOException(env, "Connection to server is lost");
    }
    char* data = malloc(size*sizeof(char));
    int readCount = RTMP_Read(rtmp, data, size);
    if (readCount > 0) {
        (*env)->SetByteArrayRegion(env, data_, offset, readCount, data);  // copy
    }
    free(data);
    if (readCount == 0) {
        return -1;
    }
    return readCount;
}

/*
 * Class:     com_example_lee_livesdk_RtmpClient
 * Method:    nativeWrite
 * Signature: ([BJ)I
 */
JNIEXPORT jint JNICALL Java_com_example_lee_livesdk_RtmpClient_nativeWrite
        (JNIEnv * env, jobject thiz, jcharArray data, jint size, jlong rtmpPointer) {

    RTMP *rtmp = (RTMP *) rtmpPointer;
    if (rtmp == NULL) {
        throwIOException(env, "First call open function");
    }

    int connected = RTMP_IsConnected(rtmp);
    if (!connected) {
        throwIOException(env, "Connection to server is lost");
    }

    return RTMP_Write(rtmp, data, size);
}

/*
 * Class:     com_example_lee_livesdk_RtmpClient
 * Method:    nativePause
 * Signature: (ZJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lee_livesdk_RtmpClient_nativePause
        (JNIEnv * env, jobject thiz, jboolean pause, jlong rtmpPointer) {

    RTMP *rtmp = (RTMP *) rtmpPointer;
    if (rtmp == NULL) {
        throwIOException(env, "First call open function");
    }

    int DoPause = 0;
    if (pause == JNI_TRUE) {
        DoPause = 1;
    }
    return (jboolean) RTMP_Pause(rtmp, DoPause);
}
/*
 * Class:     com_example_lee_livesdk_RtmpClient
 * Method:    nativeIsConnected
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lee_livesdk_RtmpClient_nativeIsConnected
        (JNIEnv *env, jobject instance, jlong rtmpPointer)
{
    RTMP *rtmp = (RTMP *) rtmpPointer;
    if (rtmp == NULL) {
        return 0;
    }
    int connected = RTMP_IsConnected(rtmp);
    if (connected) {
        return 1;
    }
    else {
        return 0;
    }
}

/*
 * Class:     com_example_lee_livesdk_RtmpClient
 * Method:    nativeClose
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_example_lee_livesdk_RtmpClient_nativeClose
        (JNIEnv * env, jobject thiz, jlong rtmpPointer) {
    RTMP *rtmp = (RTMP *) rtmpPointer;
    if (rtmp != NULL) {
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
    }
}

jint throwIOException (JNIEnv *env, char *message )
{
    jclass Exception = (*env)->FindClass(env, "java/io/IOException");
    return (*env)->ThrowNew(env, Exception, message);
}