package com.google.firebase.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Objects;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;

@KeepForSdk
/* compiled from: com.google.firebase:firebase-common@@16.0.1 */
public class InternalTokenResult {
    private String zza;

    @KeepForSdk
    public InternalTokenResult(@Nullable String str) {
        this.zza = str;
    }

    @Nullable
    @KeepForSdk
    public String getToken() {
        return this.zza;
    }

    public int hashCode() {
        return Objects.hashCode(this.zza);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof InternalTokenResult)) {
            return false;
        }
        return Objects.equal(this.zza, ((InternalTokenResult) obj).zza);
    }

    public String toString() {
        return Objects.toStringHelper(this).add(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_TOKEN, this.zza).toString();
    }
}
