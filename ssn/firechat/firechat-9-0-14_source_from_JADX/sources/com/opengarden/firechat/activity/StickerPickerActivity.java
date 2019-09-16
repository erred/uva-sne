package com.opengarden.firechat.activity;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import com.google.gson.Gson;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.ThemeUtils;
import java.net.URLEncoder;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0007\u001a\u00020\u0004H\u0016J&\u0010\b\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\u000fH\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\"\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u000f2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0014J\u0012\u0010\u0018\u001a\u00020\t2\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0016J\u0010\u0010\u001b\u001a\u00020\t2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016J&\u0010\u001e\u001a\u00020\u00122\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X.¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X.¢\u0006\u0002\n\u0000¨\u0006 "}, mo21251d2 = {"Lcom/opengarden/firechat/activity/StickerPickerActivity;", "Lcom/opengarden/firechat/activity/AbstractWidgetActivity;", "()V", "mWidgetId", "", "mWidgetUrl", "buildInterfaceUrl", "scalarToken", "dealsWithWidgetRequest", "", "eventData", "", "", "Lcom/opengarden/firechat/types/JsonDict;", "getLayoutRes", "", "getTitleRes", "initUiAndData", "", "onActivityResult", "requestCode", "resultCode", "data", "Landroid/content/Intent;", "onCreateOptionsMenu", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "sendSticker", "Companion", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: StickerPickerActivity.kt */
public final class StickerPickerActivity extends AbstractWidgetActivity {
    public static final Companion Companion = new Companion(null);
    private static final String EXTRA_OUT_CONTENT = "EXTRA_OUT_CONTENT";
    private static final String EXTRA_WIDGET_ID = "EXTRA_WIDGET_ID";
    private static final String EXTRA_WIDGET_URL = "EXTRA_WIDGET_URL";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "StickerPickerActivity";
    @NotNull
    public static final String WIDGET_NAME = "m.stickerpicker";
    private String mWidgetId;
    private String mWidgetUrl;

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J.\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u0004J\u000e\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\rR\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u001c\u0010\u0007\u001a\n \b*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u0016"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/StickerPickerActivity$Companion;", "", "()V", "EXTRA_OUT_CONTENT", "", "EXTRA_WIDGET_ID", "EXTRA_WIDGET_URL", "LOG_TAG", "kotlin.jvm.PlatformType", "getLOG_TAG", "()Ljava/lang/String;", "WIDGET_NAME", "getIntent", "Landroid/content/Intent;", "context", "Landroid/content/Context;", "matrixId", "roomId", "widgetUrl", "widgetId", "getResultContent", "intent", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: StickerPickerActivity.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* access modifiers changed from: private */
        public final String getLOG_TAG() {
            return StickerPickerActivity.LOG_TAG;
        }

        @NotNull
        public final Intent getIntent(@NotNull Context context, @NotNull String str, @NotNull String str2, @NotNull String str3, @NotNull String str4) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(str, "matrixId");
            Intrinsics.checkParameterIsNotNull(str2, "roomId");
            Intrinsics.checkParameterIsNotNull(str3, "widgetUrl");
            Intrinsics.checkParameterIsNotNull(str4, "widgetId");
            Intent intent = new Intent(context, StickerPickerActivity.class);
            intent.putExtra("EXTRA_MATRIX_ID", str);
            intent.putExtra("EXTRA_ROOM_ID", str2);
            intent.putExtra(StickerPickerActivity.EXTRA_WIDGET_URL, str3);
            intent.putExtra("EXTRA_WIDGET_ID", str4);
            return intent;
        }

        @NotNull
        public final String getResultContent(@NotNull Intent intent) {
            Intrinsics.checkParameterIsNotNull(intent, "intent");
            String stringExtra = intent.getStringExtra(StickerPickerActivity.EXTRA_OUT_CONTENT);
            Intrinsics.checkExpressionValueIsNotNull(stringExtra, "intent.getStringExtra(EXTRA_OUT_CONTENT)");
            return stringExtra;
        }
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_choose_sticker;
    }

    public int getTitleRes() {
        return C1299R.string.title_activity_choose_sticker;
    }

    public void initUiAndData() {
        String stringExtra = getIntent().getStringExtra(EXTRA_WIDGET_URL);
        Intrinsics.checkExpressionValueIsNotNull(stringExtra, "intent.getStringExtra(EXTRA_WIDGET_URL)");
        this.mWidgetUrl = stringExtra;
        String stringExtra2 = getIntent().getStringExtra("EXTRA_WIDGET_ID");
        Intrinsics.checkExpressionValueIsNotNull(stringExtra2, "intent.getStringExtra(EXTRA_WIDGET_ID)");
        this.mWidgetId = stringExtra2;
        configureToolbar();
        super.initUiAndData();
    }

    @Nullable
    public String buildInterfaceUrl(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, "scalarToken");
        try {
            StringBuilder sb = new StringBuilder();
            String str2 = this.mWidgetUrl;
            if (str2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mWidgetUrl");
            }
            sb.append(str2);
            sb.append("?");
            sb.append("scalar_token=");
            sb.append(URLEncoder.encode(str, "utf-8"));
            sb.append("&room_id=");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(URLEncoder.encode(mRoom.getRoomId(), "utf-8"));
            sb.append("&widgetId=");
            String str3 = this.mWidgetId;
            if (str3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mWidgetId");
            }
            sb.append(URLEncoder.encode(str3, "utf-8"));
            return sb.toString();
        } catch (Exception e) {
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## buildInterfaceUrl() failed ");
            sb2.append(e.getMessage());
            Log.m211e(access$getLOG_TAG$p, sb2.toString());
            return null;
        }
    }

    public boolean dealsWithWidgetRequest(@NotNull Map<String, ? extends Object> map) {
        Intrinsics.checkParameterIsNotNull(map, "eventData");
        String str = (String) map.get("action");
        if (str == null || str.hashCode() != 1525570748 || !str.equals(Event.EVENT_TYPE_STICKER)) {
            return super.dealsWithWidgetRequest(map);
        }
        sendSticker(map);
        Unit unit = Unit.INSTANCE;
        return true;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, @Nullable Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 13000) {
            getMWebView().reload();
        }
    }

    public boolean onCreateOptionsMenu(@Nullable Menu menu) {
        getMenuInflater().inflate(C1299R.C1302menu.vector_choose_sticker, menu);
        CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(this, C1299R.attr.icon_tint_on_dark_action_bar_color));
        return true;
    }

    public boolean onOptionsItemSelected(@NotNull MenuItem menuItem) {
        Intrinsics.checkParameterIsNotNull(menuItem, "item");
        if (menuItem.getItemId() != C1299R.C1301id.menu_settings) {
            return super.onOptionsItemSelected(menuItem);
        }
        String str = this.mWidgetId;
        if (str == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWidgetId");
        }
        openIntegrationManager(str, "type_m.stickerpicker");
        return true;
    }

    private final void sendSticker(Map<String, ? extends Object> map) {
        Log.m209d(Companion.getLOG_TAG(), "Received request send sticker");
        Object obj = map.get("data");
        if (obj == null) {
            String string = getString(C1299R.string.widget_integration_missing_parameter);
            Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…ration_missing_parameter)");
            sendError(string, map);
            return;
        }
        Object obj2 = ((Map) obj).get("content");
        if (obj2 == null) {
            String string2 = getString(C1299R.string.widget_integration_missing_parameter);
            Intrinsics.checkExpressionValueIsNotNull(string2, "getString(R.string.widge…ration_missing_parameter)");
            sendError(string2, map);
            return;
        }
        String json = new Gson().toJson(obj2);
        sendSuccess(map);
        Intent intent = new Intent();
        intent.putExtra(EXTRA_OUT_CONTENT, json);
        setResult(-1, intent);
        finish();
    }
}
