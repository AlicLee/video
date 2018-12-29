package com.example.lee.livesdk;

public interface SrsHandlerListener {

    void onNetworkWeak();

    void onNetworkResume();

    void onEncodeIllegalArgumentException(IllegalArgumentException e);
}
