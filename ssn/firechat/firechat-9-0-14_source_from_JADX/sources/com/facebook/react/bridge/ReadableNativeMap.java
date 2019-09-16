package com.facebook.react.bridge;

import com.facebook.jni.HybridData;
import com.facebook.proguard.annotations.DoNotStrip;
import java.util.HashMap;

@DoNotStrip
public class ReadableNativeMap extends NativeMap implements ReadableMap {

    @DoNotStrip
    private static class ReadableNativeMapKeySetIterator implements ReadableMapKeySetIterator {
        @DoNotStrip
        private final HybridData mHybridData;
        @DoNotStrip
        private final ReadableNativeMap mMap;

        private static native HybridData initHybrid(ReadableNativeMap readableNativeMap);

        public native boolean hasNextKey();

        public native String nextKey();

        public ReadableNativeMapKeySetIterator(ReadableNativeMap readableNativeMap) {
            this.mMap = readableNativeMap;
            this.mHybridData = initHybrid(readableNativeMap);
        }
    }

    public native ReadableNativeArray getArray(String str);

    public native boolean getBoolean(String str);

    public native double getDouble(String str);

    public native int getInt(String str);

    public native ReadableNativeMap getMap(String str);

    public native String getString(String str);

    public native ReadableType getType(String str);

    public native boolean hasKey(String str);

    public native boolean isNull(String str);

    static {
        ReactBridge.staticInit();
    }

    protected ReadableNativeMap(HybridData hybridData) {
        super(hybridData);
    }

    public Dynamic getDynamic(String str) {
        return DynamicFromMap.create(this, str);
    }

    public ReadableMapKeySetIterator keySetIterator() {
        return new ReadableNativeMapKeySetIterator(this);
    }

    public HashMap<String, Object> toHashMap() {
        ReadableMapKeySetIterator keySetIterator = keySetIterator();
        HashMap<String, Object> hashMap = new HashMap<>();
        while (keySetIterator.hasNextKey()) {
            String nextKey = keySetIterator.nextKey();
            switch (getType(nextKey)) {
                case Null:
                    hashMap.put(nextKey, null);
                    break;
                case Boolean:
                    hashMap.put(nextKey, Boolean.valueOf(getBoolean(nextKey)));
                    break;
                case Number:
                    hashMap.put(nextKey, Double.valueOf(getDouble(nextKey)));
                    break;
                case String:
                    hashMap.put(nextKey, getString(nextKey));
                    break;
                case Map:
                    hashMap.put(nextKey, getMap(nextKey).toHashMap());
                    break;
                case Array:
                    hashMap.put(nextKey, getArray(nextKey).toArrayList());
                    break;
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append("Could not convert object with key: ");
                    sb.append(nextKey);
                    sb.append(".");
                    throw new IllegalArgumentException(sb.toString());
            }
        }
        return hashMap;
    }
}
