package com.facebook.react.bridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaOnlyArray implements ReadableArray, WritableArray {
    private final List mBackingList;

    public static JavaOnlyArray from(List list) {
        return new JavaOnlyArray(list);
    }

    /* renamed from: of */
    public static JavaOnlyArray m135of(Object... objArr) {
        return new JavaOnlyArray(objArr);
    }

    private JavaOnlyArray(Object... objArr) {
        this.mBackingList = Arrays.asList(objArr);
    }

    private JavaOnlyArray(List list) {
        this.mBackingList = new ArrayList(list);
    }

    public JavaOnlyArray() {
        this.mBackingList = new ArrayList();
    }

    public int size() {
        return this.mBackingList.size();
    }

    public boolean isNull(int i) {
        return this.mBackingList.get(i) == null;
    }

    public double getDouble(int i) {
        return ((Double) this.mBackingList.get(i)).doubleValue();
    }

    public int getInt(int i) {
        return ((Integer) this.mBackingList.get(i)).intValue();
    }

    public String getString(int i) {
        return (String) this.mBackingList.get(i);
    }

    public JavaOnlyArray getArray(int i) {
        return (JavaOnlyArray) this.mBackingList.get(i);
    }

    public boolean getBoolean(int i) {
        return ((Boolean) this.mBackingList.get(i)).booleanValue();
    }

    public JavaOnlyMap getMap(int i) {
        return (JavaOnlyMap) this.mBackingList.get(i);
    }

    public Dynamic getDynamic(int i) {
        return DynamicFromArray.create(this, i);
    }

    public ReadableType getType(int i) {
        Object obj = this.mBackingList.get(i);
        if (obj == null) {
            return ReadableType.Null;
        }
        if (obj instanceof Boolean) {
            return ReadableType.Boolean;
        }
        if ((obj instanceof Double) || (obj instanceof Float) || (obj instanceof Integer)) {
            return ReadableType.Number;
        }
        if (obj instanceof String) {
            return ReadableType.String;
        }
        if (obj instanceof ReadableArray) {
            return ReadableType.Array;
        }
        if (obj instanceof ReadableMap) {
            return ReadableType.Map;
        }
        return null;
    }

    public void pushBoolean(boolean z) {
        this.mBackingList.add(Boolean.valueOf(z));
    }

    public void pushDouble(double d) {
        this.mBackingList.add(Double.valueOf(d));
    }

    public void pushInt(int i) {
        this.mBackingList.add(Integer.valueOf(i));
    }

    public void pushString(String str) {
        this.mBackingList.add(str);
    }

    public void pushArray(WritableArray writableArray) {
        this.mBackingList.add(writableArray);
    }

    public void pushMap(WritableMap writableMap) {
        this.mBackingList.add(writableMap);
    }

    public void pushNull() {
        this.mBackingList.add(null);
    }

    public ArrayList<Object> toArrayList() {
        return new ArrayList<>(this.mBackingList);
    }

    public String toString() {
        return this.mBackingList.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JavaOnlyArray javaOnlyArray = (JavaOnlyArray) obj;
        return this.mBackingList == null ? javaOnlyArray.mBackingList == null : this.mBackingList.equals(javaOnlyArray.mBackingList);
    }

    public int hashCode() {
        if (this.mBackingList != null) {
            return this.mBackingList.hashCode();
        }
        return 0;
    }
}
