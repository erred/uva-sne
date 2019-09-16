package com.opengarden.firechat.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.p003v7.app.ActionBar;
import android.support.p003v7.app.AppCompatActivity;
import android.support.p003v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import androidx.core.view.ViewKt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.interfaces.Restorable;
import com.opengarden.firechat.dialogs.ConsentNotGivenHelper;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.Log.EventTag;
import com.opengarden.firechat.receiver.DebugReceiver;
import com.opengarden.firechat.util.AssetReader;
import java.util.HashSet;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\t\b&\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\rH\u0004J\u0010\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%H\u0014J\b\u0010&\u001a\u00020#H\u0004J\b\u0010'\u001a\u00020 H\u0016J\b\u0010(\u001a\u00020#H\u0016J\b\u0010)\u001a\u00020*H'J\b\u0010+\u001a\u00020\u0010H\u0004J\b\u0010,\u001a\u00020*H\u0017J\u0006\u0010-\u001a\u00020#J\b\u0010.\u001a\u00020#H\u0016J\b\u0010/\u001a\u00020 H\u0004J\u0006\u00100\u001a\u00020 J\u0012\u00101\u001a\u00020#2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0004J\b\u00102\u001a\u00020#H\u0014J\b\u00103\u001a\u00020#H\u0017J\u0010\u00104\u001a\u00020 2\u0006\u00105\u001a\u000206H\u0016J\b\u00107\u001a\u00020#H\u0014J\b\u00108\u001a\u00020#H\u0015J\u0010\u00109\u001a\u00020#2\u0006\u0010:\u001a\u00020\u0010H\u0015J\u0010\u0010;\u001a\u00020#2\u0006\u0010<\u001a\u00020 H\u0016J\b\u0010=\u001a\u00020#H\u0002J\u0006\u0010>\u001a\u00020#R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u000b\u001a\u0012\u0012\u0004\u0012\u00020\r0\fj\b\u0012\u0004\u0012\u00020\r`\u000eX\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u0011\u001a\u00020\u00128\u0004@\u0004X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001e¨\u0006?"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "Landroid/support/v7/app/AppCompatActivity;", "()V", "consentNotGivenHelper", "Lcom/opengarden/firechat/dialogs/ConsentNotGivenHelper;", "getConsentNotGivenHelper", "()Lcom/opengarden/firechat/dialogs/ConsentNotGivenHelper;", "consentNotGivenHelper$delegate", "Lkotlin/Lazy;", "debugReceiver", "Lcom/opengarden/firechat/receiver/DebugReceiver;", "restorables", "Ljava/util/HashSet;", "Lcom/opengarden/firechat/activity/interfaces/Restorable;", "Lkotlin/collections/HashSet;", "savedInstanceState", "Landroid/os/Bundle;", "toolbar", "Landroid/support/v7/widget/Toolbar;", "getToolbar", "()Landroid/support/v7/widget/Toolbar;", "setToolbar", "(Landroid/support/v7/widget/Toolbar;)V", "unBinder", "Lbutterknife/Unbinder;", "waitingView", "Landroid/view/View;", "getWaitingView", "()Landroid/view/View;", "setWaitingView", "(Landroid/view/View;)V", "addToRestorables", "", "restorable", "attachBaseContext", "", "base", "Landroid/content/Context;", "configureToolbar", "displayInFullscreen", "doBeforeSetContentView", "getLayoutRes", "", "getSavedInstanceState", "getTitleRes", "hideWaitingView", "initUiAndData", "isFirstCreation", "isWaitingViewVisible", "onCreate", "onDestroy", "onLowMemory", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "onPause", "onResume", "onSaveInstanceState", "outState", "onWindowFocusChanged", "hasFocus", "setFullScreen", "showWaitingView", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: RiotAppCompatActivity.kt */
public abstract class RiotAppCompatActivity extends AppCompatActivity {
    static final /* synthetic */ KProperty[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(RiotAppCompatActivity.class), "consentNotGivenHelper", "getConsentNotGivenHelper()Lcom/opengarden/firechat/dialogs/ConsentNotGivenHelper;"))};
    @NotNull
    private final Lazy consentNotGivenHelper$delegate = LazyKt.lazy(new RiotAppCompatActivity$consentNotGivenHelper$2(this));
    private DebugReceiver debugReceiver;
    private final HashSet<Restorable> restorables = new HashSet<>();
    /* access modifiers changed from: private */
    public Bundle savedInstanceState;
    @Nullable
    @NotNull
    @BindView(2131297080)
    protected Toolbar toolbar;
    private Unbinder unBinder;
    @org.jetbrains.annotations.Nullable
    private View waitingView;

    public boolean displayInFullscreen() {
        return false;
    }

    public void doBeforeSetContentView() {
    }

    @NotNull
    public final ConsentNotGivenHelper getConsentNotGivenHelper() {
        Lazy lazy = this.consentNotGivenHelper$delegate;
        KProperty kProperty = $$delegatedProperties[0];
        return (ConsentNotGivenHelper) lazy.getValue();
    }

    @LayoutRes
    public abstract int getLayoutRes();

    @StringRes
    public int getTitleRes() {
        return -1;
    }

    public void initUiAndData() {
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final Toolbar getToolbar() {
        Toolbar toolbar2 = this.toolbar;
        if (toolbar2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("toolbar");
        }
        return toolbar2;
    }

    /* access modifiers changed from: protected */
    public final void setToolbar(@NotNull Toolbar toolbar2) {
        Intrinsics.checkParameterIsNotNull(toolbar2, "<set-?>");
        this.toolbar = toolbar2;
    }

    @CallSuper
    public void onLowMemory() {
        super.onLowMemory();
        AssetReader.INSTANCE.clearCache();
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "base");
        super.attachBaseContext(VectorApp.getLocalisedContext(context));
    }

    /* access modifiers changed from: protected */
    public final void onCreate(@org.jetbrains.annotations.Nullable Bundle bundle) {
        super.onCreate(bundle);
        doBeforeSetContentView();
        setContentView(getLayoutRes());
        this.unBinder = ButterKnife.bind((Activity) this);
        this.savedInstanceState = bundle;
        initUiAndData();
        int titleRes = getTitleRes();
        if (titleRes != -1) {
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setTitle(titleRes);
            } else {
                setTitle(titleRes);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        Unbinder unbinder = this.unBinder;
        if (unbinder != null) {
            unbinder.unbind();
        }
        this.unBinder = null;
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onResume() {
        super.onResume();
        if (displayInFullscreen()) {
            setFullScreen();
        }
        EventTag eventTag = EventTag.NAVIGATION;
        StringBuilder sb = new StringBuilder();
        sb.append("onResume Activity ");
        sb.append(getClass().getSimpleName());
        Log.event(eventTag, sb.toString());
        DebugReceiver.Companion.getIntentFilter();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.debugReceiver != null) {
            unregisterReceiver(this.debugReceiver);
            this.debugReceiver = null;
        }
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z && displayInFullscreen()) {
            setFullScreen();
        }
    }

    public boolean onOptionsItemSelected(@NotNull MenuItem menuItem) {
        Intrinsics.checkParameterIsNotNull(menuItem, "item");
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        setResult(0);
        finish();
        return true;
    }

    @org.jetbrains.annotations.Nullable
    public final View getWaitingView() {
        return this.waitingView;
    }

    public final void setWaitingView(@org.jetbrains.annotations.Nullable View view) {
        this.waitingView = view;
    }

    public final boolean isWaitingViewVisible() {
        View view = this.waitingView;
        if (view == null) {
            return false;
        }
        return view.getVisibility() == 0;
    }

    public final void showWaitingView() {
        if (this.waitingView != null) {
            View view = this.waitingView;
            if (view != null) {
                ViewKt.setVisible(view, true);
            }
        }
    }

    public final void hideWaitingView() {
        View view = this.waitingView;
        if (view != null) {
            ViewKt.setVisible(view, false);
        }
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final Bundle getSavedInstanceState() {
        Bundle bundle = this.savedInstanceState;
        if (bundle == null) {
            Intrinsics.throwNpe();
        }
        return bundle;
    }

    /* access modifiers changed from: protected */
    public final boolean isFirstCreation() {
        return this.savedInstanceState == null;
    }

    /* access modifiers changed from: protected */
    public final void configureToolbar() {
        Toolbar toolbar2 = this.toolbar;
        if (toolbar2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("toolbar");
        }
        setSupportActionBar(toolbar2);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private final void setFullScreen() {
        Window window = getWindow();
        Intrinsics.checkExpressionValueIsNotNull(window, "window");
        View decorView = window.getDecorView();
        Intrinsics.checkExpressionValueIsNotNull(decorView, "window.decorView");
        decorView.setSystemUiVisibility(5894);
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onSaveInstanceState(@NotNull Bundle bundle) {
        Intrinsics.checkParameterIsNotNull(bundle, "outState");
        super.onSaveInstanceState(bundle);
        for (Restorable saveState : this.restorables) {
            saveState.saveState(bundle);
        }
    }

    /* access modifiers changed from: protected */
    public final boolean addToRestorables(@NotNull Restorable restorable) {
        Intrinsics.checkParameterIsNotNull(restorable, "restorable");
        return this.restorables.add(restorable);
    }
}
