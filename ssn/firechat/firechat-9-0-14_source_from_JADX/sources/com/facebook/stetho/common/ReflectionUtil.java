package com.facebook.stetho.common;

import java.lang.reflect.Field;
import javax.annotation.Nullable;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    @Nullable
    public static Class<?> tryGetClassForName(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException unused) {
            return null;
        }
    }

    @Nullable
    public static Field tryGetDeclaredField(Class<?> cls, String str) {
        try {
            return cls.getDeclaredField(str);
        } catch (NoSuchFieldException e) {
            LogUtil.m160d(e, "Could not retrieve %s field from %s", str, cls);
            return null;
        }
    }

    @Nullable
    public static Object getFieldValue(Field field, Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
