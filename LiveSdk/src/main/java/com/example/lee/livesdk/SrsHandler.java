package com.example.lee.livesdk;


import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class SrsHandler extends Handler {
    public static final int MSG_ENCODE_NETWORK_WEAK = 0;
    public static final int MSG_ENCODE_NETWORK_RESUME = 1;
    public static final int MSG_ENCODE_ILLEGAL_ARGUMENT_EXCEPTION = 2;

    private WeakReference<SrsHandlerListener> mWeakListener;
    public SrsHandler(SrsHandlerListener listener){
        mWeakListener=new WeakReference<>(listener);
    }
    public abstract void notifyNetworkWeak();

    public abstract void notifyNetworkResume();

    public abstract void notifyEncodeIllegalArgumentException(IllegalArgumentException e);
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        SrsHandlerListener listener = mWeakListener.get();
        if (listener == null) {
            return;
        }
        switch (msg.what) {
            case MSG_ENCODE_NETWORK_WEAK:
                listener.onNetworkWeak();
                break;
            case MSG_ENCODE_NETWORK_RESUME:
                listener.onNetworkResume();
                break;
            case MSG_ENCODE_ILLEGAL_ARGUMENT_EXCEPTION:
                listener.onEncodeIllegalArgumentException((IllegalArgumentException) msg.obj);
            default:
                throw new RuntimeException("unknown msg " + msg.what);
        }
    }
}
