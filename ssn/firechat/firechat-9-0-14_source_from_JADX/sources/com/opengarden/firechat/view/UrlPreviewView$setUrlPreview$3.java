package com.opengarden.firechat.view;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.opengarden.firechat.matrixsdk.rest.model.URLPreview;
import com.opengarden.firechat.util.ExternalApplicationsUtilKt;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/view/View;", "kotlin.jvm.PlatformType", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: UrlPreviewView.kt */
final class UrlPreviewView$setUrlPreview$3 implements OnClickListener {
    final /* synthetic */ Context $context;
    final /* synthetic */ URLPreview $preview;

    UrlPreviewView$setUrlPreview$3(Context context, URLPreview uRLPreview) {
        this.$context = context;
        this.$preview = uRLPreview;
    }

    public final void onClick(View view) {
        ExternalApplicationsUtilKt.openUrlInExternalBrowser(this.$context, this.$preview.getRequestedURL());
    }
}
