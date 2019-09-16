package com.opengarden.firechat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.p000v4.app.Fragment;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.opengarden.firechat.activity.RiotAppCompatActivity;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.Log.EventTag;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0016\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016J\b\u0010\u000f\u001a\u00020\fH\u0017J\b\u0010\u0010\u001a\u00020\fH\u0016J\b\u0010\u0011\u001a\u00020\fH\u0017J\u001a\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0017R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\n¨\u0006\u0017"}, mo21251d2 = {"Lcom/opengarden/firechat/fragments/VectorBaseFragment;", "Landroid/support/v4/app/Fragment;", "()V", "mUnBinder", "Lbutterknife/Unbinder;", "riotActivity", "Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "getRiotActivity", "()Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "setRiotActivity", "(Lcom/opengarden/firechat/activity/RiotAppCompatActivity;)V", "onAttach", "", "context", "Landroid/content/Context;", "onDestroyView", "onDetach", "onResume", "onViewCreated", "view", "Landroid/view/View;", "savedInstanceState", "Landroid/os/Bundle;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorBaseFragment.kt */
public class VectorBaseFragment extends Fragment {
    private Unbinder mUnBinder;
    @Nullable
    private RiotAppCompatActivity riotActivity;

    /* access modifiers changed from: protected */
    @Nullable
    public final RiotAppCompatActivity getRiotActivity() {
        return this.riotActivity;
    }

    /* access modifiers changed from: protected */
    public final void setRiotActivity(@Nullable RiotAppCompatActivity riotAppCompatActivity) {
        this.riotActivity = riotAppCompatActivity;
    }

    @CallSuper
    public void onResume() {
        super.onResume();
        EventTag eventTag = EventTag.NAVIGATION;
        StringBuilder sb = new StringBuilder();
        sb.append("onResume Fragment ");
        sb.append(getClass().getSimpleName());
        Log.event(eventTag, sb.toString());
    }

    public void onAttach(@Nullable Context context) {
        super.onAttach(context);
        if (context == null) {
            throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.activity.RiotAppCompatActivity");
        }
        this.riotActivity = (RiotAppCompatActivity) context;
    }

    public void onDetach() {
        super.onDetach();
        this.riotActivity = null;
    }

    @CallSuper
    public void onViewCreated(@NotNull View view, @Nullable Bundle bundle) {
        Intrinsics.checkParameterIsNotNull(view, "view");
        super.onViewCreated(view, bundle);
        this.mUnBinder = ButterKnife.bind((Object) this, view);
    }

    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        Unbinder unbinder = this.mUnBinder;
        if (unbinder != null) {
            unbinder.unbind();
        }
        this.mUnBinder = null;
    }
}
