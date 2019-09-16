package com.opengarden.firechat.activity;

import android.support.annotation.UiThread;
import android.view.View;
import android.webkit.WebView;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public final class VectorWebViewActivity_ViewBinding extends RiotAppCompatActivity_ViewBinding {
    private VectorWebViewActivity target;

    @UiThread
    public VectorWebViewActivity_ViewBinding(VectorWebViewActivity vectorWebViewActivity) {
        this(vectorWebViewActivity, vectorWebViewActivity.getWindow().getDecorView());
    }

    @UiThread
    public VectorWebViewActivity_ViewBinding(VectorWebViewActivity vectorWebViewActivity, View view) {
        super(vectorWebViewActivity, view);
        this.target = vectorWebViewActivity;
        vectorWebViewActivity.webView = (WebView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.simple_webview, "field 'webView'", WebView.class);
    }

    public void unbind() {
        VectorWebViewActivity vectorWebViewActivity = this.target;
        if (vectorWebViewActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        vectorWebViewActivity.webView = null;
        super.unbind();
    }
}
