package org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.ArrayUtils;

public class ReflectionToStringBuilder extends ToStringBuilder {
    private boolean appendStatics = false;
    private boolean appendTransients = false;
    protected String[] excludeFieldNames;
    private Class<?> upToClass = null;

    public static String toString(Object obj) {
        return toString(obj, null, false, false, null);
    }

    public static String toString(Object obj, ToStringStyle toStringStyle) {
        return toString(obj, toStringStyle, false, false, null);
    }

    public static String toString(Object obj, ToStringStyle toStringStyle, boolean z) {
        return toString(obj, toStringStyle, z, false, null);
    }

    public static String toString(Object obj, ToStringStyle toStringStyle, boolean z, boolean z2) {
        return toString(obj, toStringStyle, z, z2, null);
    }

    public static <T> String toString(T t, ToStringStyle toStringStyle, boolean z, boolean z2, Class<? super T> cls) {
        ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(t, toStringStyle, null, cls, z, z2);
        return reflectionToStringBuilder.toString();
    }

    public static String toStringExclude(Object obj, Collection<String> collection) {
        return toStringExclude(obj, toNoNullStringArray(collection));
    }

    static String[] toNoNullStringArray(Collection<String> collection) {
        if (collection == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return toNoNullStringArray(collection.toArray());
    }

    static String[] toNoNullStringArray(Object[] objArr) {
        ArrayList arrayList = new ArrayList(objArr.length);
        for (Object obj : objArr) {
            if (obj != null) {
                arrayList.add(obj.toString());
            }
        }
        return (String[]) arrayList.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static String toStringExclude(Object obj, String... strArr) {
        return new ReflectionToStringBuilder(obj).setExcludeFieldNames(strArr).toString();
    }

    private static Object checkNotNull(Object obj) {
        if (obj != null) {
            return obj;
        }
        throw new IllegalArgumentException("The Object passed in should not be null.");
    }

    public ReflectionToStringBuilder(Object obj) {
        super(checkNotNull(obj));
    }

    public ReflectionToStringBuilder(Object obj, ToStringStyle toStringStyle) {
        super(checkNotNull(obj), toStringStyle);
    }

    public ReflectionToStringBuilder(Object obj, ToStringStyle toStringStyle, StringBuffer stringBuffer) {
        super(checkNotNull(obj), toStringStyle, stringBuffer);
    }

    public <T> ReflectionToStringBuilder(T t, ToStringStyle toStringStyle, StringBuffer stringBuffer, Class<? super T> cls, boolean z, boolean z2) {
        super(checkNotNull(t), toStringStyle, stringBuffer);
        setUpToClass(cls);
        setAppendTransients(z);
        setAppendStatics(z2);
    }

    /* access modifiers changed from: protected */
    public boolean accept(Field field) {
        if (field.getName().indexOf(36) != -1) {
            return false;
        }
        if (Modifier.isTransient(field.getModifiers()) && !isAppendTransients()) {
            return false;
        }
        if (Modifier.isStatic(field.getModifiers()) && !isAppendStatics()) {
            return false;
        }
        if ((this.excludeFieldNames == null || Arrays.binarySearch(this.excludeFieldNames, field.getName()) < 0) && !field.isAnnotationPresent(ToStringExclude.class)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void appendFieldsIn(Class<?> cls) {
        if (cls.isArray()) {
            reflectionAppendArray(getObject());
            return;
        }
        Field[] declaredFields = cls.getDeclaredFields();
        AccessibleObject.setAccessible(declaredFields, true);
        for (Field field : declaredFields) {
            String name = field.getName();
            if (accept(field)) {
                try {
                    append(name, getValue(field));
                } catch (IllegalAccessException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unexpected IllegalAccessException: ");
                    sb.append(e.getMessage());
                    throw new InternalError(sb.toString());
                }
            }
        }
    }

    public String[] getExcludeFieldNames() {
        return (String[]) this.excludeFieldNames.clone();
    }

    public Class<?> getUpToClass() {
        return this.upToClass;
    }

    /* access modifiers changed from: protected */
    public Object getValue(Field field) throws IllegalArgumentException, IllegalAccessException {
        return field.get(getObject());
    }

    public boolean isAppendStatics() {
        return this.appendStatics;
    }

    public boolean isAppendTransients() {
        return this.appendTransients;
    }

    public ReflectionToStringBuilder reflectionAppendArray(Object obj) {
        getStyle().reflectionAppendArrayDetail(getStringBuffer(), null, obj);
        return this;
    }

    public void setAppendStatics(boolean z) {
        this.appendStatics = z;
    }

    public void setAppendTransients(boolean z) {
        this.appendTransients = z;
    }

    public ReflectionToStringBuilder setExcludeFieldNames(String... strArr) {
        if (strArr == null) {
            this.excludeFieldNames = null;
        } else {
            this.excludeFieldNames = toNoNullStringArray((Object[]) strArr);
            Arrays.sort(this.excludeFieldNames);
        }
        return this;
    }

    public void setUpToClass(Class<?> cls) {
        if (cls != null) {
            Object object = getObject();
            if (object != null && !cls.isInstance(object)) {
                throw new IllegalArgumentException("Specified class is not a superclass of the object");
            }
        }
        this.upToClass = cls;
    }

    public String toString() {
        if (getObject() == null) {
            return getStyle().getNullText();
        }
        Class cls = getObject().getClass();
        appendFieldsIn(cls);
        while (cls.getSuperclass() != null && cls != getUpToClass()) {
            cls = cls.getSuperclass();
            appendFieldsIn(cls);
        }
        return super.toString();
    }
}
