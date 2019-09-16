package com.opengarden.firechat.preference;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.MyUser;
import com.opengarden.firechat.util.VectorUtils;

public class UserAvatarPreference extends EditTextPreference {
    ImageView mAvatarView;
    Context mContext;
    private ProgressBar mLoadingProgressBar;
    MXSession mSession;

    /* access modifiers changed from: protected */
    public void showDialog(Bundle bundle) {
    }

    public UserAvatarPreference(Context context) {
        super(context);
        this.mContext = context;
    }

    public UserAvatarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public UserAvatarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public View onCreateView(ViewGroup viewGroup) {
        setWidgetLayoutResource(C1299R.layout.vector_settings_round_avatar);
        View onCreateView = super.onCreateView(viewGroup);
        this.mAvatarView = (ImageView) onCreateView.findViewById(C1299R.C1301id.avatar_img);
        this.mLoadingProgressBar = (ProgressBar) onCreateView.findViewById(C1299R.C1301id.avatar_update_progress_bar);
        refreshAvatar();
        return onCreateView;
    }

    public void refreshAvatar() {
        if (this.mAvatarView != null && this.mSession != null) {
            MyUser myUser = this.mSession.getMyUser();
            VectorUtils.loadUserAvatar(this.mContext, this.mSession, this.mAvatarView, myUser.getAvatarUrl(), myUser.user_id, myUser.displayname);
        }
    }

    public void setSession(MXSession mXSession) {
        this.mSession = mXSession;
        refreshAvatar();
    }
}
