package com.opengarden.firechat.fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.VectorGroupDetailsActivity;
import com.opengarden.firechat.matrixsdk.MXSession;

public abstract class GroupDetailsBaseFragment extends VectorBaseFragment {
    private static final String CURRENT_FILTER = "CURRENT_FILTER";
    private static final String LOG_TAG = "GroupDetailsBaseFragment";
    protected VectorGroupDetailsActivity mActivity;
    protected MXSession mSession;

    /* access modifiers changed from: protected */
    public abstract void initViews();

    public abstract void refreshViews();

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mSession = Matrix.getInstance(getContext()).getDefaultSession();
        this.mActivity = (VectorGroupDetailsActivity) getActivity();
        initViews();
    }

    @CallSuper
    public void onDetach() {
        super.onDetach();
        this.mActivity = null;
    }

    public void onResume() {
        super.onResume();
        if (this.mActivity != null) {
            View currentFocus = this.mActivity.getCurrentFocus();
            if (currentFocus != null) {
                ((InputMethodManager) this.mActivity.getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }
}
