package com.opengarden.firechat.util;

import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002¨\u0006\u0003"}, mo21251d2 = {"getFingerprintHumanReadable", "", "Lcom/opengarden/firechat/matrixsdk/crypto/data/MXDeviceInfo;", "vector_appfirechatRelease"}, mo21252k = 2, mo21253mv = {1, 1, 9})
/* compiled from: MatrixSdkExtensions.kt */
public final class MatrixSdkExtensionsKt {
    @Nullable
    public static final String getFingerprintHumanReadable(@NotNull MXDeviceInfo mXDeviceInfo) {
        Intrinsics.checkParameterIsNotNull(mXDeviceInfo, "$receiver");
        String fingerprint = mXDeviceInfo.fingerprint();
        if (fingerprint != null) {
            List chunked = StringsKt.chunked(fingerprint, 4);
            if (chunked != null) {
                return CollectionsKt.joinToString$default(chunked, StringUtils.SPACE, null, null, 0, null, null, 62, null);
            }
        }
        return null;
    }
}
