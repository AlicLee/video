package com.example.lee.livesdk;


/**
 * Created by leo.ma on 2016/11/4.
 */

public class SrsEncodeHandler extends SrsHandler {

//    private WeakReference<SrsHandlerListener> mWeakListener;

    public SrsEncodeHandler(SrsHandlerListener listener) {
        super(listener);
//        mWeakListener = new WeakReference<>(listener);
    }

    public void notifyNetworkWeak() {
        sendEmptyMessage(MSG_ENCODE_NETWORK_WEAK);
    }

    public void notifyNetworkResume() {
        sendEmptyMessage(MSG_ENCODE_NETWORK_RESUME);
    }

    public void notifyEncodeIllegalArgumentException(IllegalArgumentException e) {
        obtainMessage(MSG_ENCODE_ILLEGAL_ARGUMENT_EXCEPTION, e).sendToTarget();
    }

}
