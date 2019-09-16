package com.opengarden.firechat.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.util.VectorUtils;

public class VectorGroupPreference extends VectorSwitchPreference {
    private static final String LOG_TAG = "VectorGroupPreference";
    private ImageView mAvatarView;
    private Context mContext;
    private Group mGroup;
    private MXSession mSession;

    public VectorGroupPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public VectorGroupPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    @TargetApi(21)
    public VectorGroupPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context);
    }

    public VectorGroupPreference(Context context) {
        super(context, null);
        init(context);
    }

    /* access modifiers changed from: protected */
    public View onCreateView(ViewGroup viewGroup) {
        View onCreateView = super.onCreateView(viewGroup);
        try {
            ViewParent parent = ((ImageView) onCreateView.findViewById(16908294)).getParent();
            while (parent.getParent() != null) {
                parent = parent.getParent();
            }
            FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(this.mContext).inflate(C1299R.layout.vector_settings_round_group_avatar, null, false);
            this.mAvatarView = (ImageView) frameLayout.findViewById(C1299R.C1301id.avatar_img);
            LayoutParams layoutParams = new LayoutParams(-2, -2);
            layoutParams.gravity = 17;
            frameLayout.setLayoutParams(layoutParams);
            ((LinearLayout) parent).addView(frameLayout, 0);
            refreshAvatar();
        } catch (Exception unused) {
            this.mAvatarView = null;
        }
        return onCreateView;
    }

    public void setGroup(Group group, MXSession mXSession) {
        this.mGroup = group;
        this.mSession = mXSession;
        refreshAvatar();
    }

    public void refreshAvatar() {
        if (this.mAvatarView != null && this.mSession != null && this.mGroup != null) {
            VectorUtils.loadGroupAvatar(this.mContext, this.mSession, this.mAvatarView, this.mGroup);
        }
    }

    private void init(Context context) {
        setWidgetLayoutResource(C1299R.layout.preference_switch_layout);
        this.mContext = context;
    }
}
