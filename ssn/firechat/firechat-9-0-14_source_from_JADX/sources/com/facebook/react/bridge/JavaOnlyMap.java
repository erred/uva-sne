package com.facebook.react.bridge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JavaOnlyMap implements ReadableMap, WritableMap {
    /* access modifiers changed from: private */
    public final Map mBackingMap;

    /* renamed from: of */
    public static JavaOnlyMap m136of(Object... objArr) {
        return new JavaOnlyMap(objArr);
    }

    private JavaOnlyMap(Object... objArr) {
        if (objArr.length % 2 != 0) {
            throw new IllegalArgumentException("You must provide the same number of keys and values");
        }
        this.mBackingMap = new HashMap();
        for (int i = 0; i < objArr.length; i += 2) {
            this.mBackingMap.put(objArr[i], objArr[i + 1]);
        }
    }

    public JavaOnlyMap() {
        this.mBackingMap = new HashMap();
    }

    public boolean hasKey(String str) {
        return this.mBackingMap.containsKey(str);
    }

    public boolean isNull(String str) {
        return this.mBackingMap.get(str) == null;
    }

    public boolean getBoolean(String str) {
        return ((Boolean) this.mBackingMap.get(str)).booleanValue();
    }

    public double getDouble(String str) {
        return ((Double) this.mBackingMap.get(str)).doubleValue();
    }

    public int getInt(String str) {
        return ((Integer) this.mBackingMap.get(str)).intValue();
    }

    public String getString(String str) {
        return (String) this.mBackingMap.get(str);
    }

    public JavaOnlyMap getMap(String str) {
        return (JavaOnlyMap) this.mBackingMap.get(str);
    }

    public JavaOnlyArray getArray(String str) {
        return (JavaOnlyArray) this.mBackingMap.get(str);
    }

    public Dynamic getDynamic(String str) {
        return DynamicFromMap.create(this, str);
    }

    public ReadableType getType(String str) {
        Object obj = this.mBackingMap.get(str);
        if (obj == null) {
            return ReadableType.Null;
        }
        if (obj instanceof Number) {
            return ReadableType.Number;
        }
        if (obj instanceof String) {
            return ReadableType.String;
        }
        if (obj instanceof Boolean) {
            return ReadableType.Boolean;
        }
        if (obj instanceof ReadableMap) {
            return ReadableType.Map;
        }
        if (obj instanceof ReadableArray) {
            return ReadableType.Array;
        }
        if (obj instanceof Dynamic) {
            return ((Dynamic) obj).getType();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid value ");
        sb.append(obj.toString());
        sb.append(" for key ");
        sb.append(str);
        sb.append("contained in JavaOnlyMap");
        throw new IllegalArgumentException(sb.toString());
    }

    public ReadableMapKeySetIterator keySetIterator() {
        return new ReadableMapKeySetIterator() {
            Iterator<String> mIterator = JavaOnlyMap.this.mBackingMap.keySet().iterator();

            public boolean hasNextKey() {
                return this.mIterator.hasNext();
            }

            public String nextKey() {
                return (String) this.mIterator.next();
            }
        };
    }

    public void putBoolean(String str, boolean z) {
        this.mBackingMap.put(str, Boolean.valueOf(z));
    }

    public void putDouble(String str, double d) {
        this.mBackingMap.put(str, Double.valueOf(d));
    }

    public void putInt(String str, int i) {
        this.mBackingMap.put(str, Integer.valueOf(i));
    }

    public void putString(String str, String str2) {
        this.mBackingMap.put(str, str2);
    }

    public void putNull(String str) {
        this.mBackingMap.put(str, null);
    }

    public void putMap(String str, WritableMap writableMap) {
        this.mBackingMap.put(str, writableMap);
    }

    public void merge(ReadableMap readableMap) {
        this.mBackingMap.putAll(((JavaOnlyMap) readableMap).mBackingMap);
    }

    public void putArray(String str, WritableArray writableArray) {
        this.mBackingMap.put(str, writableArray);
    }

    public HashMap<String, Object> toHashMap() {
        return new HashMap<>(this.mBackingMap);
    }

    public String toString() {
        return this.mBackingMap.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JavaOnlyMap javaOnlyMap = (JavaOnlyMap) obj;
        return this.mBackingMap == null ? javaOnlyMap.mBackingMap == null : this.mBackingMap.equals(javaOnlyMap.mBackingMap);
    }

    public int hashCode() {
        if (this.mBackingMap != null) {
            return this.mBackingMap.hashCode();
        }
        return 0;
    }
}
