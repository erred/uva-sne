package com.opengarden.firechat.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.p000v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.util.RoomUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UnreadCounterBadgeView extends RelativeLayout {
    public static final int DEFAULT = 2;
    public static final int HIGHLIGHTED = 0;
    public static final int NOTIFIED = 1;
    private TextView mCounterTextView;
    private View mParentView;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    public UnreadCounterBadgeView(Context context) {
        super(context);
        init();
    }

    public UnreadCounterBadgeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public UnreadCounterBadgeView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        inflate(getContext(), C1299R.layout.unread_counter_badge, this);
        this.mCounterTextView = (TextView) findViewById(C1299R.C1301id.unread_counter_badge_text_view);
        this.mParentView = findViewById(C1299R.C1301id.unread_counter_badge_layout);
    }

    public void updateCounter(int i, int i2) {
        updateText(RoomUtils.formatUnreadMessagesCounter(i), i2);
    }

    public void updateText(String str, int i) {
        if (!TextUtils.isEmpty(str)) {
            this.mCounterTextView.setText(str);
            setVisibility(0);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(0);
            gradientDrawable.setCornerRadius(100.0f);
            if (i == 0) {
                gradientDrawable.setColor(ContextCompat.getColor(getContext(), C1299R.color.vector_fuchsia_color));
            } else if (i == 1) {
                gradientDrawable.setColor(ContextCompat.getColor(getContext(), C1299R.color.vector_green_color));
            } else {
                gradientDrawable.setColor(ContextCompat.getColor(getContext(), C1299R.color.vector_silver_color));
            }
            this.mParentView.setBackground(gradientDrawable);
            return;
        }
        setVisibility(8);
    }
}
