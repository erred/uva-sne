package com.opengarden.firechat.fragments;

import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.sync.DeviceInfo;
import com.opengarden.firechat.matrixsdk.rest.model.sync.DevicesListResponse;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000)\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0014\u0010\b\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016J\u0010\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u0002H\u0016J\u0014\u0010\r\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016¨\u0006\u000e"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$refreshDevicesList$1", "Lcom/opengarden/firechat/matrixsdk/rest/callback/ApiCallback;", "Lcom/opengarden/firechat/matrixsdk/rest/model/sync/DevicesListResponse;", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;)V", "onMatrixError", "", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "info", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$refreshDevicesList$1 implements ApiCallback<DevicesListResponse> {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$refreshDevicesList$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public void onSuccess(@NotNull DevicesListResponse devicesListResponse) {
        Intrinsics.checkParameterIsNotNull(devicesListResponse, "info");
        if (devicesListResponse.devices.size() == 0) {
            this.this$0.removeDevicesPreference();
            return;
        }
        VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment = this.this$0;
        List<DeviceInfo> list = devicesListResponse.devices;
        Intrinsics.checkExpressionValueIsNotNull(list, "info.devices");
        vectorSettingsPreferencesFragment.buildDevicesSettings(list);
    }

    public void onNetworkError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        this.this$0.removeDevicesPreference();
        this.this$0.onCommonDone(exc.getMessage());
    }

    public void onMatrixError(@NotNull MatrixError matrixError) {
        Intrinsics.checkParameterIsNotNull(matrixError, "e");
        this.this$0.removeDevicesPreference();
        this.this$0.onCommonDone(matrixError.getMessage());
    }

    public void onUnexpectedError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        this.this$0.removeDevicesPreference();
        this.this$0.onCommonDone(exc.getMessage());
    }
}
