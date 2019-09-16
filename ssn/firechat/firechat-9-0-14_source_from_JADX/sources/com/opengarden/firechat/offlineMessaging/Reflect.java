package com.opengarden.firechat.offlineMessaging;

import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Reflect {
    Reflect() {
    }

    public static Object get(Object obj, String str) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    public static Object getFromSuperclass(Object obj, String str) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field declaredField = obj.getClass().getSuperclass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    public static int getSuperclassInt(Object obj, String str) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field declaredField = obj.getClass().getSuperclass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.getInt(obj);
    }

    public static Object get(Class<?> cls, String str) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(cls);
    }

    public static void set(Object obj, String str, Object obj2) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        declaredField.set(obj, obj2);
    }

    public static boolean has(Object obj, String str, Object... objArr) {
        boolean z = false;
        try {
            if (methodLookup(obj.getClass(), str, objArr) != null) {
                z = true;
            }
            return z;
        } catch (IllegalArgumentException | NoSuchMethodException unused) {
            return false;
        }
    }

    public static boolean hasAny(Object obj, String str) {
        return hasAny(obj.getClass(), str);
    }

    public static boolean hasAny(Class<?> cls, String str) {
        for (Method name : cls.getMethods()) {
            if (name.getName().equals(str)) {
                return true;
            }
        }
        for (Method name2 : cls.getDeclaredMethods()) {
            if (name2.getName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static Object call(Object obj, String str, Object... objArr) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return callOnClass(obj.getClass(), obj, str, objArr);
    }

    public static Object callOnSuperclass(Object obj, String str, Object... objArr) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return callOnClass(obj.getClass().getSuperclass(), obj, str, objArr);
    }

    public static Object call(Class<?> cls, String str, Object... objArr) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return callOnClass(cls, cls, str, objArr);
    }

    public static Object callOnClass(Class<?> cls, Object obj, String str, Object... objArr) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return methodLookup(cls, str, objArr).invoke(obj, objArr);
    }

    public static Object construct(Class<?> cls, Object... objArr) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor constructor;
        try {
            constructor = fastContrusctorLookup(cls, objArr);
        } catch (IllegalArgumentException | NoSuchMethodException unused) {
            constructor = null;
        }
        if (constructor == null) {
            constructor = slowConstructorLookup(cls, objArr);
            StringBuilder sb = new StringBuilder();
            sb.append("Had to use slowConstructorLookup for ");
            sb.append(constructor);
            Log.d("Reflect", sb.toString());
        }
        return constructor.newInstance(objArr);
    }

    private static Class<?>[] paramTypes(Object[] objArr) {
        Class<?>[] clsArr = new Class[objArr.length];
        for (int i = 0; i < objArr.length; i++) {
            Class<?> cls = objArr[i].getClass();
            clsArr[i] = cls;
            try {
                clsArr[i] = (Class) cls.getField("TYPE").get(null);
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException unused) {
            }
        }
        return clsArr;
    }

    private static boolean paramsMatch(Class<?>[] clsArr, Object[] objArr) {
        if (clsArr.length != objArr.length) {
            return false;
        }
        for (int i = 0; i < objArr.length; i++) {
            Class cls = objArr[i].getClass();
            Class<?> cls2 = clsArr[i];
            try {
                if (cls2.isAssignableFrom((Class) cls.getField("TYPE").get(null))) {
                    continue;
                }
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException unused) {
            }
            if (!cls2.isAssignableFrom(cls)) {
                return false;
            }
        }
        return true;
    }

    private static Constructor<?> fastContrusctorLookup(Class<?> cls, Object... objArr) throws NoSuchMethodException {
        Class[] paramTypes = paramTypes(objArr);
        try {
            return cls.getConstructor(paramTypes);
        } catch (NoSuchMethodException unused) {
            Constructor<?> declaredConstructor = cls.getDeclaredConstructor(paramTypes);
            declaredConstructor.setAccessible(true);
            return declaredConstructor;
        }
    }

    private static Constructor<?> slowConstructorLookup(Class<?> cls, Object... objArr) throws NoSuchMethodException {
        Constructor<?>[] constructors;
        Constructor<?>[] declaredConstructors;
        for (Constructor<?> constructor : cls.getConstructors()) {
            if (paramsMatch(constructor.getParameterTypes(), objArr)) {
                return constructor;
            }
        }
        for (Constructor<?> constructor2 : cls.getDeclaredConstructors()) {
            if (paramsMatch(constructor2.getParameterTypes(), objArr)) {
                constructor2.setAccessible(true);
                return constructor2;
            }
        }
        throw new NoSuchMethodException();
    }

    private static Method methodLookup(Class<?> cls, String str, Object... objArr) throws NoSuchMethodException {
        Method method;
        try {
            method = fastMethodLookup(cls, str, objArr);
        } catch (IllegalArgumentException | NoSuchMethodException unused) {
            method = null;
        }
        if (method != null) {
            return method;
        }
        Method slowMethodLookup = slowMethodLookup(cls, str, objArr);
        StringBuilder sb = new StringBuilder();
        sb.append("Had to use slowMethodLookup for ");
        sb.append(slowMethodLookup);
        Log.d("Reflect", sb.toString());
        return slowMethodLookup;
    }

    private static Method fastMethodLookup(Class<?> cls, String str, Object... objArr) throws NoSuchMethodException {
        Class[] paramTypes = paramTypes(objArr);
        try {
            return cls.getMethod(str, paramTypes);
        } catch (NoSuchMethodException unused) {
            Method declaredMethod = cls.getDeclaredMethod(str, paramTypes);
            declaredMethod.setAccessible(true);
            return declaredMethod;
        }
    }

    private static boolean methodFilter(Method method, String str, Object[] objArr) {
        if (!method.getName().equals(str)) {
            return false;
        }
        return paramsMatch(method.getParameterTypes(), objArr);
    }

    private static Method slowMethodLookup(Class<?> cls, String str, Object... objArr) throws NoSuchMethodException {
        Method[] methods;
        Method[] declaredMethods;
        for (Method method : cls.getMethods()) {
            if (methodFilter(method, str, objArr)) {
                return method;
            }
        }
        for (Method method2 : cls.getDeclaredMethods()) {
            if (methodFilter(method2, str, objArr)) {
                method2.setAccessible(true);
                return method2;
            }
        }
        throw new NoSuchMethodException();
    }
}
