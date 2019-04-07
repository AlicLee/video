package com.example.lee.videoandroid.view.video;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class ProgressRequestBody extends RequestBody {
    private ProgressListener listener;

    protected RequestBody mDelegate;
    protected CountingSink mCountingSink;

    public ProgressRequestBody(RequestBody body, ProgressListener listener) {
        this.mDelegate = body;
        this.listener = listener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mDelegate.contentType();
    }
    @Override
    public long contentLength() {
        try {
            return mDelegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        mCountingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(mCountingSink);
        mDelegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            listener.progress((int) (1000F * bytesWritten / contentLength()));
        }
    }


}
