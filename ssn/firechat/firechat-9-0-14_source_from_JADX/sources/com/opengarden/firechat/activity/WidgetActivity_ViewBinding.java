package com.opengarden.firechat.activity;

import android.support.annotation.UiThread;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import butterknife.internal.C0487Utils;
import butterknife.internal.DebouncingOnClickListener;
import com.opengarden.firechat.C1299R;

public final class WidgetActivity_ViewBinding extends RiotAppCompatActivity_ViewBinding {
    private WidgetActivity target;
    private View view2131297128;
    private View view2131297129;

    @UiThread
    public WidgetActivity_ViewBinding(WidgetActivity widgetActivity) {
        this(widgetActivity, widgetActivity.getWindow().getDecorView());
    }

    @UiThread
    public WidgetActivity_ViewBinding(final WidgetActivity widgetActivity, View view) {
        super(widgetActivity, view);
        this.target = widgetActivity;
        View findRequiredView = C0487Utils.findRequiredView(view, C1299R.C1301id.widget_close_icon, "field 'mCloseWidgetIcon' and method 'onCloseClick$vector_appfirechatRelease'");
        widgetActivity.mCloseWidgetIcon = findRequiredView;
        this.view2131297129 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                widgetActivity.onCloseClick$vector_appfirechatRelease();
            }
        });
        widgetActivity.mWidgetWebView = (WebView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.widget_web_view, "field 'mWidgetWebView'", WebView.class);
        widgetActivity.mWidgetTypeTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.widget_title, "field 'mWidgetTypeTextView'", TextView.class);
        View findRequiredView2 = C0487Utils.findRequiredView(view, C1299R.C1301id.widget_back_icon, "method 'onBackClicked$vector_appfirechatRelease'");
        this.view2131297128 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                widgetActivity.onBackClicked$vector_appfirechatRelease();
            }
        });
    }

    public void unbind() {
        WidgetActivity widgetActivity = this.target;
        if (widgetActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        widgetActivity.mCloseWidgetIcon = null;
        widgetActivity.mWidgetWebView = null;
        widgetActivity.mWidgetTypeTextView = null;
        this.view2131297129.setOnClickListener(null);
        this.view2131297129 = null;
        this.view2131297128.setOnClickListener(null);
        this.view2131297128 = null;
        super.unbind();
    }
}
