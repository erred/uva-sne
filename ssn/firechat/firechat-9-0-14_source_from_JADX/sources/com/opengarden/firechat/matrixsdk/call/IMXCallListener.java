package com.opengarden.firechat.matrixsdk.call;

import android.view.View;

public interface IMXCallListener {
    void onCallAnsweredElsewhere();

    void onCallEnd(int i);

    void onCallError(String str);

    void onCallViewCreated(View view);

    void onPreviewSizeChanged(int i, int i2);

    void onReady();

    void onStateDidChange(String str);
}
