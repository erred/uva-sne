package org.apache.commons.lang3.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.ClassUtils;

abstract class MemberUtils {
    private static final int ACCESS_TEST = 7;
    private static final Class<?>[] ORDERED_PRIMITIVE_TYPES = {Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};

    private static final class Executable {
        private final boolean isVarArgs;
        private final Class<?>[] parameterTypes;

        /* access modifiers changed from: private */
        /* renamed from: of */
        public static Executable m306of(Method method) {
            return new Executable(method);
        }

        /* access modifiers changed from: private */
        /* renamed from: of */
        public static Executable m305of(Constructor<?> constructor) {
            return new Executable(constructor);
        }

        private Executable(Method method) {
            this.parameterTypes = method.getParameterTypes();
            this.isVarArgs = method.isVarArgs();
        }

        private Executable(Constructor<?> constructor) {
            this.parameterTypes = constructor.getParameterTypes();
            this.isVarArgs = constructor.isVarArgs();
        }

        public Class<?>[] getParameterTypes() {
            return this.parameterTypes;
        }

        public boolean isVarArgs() {
            return this.isVarArgs;
        }
    }

    static boolean isPackageAccess(int i) {
        return (i & 7) == 0;
    }

    MemberUtils() {
    }

    static boolean setAccessibleWorkaround(AccessibleObject accessibleObject) {
        if (accessibleObject == null || accessibleObject.isAccessible()) {
            return false;
        }
        Member member = (Member) accessibleObject;
        if (!accessibleObject.isAccessible() && Modifier.isPublic(member.getModifiers()) && isPackageAccess(member.getDeclaringClass().getModifiers())) {
            try {
                accessibleObject.setAccessible(true);
                return true;
            } catch (SecurityException unused) {
            }
        }
        return false;
    }

    static boolean isAccessible(Member member) {
        return member != null && Modifier.isPublic(member.getModifiers()) && !member.isSynthetic();
    }

    static int compareConstructorFit(Constructor<?> constructor, Constructor<?> constructor2, Class<?>[] clsArr) {
        return compareParameterTypes(Executable.m305of(constructor), Executable.m305of(constructor2), clsArr);
    }

    static int compareMethodFit(Method method, Method method2, Class<?>[] clsArr) {
        return compareParameterTypes(Executable.m306of(method), Executable.m306of(method2), clsArr);
    }

    private static int compareParameterTypes(Executable executable, Executable executable2, Class<?>[] clsArr) {
        float totalTransformationCost = getTotalTransformationCost(clsArr, executable);
        float totalTransformationCost2 = getTotalTransformationCost(clsArr, executable2);
        if (totalTransformationCost < totalTransformationCost2) {
            return -1;
        }
        return totalTransformationCost2 < totalTransformationCost ? 1 : 0;
    }

    private static float getTotalTransformationCost(Class<?>[] clsArr, Executable executable) {
        Class[] parameterTypes = executable.getParameterTypes();
        boolean isVarArgs = executable.isVarArgs();
        long length = (long) (isVarArgs ? parameterTypes.length - 1 : parameterTypes.length);
        if (((long) clsArr.length) < length) {
            return Float.MAX_VALUE;
        }
        boolean z = false;
        float f = 0.0f;
        for (int i = 0; ((long) i) < length; i++) {
            f += getObjectTransformationCost(clsArr[i], parameterTypes[i]);
        }
        if (isVarArgs) {
            boolean z2 = clsArr.length < parameterTypes.length;
            if (clsArr.length == parameterTypes.length && clsArr[clsArr.length - 1].isArray()) {
                z = true;
            }
            Class componentType = parameterTypes[parameterTypes.length - 1].getComponentType();
            if (z2) {
                f += getObjectTransformationCost(componentType, Object.class) + 0.001f;
            } else if (z) {
                f += getObjectTransformationCost(clsArr[clsArr.length - 1].getComponentType(), componentType) + 0.001f;
            } else {
                for (int length2 = parameterTypes.length - 1; length2 < clsArr.length; length2++) {
                    f += getObjectTransformationCost(clsArr[length2], componentType) + 0.001f;
                }
            }
        }
        return f;
    }

    private static float getObjectTransformationCost(Class<?> cls, Class<?> cls2) {
        if (cls2.isPrimitive()) {
            return getPrimitivePromotionCost(cls, cls2);
        }
        float f = 0.0f;
        while (true) {
            if (cls != null && !cls2.equals(cls)) {
                if (cls2.isInterface() && ClassUtils.isAssignable(cls, cls2)) {
                    f += 0.25f;
                    break;
                }
                f += 1.0f;
                cls = cls.getSuperclass();
            } else {
                break;
            }
        }
        if (cls == null) {
            f += 1.5f;
        }
        return f;
    }

    private static float getPrimitivePromotionCost(Class<?> cls, Class<?> cls2) {
        float f;
        if (!cls.isPrimitive()) {
            cls = ClassUtils.wrapperToPrimitive(cls);
            f = 0.1f;
        } else {
            f = 0.0f;
        }
        int i = 0;
        while (cls != cls2 && i < ORDERED_PRIMITIVE_TYPES.length) {
            if (cls == ORDERED_PRIMITIVE_TYPES[i]) {
                f += 0.1f;
                if (i < ORDERED_PRIMITIVE_TYPES.length - 1) {
                    cls = ORDERED_PRIMITIVE_TYPES[i + 1];
                }
            }
            i++;
        }
        return f;
    }

    static boolean isMatchingMethod(Method method, Class<?>[] clsArr) {
        return isMatchingExecutable(Executable.m306of(method), clsArr);
    }

    static boolean isMatchingConstructor(Constructor<?> constructor, Class<?>[] clsArr) {
        return isMatchingExecutable(Executable.m305of(constructor), clsArr);
    }

    private static boolean isMatchingExecutable(Executable executable, Class<?>[] clsArr) {
        Class[] parameterTypes = executable.getParameterTypes();
        if (!executable.isVarArgs()) {
            return ClassUtils.isAssignable(clsArr, (Class<?>[]) parameterTypes, true);
        }
        int i = 0;
        while (i < parameterTypes.length - 1 && i < clsArr.length) {
            if (!ClassUtils.isAssignable(clsArr[i], parameterTypes[i], true)) {
                return false;
            }
            i++;
        }
        Class componentType = parameterTypes[parameterTypes.length - 1].getComponentType();
        while (i < clsArr.length) {
            if (!ClassUtils.isAssignable(clsArr[i], componentType, true)) {
                return false;
            }
            i++;
        }
        return true;
    }
}
