package com.opengarden.firechat.view;

import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import butterknife.internal.DebouncingOnClickListener;
import com.opengarden.firechat.C1299R;

public final class UrlPreviewView_ViewBinding implements Unbinder {
    private UrlPreviewView target;
    private View view2131297111;

    @UiThread
    public UrlPreviewView_ViewBinding(UrlPreviewView urlPreviewView) {
        this(urlPreviewView, urlPreviewView);
    }

    @UiThread
    public UrlPreviewView_ViewBinding(final UrlPreviewView urlPreviewView, View view) {
        this.target = urlPreviewView;
        urlPreviewView.mImageView = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.url_preview_image_view, "field 'mImageView'", ImageView.class);
        urlPreviewView.mTitleTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.url_preview_title_text_view, "field 'mTitleTextView'", TextView.class);
        urlPreviewView.mDescriptionTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.url_preview_description_text_view, "field 'mDescriptionTextView'", TextView.class);
        View findRequiredView = C0487Utils.findRequiredView(view, C1299R.C1301id.url_preview_hide_image_view, "method 'closeUrlPreview$vector_appfirechatRelease'");
        this.view2131297111 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                urlPreviewView.closeUrlPreview$vector_appfirechatRelease();
            }
        });
    }

    public void unbind() {
        UrlPreviewView urlPreviewView = this.target;
        if (urlPreviewView == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        urlPreviewView.mImageView = null;
        urlPreviewView.mTitleTextView = null;
        urlPreviewView.mDescriptionTextView = null;
        this.view2131297111.setOnClickListener(null);
        this.view2131297111 = null;
    }
}
