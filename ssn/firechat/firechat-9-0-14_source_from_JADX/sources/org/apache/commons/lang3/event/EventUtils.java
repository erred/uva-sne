package org.apache.commons.lang3.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.reflect.MethodUtils;

public class EventUtils {

    private static class EventBindingInvocationHandler implements InvocationHandler {
        private final Set<String> eventTypes;
        private final String methodName;
        private final Object target;

        EventBindingInvocationHandler(Object obj, String str, String[] strArr) {
            this.target = obj;
            this.methodName = str;
            this.eventTypes = new HashSet(Arrays.asList(strArr));
        }

        public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
            if (!this.eventTypes.isEmpty() && !this.eventTypes.contains(method.getName())) {
                return null;
            }
            if (hasMatchingParametersMethod(method)) {
                return MethodUtils.invokeMethod(this.target, this.methodName, objArr);
            }
            return MethodUtils.invokeMethod(this.target, this.methodName);
        }

        private boolean hasMatchingParametersMethod(Method method) {
            return MethodUtils.getAccessibleMethod(this.target.getClass(), this.methodName, method.getParameterTypes()) != null;
        }
    }

    public static <L> void addEventListener(Object obj, Class<L> cls, L l) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("add");
            sb.append(cls.getSimpleName());
            MethodUtils.invokeMethod(obj, sb.toString(), l);
        } catch (NoSuchMethodException unused) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Class ");
            sb2.append(obj.getClass().getName());
            sb2.append(" does not have a public add");
            sb2.append(cls.getSimpleName());
            sb2.append(" method which takes a parameter of type ");
            sb2.append(cls.getName());
            sb2.append(".");
            throw new IllegalArgumentException(sb2.toString());
        } catch (IllegalAccessException unused2) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Class ");
            sb3.append(obj.getClass().getName());
            sb3.append(" does not have an accessible add");
            sb3.append(cls.getSimpleName());
            sb3.append(" method which takes a parameter of type ");
            sb3.append(cls.getName());
            sb3.append(".");
            throw new IllegalArgumentException(sb3.toString());
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to add listener.", e.getCause());
        }
    }

    public static <L> void bindEventsToMethod(Object obj, String str, Object obj2, Class<L> cls, String... strArr) {
        addEventListener(obj2, cls, cls.cast(Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[]{cls}, new EventBindingInvocationHandler(obj, str, strArr))));
    }
}
