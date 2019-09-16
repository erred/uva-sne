package com.opengarden.firechat.webview;

import android.text.TextUtils;
import com.google.android.gms.common.internal.ImagesContract;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.RiotAppCompatActivity;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.util.WeakReferenceDelegate;
import com.opengarden.firechat.util.WeakReferenceDelegateKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u00005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000*\u0001\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0001¢\u0006\u0002\u0010\u0005J\b\u0010\u000e\u001a\u00020\u000fH\u0002J!\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0012H\u0001J\u0010\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0011\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0001J\u0011\u0010\u0018\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0001J\u0011\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0011\u001a\u00020\u0012H\u0001R\u0010\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\u0004\n\u0002\u0010\bR\u000e\u0010\u0004\u001a\u00020\u0001X\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u0004\u0018\u00010\u00038BX\u0002¢\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000b¨\u0006\u001b"}, mo21251d2 = {"Lcom/opengarden/firechat/webview/ConsentWebViewEventListener;", "Lcom/opengarden/firechat/webview/WebViewEventListener;", "activity", "Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "delegate", "(Lcom/opengarden/firechat/activity/RiotAppCompatActivity;Lcom/opengarden/firechat/webview/WebViewEventListener;)V", "createRiotBotRoomCallback", "com/opengarden/firechat/webview/ConsentWebViewEventListener$createRiotBotRoomCallback$1", "Lcom/opengarden/firechat/webview/ConsentWebViewEventListener$createRiotBotRoomCallback$1;", "safeActivity", "getSafeActivity", "()Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "safeActivity$delegate", "Lcom/opengarden/firechat/util/WeakReferenceDelegate;", "createRiotBotRoomIfNeeded", "", "onPageError", "url", "", "errorCode", "", "description", "onPageFinished", "onPageStarted", "pageWillStart", "shouldOverrideUrlLoading", "", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: ConsentWebViewEventListener.kt */
public final class ConsentWebViewEventListener implements WebViewEventListener {
    static final /* synthetic */ KProperty[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(ConsentWebViewEventListener.class), "safeActivity", "getSafeActivity()Lcom/opengarden/firechat/activity/RiotAppCompatActivity;"))};
    private final ConsentWebViewEventListener$createRiotBotRoomCallback$1 createRiotBotRoomCallback = new ConsentWebViewEventListener$createRiotBotRoomCallback$1(this);
    private final WebViewEventListener delegate;
    private final WeakReferenceDelegate safeActivity$delegate;

    /* access modifiers changed from: private */
    public final RiotAppCompatActivity getSafeActivity() {
        return (RiotAppCompatActivity) this.safeActivity$delegate.getValue(this, $$delegatedProperties[0]);
    }

    public void onPageError(@NotNull String str, int i, @NotNull String str2) {
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        Intrinsics.checkParameterIsNotNull(str2, "description");
        this.delegate.onPageError(str, i, str2);
    }

    public void onPageStarted(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        this.delegate.onPageStarted(str);
    }

    public void pageWillStart(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        this.delegate.pageWillStart(str);
    }

    public boolean shouldOverrideUrlLoading(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        return this.delegate.shouldOverrideUrlLoading(str);
    }

    public ConsentWebViewEventListener(@NotNull RiotAppCompatActivity riotAppCompatActivity, @NotNull WebViewEventListener webViewEventListener) {
        Intrinsics.checkParameterIsNotNull(riotAppCompatActivity, "activity");
        Intrinsics.checkParameterIsNotNull(webViewEventListener, "delegate");
        this.delegate = webViewEventListener;
        this.safeActivity$delegate = WeakReferenceDelegateKt.weak(riotAppCompatActivity);
    }

    public void onPageFinished(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        this.delegate.onPageFinished(str);
        if (TextUtils.equals(str, "https://matrix.org/_matrix/consent")) {
            createRiotBotRoomIfNeeded();
        }
    }

    private final void createRiotBotRoomIfNeeded() {
        RiotAppCompatActivity safeActivity = getSafeActivity();
        if (safeActivity != null) {
            Matrix instance = Matrix.getInstance(safeActivity);
            Intrinsics.checkExpressionValueIsNotNull(instance, "Matrix.getInstance(it)");
            MXSession defaultSession = instance.getDefaultSession();
            Intrinsics.checkExpressionValueIsNotNull(defaultSession, "session");
            MXDataHandler dataHandler = defaultSession.getDataHandler();
            Intrinsics.checkExpressionValueIsNotNull(dataHandler, "session.dataHandler");
            IMXStore store = dataHandler.getStore();
            Intrinsics.checkExpressionValueIsNotNull(store, "session.dataHandler.store");
            Collection rooms = store.getRooms();
            Intrinsics.checkExpressionValueIsNotNull(rooms, "session.dataHandler.store.rooms");
            Iterable iterable = rooms;
            Collection arrayList = new ArrayList();
            for (Object next : iterable) {
                if (((Room) next).hasMembership(RoomMember.MEMBERSHIP_JOIN)) {
                    arrayList.add(next);
                }
            }
            if (((List) arrayList).isEmpty()) {
                safeActivity.showWaitingView();
                defaultSession.createDirectMessageRoom("@riot-bot:matrix.org", this.createRiotBotRoomCallback);
                return;
            }
            safeActivity.finish();
        }
    }
}
