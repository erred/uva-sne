package com.opengarden.firechat.activity;

import android.support.annotation.UiThread;
import android.view.View;
import android.webkit.WebView;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class AbstractWidgetActivity_ViewBinding extends RiotAppCompatActivity_ViewBinding {
    private AbstractWidgetActivity target;

    @UiThread
    public AbstractWidgetActivity_ViewBinding(AbstractWidgetActivity abstractWidgetActivity) {
        this(abstractWidgetActivity, abstractWidgetActivity.getWindow().getDecorView());
    }

    @UiThread
    public AbstractWidgetActivity_ViewBinding(AbstractWidgetActivity abstractWidgetActivity, View view) {
        super(abstractWidgetActivity, view);
        this.target = abstractWidgetActivity;
        abstractWidgetActivity.mWebView = (WebView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.widget_webview, "field 'mWebView'", WebView.class);
    }

    public void unbind() {
        AbstractWidgetActivity abstractWidgetActivity = this.target;
        if (abstractWidgetActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        abstractWidgetActivity.mWebView = null;
        super.unbind();
    }
}
