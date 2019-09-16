package com.opengarden.firechat.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.model.URLPreview;
import java.util.HashSet;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 %2\u00020\u0001:\u0001%B%\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\r\u0010\u001c\u001a\u00020\u001dH\u0001¢\u0006\u0002\b\u001eJ(\u0010\u001f\u001a\u00020\u001d2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010#2\u0006\u0010$\u001a\u00020\u001bR\u001e\u0010\t\u001a\u00020\n8\u0006@\u0006X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001e\u0010\u000f\u001a\u00020\u00108\u0006@\u0006X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0015\u001a\u00020\u0016X\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u0017\u001a\u00020\n8\u0006@\u0006X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\f\"\u0004\b\u0019\u0010\u000eR\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u000e¢\u0006\u0002\n\u0000¨\u0006&"}, mo21251d2 = {"Lcom/opengarden/firechat/view/UrlPreviewView;", "Landroid/widget/FrameLayout;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "defStyleAttr", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "mDescriptionTextView", "Landroid/widget/TextView;", "getMDescriptionTextView", "()Landroid/widget/TextView;", "setMDescriptionTextView", "(Landroid/widget/TextView;)V", "mImageView", "Landroid/widget/ImageView;", "getMImageView", "()Landroid/widget/ImageView;", "setMImageView", "(Landroid/widget/ImageView;)V", "mIsDismissed", "", "mTitleTextView", "getMTitleTextView", "setMTitleTextView", "mUID", "", "closeUrlPreview", "", "closeUrlPreview$vector_appfirechatRelease", "setUrlPreview", "session", "Lcom/opengarden/firechat/matrixsdk/MXSession;", "preview", "Lcom/opengarden/firechat/matrixsdk/rest/model/URLPreview;", "uid", "Companion", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: UrlPreviewView.kt */
public final class UrlPreviewView extends FrameLayout {
    public static final Companion Companion = new Companion(null);
    private static final String DISMISSED_URL_PREVIEWS_PREF_KEY = "DISMISSED_URL_PREVIEWS_PREF_KEY";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "UrlPreviewView";
    /* access modifiers changed from: private */
    public static final Lazy sDismissedUrlsPreviews$delegate = LazyKt.lazy(UrlPreviewView$Companion$sDismissedUrlsPreviews$2.INSTANCE);
    @NotNull
    @BindView(2131297110)
    public TextView mDescriptionTextView;
    @NotNull
    @BindView(2131297112)
    public ImageView mImageView;
    private boolean mIsDismissed;
    @NotNull
    @BindView(2131297114)
    public TextView mTitleTextView;
    private String mUID;

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u001c\u0010\u0005\u001a\n \u0006*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR)\u0010\t\u001a\u0010\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\u00040\u00040\n8BX\u0002¢\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\f¨\u0006\u0012"}, mo21251d2 = {"Lcom/opengarden/firechat/view/UrlPreviewView$Companion;", "", "()V", "DISMISSED_URL_PREVIEWS_PREF_KEY", "", "LOG_TAG", "kotlin.jvm.PlatformType", "getLOG_TAG", "()Ljava/lang/String;", "sDismissedUrlsPreviews", "Ljava/util/HashSet;", "getSDismissedUrlsPreviews", "()Ljava/util/HashSet;", "sDismissedUrlsPreviews$delegate", "Lkotlin/Lazy;", "didUrlPreviewDismiss", "", "uid", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: UrlPreviewView.kt */
    public static final class Companion {
        static final /* synthetic */ KProperty[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(Companion.class), "sDismissedUrlsPreviews", "getSDismissedUrlsPreviews()Ljava/util/HashSet;"))};

        /* access modifiers changed from: private */
        public final HashSet<String> getSDismissedUrlsPreviews() {
            Lazy access$getSDismissedUrlsPreviews$cp = UrlPreviewView.sDismissedUrlsPreviews$delegate;
            KProperty kProperty = $$delegatedProperties[0];
            return (HashSet) access$getSDismissedUrlsPreviews$cp.getValue();
        }

        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* access modifiers changed from: private */
        public final String getLOG_TAG() {
            return UrlPreviewView.LOG_TAG;
        }

        public final boolean didUrlPreviewDismiss(@NotNull String str) {
            Intrinsics.checkParameterIsNotNull(str, "uid");
            return getSDismissedUrlsPreviews().contains(str);
        }
    }

    @JvmOverloads
    public UrlPreviewView(@NotNull Context context) {
        this(context, null, 0, 6, null);
    }

    @JvmOverloads
    public UrlPreviewView(@NotNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0, 4, null);
    }

    @JvmOverloads
    public /* synthetic */ UrlPreviewView(Context context, AttributeSet attributeSet, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        if ((i2 & 2) != 0) {
            attributeSet = null;
        }
        if ((i2 & 4) != 0) {
            i = 0;
        }
        this(context, attributeSet, i);
    }

    @JvmOverloads
    public UrlPreviewView(@NotNull Context context, @Nullable AttributeSet attributeSet, int i) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context, attributeSet, i);
        View.inflate(context, C1299R.layout.url_preview_view, this);
        ButterKnife.bind((View) this);
    }

    @NotNull
    public final ImageView getMImageView() {
        ImageView imageView = this.mImageView;
        if (imageView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mImageView");
        }
        return imageView;
    }

    public final void setMImageView(@NotNull ImageView imageView) {
        Intrinsics.checkParameterIsNotNull(imageView, "<set-?>");
        this.mImageView = imageView;
    }

    @NotNull
    public final TextView getMTitleTextView() {
        TextView textView = this.mTitleTextView;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mTitleTextView");
        }
        return textView;
    }

    public final void setMTitleTextView(@NotNull TextView textView) {
        Intrinsics.checkParameterIsNotNull(textView, "<set-?>");
        this.mTitleTextView = textView;
    }

    @NotNull
    public final TextView getMDescriptionTextView() {
        TextView textView = this.mDescriptionTextView;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mDescriptionTextView");
        }
        return textView;
    }

    public final void setMDescriptionTextView(@NotNull TextView textView) {
        Intrinsics.checkParameterIsNotNull(textView, "<set-?>");
        this.mDescriptionTextView = textView;
    }

    public final void setUrlPreview(@NotNull Context context, @NotNull MXSession mXSession, @Nullable URLPreview uRLPreview, @NotNull String str) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(mXSession, "session");
        Intrinsics.checkParameterIsNotNull(str, "uid");
        String access$getLOG_TAG$p = Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("## setUrlPreview ");
        sb.append(this);
        Log.d(access$getLOG_TAG$p, sb.toString());
        if (uRLPreview == null || this.mIsDismissed || Companion.didUrlPreviewDismiss(str) || !mXSession.isURLPreviewEnabled()) {
            setVisibility(8);
            return;
        }
        setVisibility(0);
        MXMediasCache mediasCache = mXSession.getMediasCache();
        HomeServerConnectionConfig homeServerConfig = mXSession.getHomeServerConfig();
        ImageView imageView = this.mImageView;
        if (imageView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mImageView");
        }
        mediasCache.loadAvatarThumbnail(homeServerConfig, imageView, uRLPreview.getThumbnailURL(), context.getResources().getDimensionPixelSize(C1299R.dimen.profile_avatar_size));
        TextView textView = this.mTitleTextView;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mTitleTextView");
        }
        if (uRLPreview.getRequestedURL() != null && uRLPreview.getTitle() != null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("<a href=\"");
            sb2.append(uRLPreview.getRequestedURL());
            sb2.append("\">");
            sb2.append(uRLPreview.getTitle());
            sb2.append("</a>");
            textView.setText(Html.fromHtml(sb2.toString()));
        } else if (uRLPreview.getTitle() != null) {
            textView.setText(uRLPreview.getTitle());
        } else {
            textView.setText(uRLPreview.getRequestedURL());
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView textView2 = this.mDescriptionTextView;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mDescriptionTextView");
        }
        if (TextUtils.isEmpty(uRLPreview.getDescription())) {
            textView2.setVisibility(8);
        } else {
            textView2.setVisibility(0);
            textView2.setText(uRLPreview.getDescription());
        }
        this.mUID = str;
        if (uRLPreview.getRequestedURL() == null) {
            TextView textView3 = this.mDescriptionTextView;
            if (textView3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mDescriptionTextView");
            }
            textView3.setClickable(false);
            ImageView imageView2 = this.mImageView;
            if (imageView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mImageView");
            }
            imageView2.setClickable(false);
        } else if (uRLPreview.getRequestedURL() != null) {
            TextView textView4 = this.mDescriptionTextView;
            if (textView4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mDescriptionTextView");
            }
            textView4.setOnClickListener(new UrlPreviewView$setUrlPreview$3(context, uRLPreview));
            ImageView imageView3 = this.mImageView;
            if (imageView3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mImageView");
            }
            imageView3.setOnClickListener(new UrlPreviewView$setUrlPreview$4(context, uRLPreview));
        }
    }

    @OnClick({2131297111})
    public final void closeUrlPreview$vector_appfirechatRelease() {
        this.mIsDismissed = true;
        setVisibility(8);
        Companion.getSDismissedUrlsPreviews().add(this.mUID);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(VectorApp.getInstance());
        Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "PreferenceManager.getDef…(VectorApp.getInstance())");
        Editor edit = defaultSharedPreferences.edit();
        Intrinsics.checkExpressionValueIsNotNull(edit, "editor");
        edit.putStringSet(DISMISSED_URL_PREVIEWS_PREF_KEY, Companion.getSDismissedUrlsPreviews());
        edit.apply();
    }
}
