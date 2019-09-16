package com.opengarden.firechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.opengarden.firechat.C1299R;

public class VideoRecordView extends RelativeLayout {
    private VideoRecordProgressView mVideoRecordProgressView;

    public VideoRecordView(Context context) {
        super(context);
        initView();
    }

    public VideoRecordView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public VideoRecordView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), C1299R.layout.video_record_view, this);
        this.mVideoRecordProgressView = (VideoRecordProgressView) findViewById(C1299R.C1301id.video_record_progress_view);
    }

    public void startAnimation() {
        this.mVideoRecordProgressView.startAnimation();
    }

    private void stopAnimation() {
        this.mVideoRecordProgressView.stopAnimation();
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (8 == i || 4 == i) {
            stopAnimation();
        }
    }
}
