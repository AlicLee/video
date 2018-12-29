//package com.example.lee.livesdk;
//
//import android.media.MediaCodecInfo;
//import android.media.MediaCodecList;
//import android.util.Log;
//
//public class SrsUtils {
//    private final String  TAG=SrsUtils.class.getSimpleName();
//    private int mVideoColorFormat=-1;//支持的colorFormat
//    private MediaCodecInfo mediaCodecInfo;//支持mediaCodecInfo
//
//    public static SrsUtils getInstances(){
//        return SrsUtilsBuilder.INSTANCE;
//    }
//    public int getVideoColorFormat(){
//        mediaCodecInfo=chooseVideoMediaInfo(null);
//        if(mediaCodecInfo==null)
//            return mVideoColorFormat;
//        int mVideoColorFormat=0;
//        MediaCodecInfo.CodecCapabilities cc=mediaCodecInfo.getCapabilitiesForType(SrsConfig.VCODEC);
//        for(int i=0;i<cc.colorFormats.length;i++){
//            int cf=cc.colorFormats[i];
//            Log.i(TAG, String.format("vencoder %s supports color fomart 0x%x(%d)", mediaCodecInfo.getName(), cf, cf));
//            if(cf>= MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar &&cf<= MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar){
//                if(cf>mVideoColorFormat){
//                    mVideoColorFormat=cf;
//                }
//            }
//        }
//        for(int i=0;i<cc.profileLevels.length;i++){
//            MediaCodecInfo.CodecProfileLevel pl=cc.profileLevels[i];
//            Log.i(TAG, String.format("vencoder %s support profile %d, level %d", mediaCodecInfo.getName(), pl.profile, pl.level));
//        }
//        Log.i(TAG, String.format("vencoder %s choose color format 0x%x(%d)", mediaCodecInfo.getName(), matchedColorFormat, matchedColorFormat));
//
//        return mVideoColorFormat;
//    }
//
//    public int getmVideoColorFormat() {
//        return mVideoColorFormat;
//    }
//
//    public void setmVideoColorFormat(int mVideoColorFormat) {
//        this.mVideoColorFormat = mVideoColorFormat;
//    }
//
//    public MediaCodecInfo getMediaCodecInfo() {
//        return mediaCodecInfo;
//    }
//
//    public void setMediaCodecInfo(MediaCodecInfo mediaCodecInfo) {
//        this.mediaCodecInfo = mediaCodecInfo;
//    }
//
//    /**
//     * 获取mediacodecinfo
//     * @param name 根据名字获取
//     * @return mideocodeinfo
//     */
//    public MediaCodecInfo chooseVideoMediaInfo(String name) {
//        int nbCodecs = MediaCodecList.getCodecCount();
//        for (int i = 0; i < nbCodecs; i++) {
//             mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
//            if (!mediaCodecInfo.isEncoder()) {
//                continue;
//            }
//            String[] types = mediaCodecInfo.getSupportedTypes();
//            for (int j = 0; j < types.length; j++) {
//                if (types[j].equalsIgnoreCase(SrsConfig.VCODEC)) {
//                    Log.i(TAG, String.format("vencoder %s types: %s", mediaCodecInfo.getName(), types[j]));
//                    if (name == null) {
//                        return mediaCodecInfo;
//                    }
//
//                    if (mediaCodecInfo.getName().contains(name)) {
//                        return mediaCodecInfo;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    private static class SrsUtilsBuilder{
//        static final SrsUtils INSTANCE=new SrsUtils();
//    }
//}
