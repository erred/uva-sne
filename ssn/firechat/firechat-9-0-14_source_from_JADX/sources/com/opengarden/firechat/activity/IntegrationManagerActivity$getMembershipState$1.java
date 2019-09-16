package com.opengarden.firechat.activity;

import com.google.gson.JsonElement;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000)\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0014\u0010\b\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016J\u0012\u0010\u000b\u001a\u00020\u00052\b\u0010\f\u001a\u0004\u0018\u00010\u0002H\u0016J\u0014\u0010\r\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016¨\u0006\u000e"}, mo21251d2 = {"com/opengarden/firechat/activity/IntegrationManagerActivity$getMembershipState$1", "Lcom/opengarden/firechat/matrixsdk/rest/callback/ApiCallback;", "Lcom/opengarden/firechat/matrixsdk/rest/model/Event;", "(Lcom/opengarden/firechat/activity/IntegrationManagerActivity;Ljava/lang/String;Ljava/util/Map;)V", "onMatrixError", "", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "event", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: IntegrationManagerActivity.kt */
public final class IntegrationManagerActivity$getMembershipState$1 implements ApiCallback<Event> {
    final /* synthetic */ Map $eventData;
    final /* synthetic */ String $userId;
    final /* synthetic */ IntegrationManagerActivity this$0;

    IntegrationManagerActivity$getMembershipState$1(IntegrationManagerActivity integrationManagerActivity, String str, Map map) {
        this.this$0 = integrationManagerActivity;
        this.$userId = str;
        this.$eventData = map;
    }

    public void onSuccess(@Nullable Event event) {
        String access$getLOG_TAG$p = IntegrationManagerActivity.Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("membership_state of ");
        sb.append(this.$userId);
        sb.append(" in room ");
        Room mRoom = this.this$0.getMRoom();
        if (mRoom == null) {
            Intrinsics.throwNpe();
        }
        sb.append(mRoom.getRoomId());
        sb.append(" returns ");
        sb.append(event);
        Log.m209d(access$getLOG_TAG$p, sb.toString());
        if (event != null) {
            IntegrationManagerActivity integrationManagerActivity = this.this$0;
            JsonElement jsonElement = event.content;
            Intrinsics.checkExpressionValueIsNotNull(jsonElement, "event.content");
            integrationManagerActivity.sendObjectAsJsonMap(jsonElement, this.$eventData);
            return;
        }
        this.this$0.sendObjectResponse(null, this.$eventData);
    }

    public void onNetworkError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        String access$getLOG_TAG$p = IntegrationManagerActivity.Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("membership_state of ");
        sb.append(this.$userId);
        sb.append(" in room ");
        Room mRoom = this.this$0.getMRoom();
        if (mRoom == null) {
            Intrinsics.throwNpe();
        }
        sb.append(mRoom.getRoomId());
        sb.append(" failed ");
        sb.append(exc.getMessage());
        Log.m211e(access$getLOG_TAG$p, sb.toString());
        IntegrationManagerActivity integrationManagerActivity = this.this$0;
        String string = this.this$0.getString(C1299R.string.widget_integration_failed_to_send_request);
        Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…n_failed_to_send_request)");
        integrationManagerActivity.sendError(string, this.$eventData);
    }

    public void onMatrixError(@NotNull MatrixError matrixError) {
        Intrinsics.checkParameterIsNotNull(matrixError, "e");
        String access$getLOG_TAG$p = IntegrationManagerActivity.Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("membership_state of ");
        sb.append(this.$userId);
        sb.append(" in room ");
        Room mRoom = this.this$0.getMRoom();
        if (mRoom == null) {
            Intrinsics.throwNpe();
        }
        sb.append(mRoom.getRoomId());
        sb.append(" failed ");
        sb.append(matrixError.getMessage());
        Log.m211e(access$getLOG_TAG$p, sb.toString());
        IntegrationManagerActivity integrationManagerActivity = this.this$0;
        String string = this.this$0.getString(C1299R.string.widget_integration_failed_to_send_request);
        Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…n_failed_to_send_request)");
        integrationManagerActivity.sendError(string, this.$eventData);
    }

    public void onUnexpectedError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        String access$getLOG_TAG$p = IntegrationManagerActivity.Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("membership_state of ");
        sb.append(this.$userId);
        sb.append(" in room ");
        Room mRoom = this.this$0.getMRoom();
        if (mRoom == null) {
            Intrinsics.throwNpe();
        }
        sb.append(mRoom.getRoomId());
        sb.append(" failed ");
        sb.append(exc.getMessage());
        Log.m211e(access$getLOG_TAG$p, sb.toString());
        IntegrationManagerActivity integrationManagerActivity = this.this$0;
        String string = this.this$0.getString(C1299R.string.widget_integration_failed_to_send_request);
        Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…n_failed_to_send_request)");
        integrationManagerActivity.sendError(string, this.$eventData);
    }
}
