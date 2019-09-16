package com.opengarden.firechat.util;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u001f\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u0006\u0010\u0003\u001a\u0002H\u0002¢\u0006\u0002\u0010\u0004¨\u0006\u0005"}, mo21251d2 = {"weak", "Lcom/opengarden/firechat/util/WeakReferenceDelegate;", "T", "value", "(Ljava/lang/Object;)Lcom/opengarden/firechat/util/WeakReferenceDelegate;", "vector_appfirechatRelease"}, mo21252k = 2, mo21253mv = {1, 1, 9})
/* compiled from: WeakReferenceDelegate.kt */
public final class WeakReferenceDelegateKt {
    @NotNull
    public static final <T> WeakReferenceDelegate<T> weak(T t) {
        return new WeakReferenceDelegate<>(t);
    }
}
