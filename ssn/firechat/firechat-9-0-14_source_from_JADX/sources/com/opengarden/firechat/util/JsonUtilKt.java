package com.opengarden.firechat.util;

import com.google.gson.Gson;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\u001a$\u0010\u0000\u001a\u001c\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0001j\n\u0012\u0004\u0012\u00020\u0003\u0018\u0001`\u0004*\u00020\u0003¨\u0006\u0005"}, mo21251d2 = {"toJsonMap", "", "", "", "Lcom/opengarden/firechat/types/JsonDict;", "vector_appfirechatRelease"}, mo21252k = 2, mo21253mv = {1, 1, 9})
/* compiled from: JsonUtil.kt */
public final class JsonUtilKt {
    @Nullable
    public static final Map<String, Object> toJsonMap(@NotNull Object obj) {
        Intrinsics.checkParameterIsNotNull(obj, "$receiver");
        Gson gson = JsonUtils.getGson(false);
        Map map = null;
        try {
            return (Map) gson.fromJson(gson.toJson(obj), new JsonUtilKt$toJsonMap$1().getType());
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("## Any.toJsonMap() failed ");
            sb.append(e.getMessage());
            Log.m211e("TAG", sb.toString());
            return map;
        }
    }
}
