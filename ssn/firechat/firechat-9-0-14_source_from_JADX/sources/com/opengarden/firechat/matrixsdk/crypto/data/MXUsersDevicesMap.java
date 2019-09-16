package com.opengarden.firechat.matrixsdk.crypto.data;

import android.text.TextUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MXUsersDevicesMap<E> implements Serializable {
    private final HashMap<String, HashMap<String, E>> mMap = new HashMap<>();

    public HashMap<String, HashMap<String, E>> getMap() {
        return this.mMap;
    }

    public MXUsersDevicesMap() {
    }

    public MXUsersDevicesMap(Map<String, Map<String, E>> map) {
        if (map != null) {
            for (String str : map.keySet()) {
                this.mMap.put(str, new HashMap((Map) map.get(str)));
            }
        }
    }

    public MXUsersDevicesMap<E> deepCopy() {
        MXUsersDevicesMap<E> mXUsersDevicesMap = new MXUsersDevicesMap<>();
        for (String str : this.mMap.keySet()) {
            mXUsersDevicesMap.mMap.put(str, new HashMap((Map) this.mMap.get(str)));
        }
        return mXUsersDevicesMap;
    }

    public List<String> getUserIds() {
        return new ArrayList(this.mMap.keySet());
    }

    public List<String> getUserDeviceIds(String str) {
        if (TextUtils.isEmpty(str) || !this.mMap.containsKey(str)) {
            return null;
        }
        return new ArrayList(((HashMap) this.mMap.get(str)).keySet());
    }

    public E getObject(String str, String str2) {
        if (TextUtils.isEmpty(str2) || !this.mMap.containsKey(str2) || TextUtils.isEmpty(str)) {
            return null;
        }
        return ((HashMap) this.mMap.get(str2)).get(str);
    }

    public void setObject(E e, String str, String str2) {
        if (e != null && !TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            HashMap hashMap = (HashMap) this.mMap.get(str);
            if (hashMap == null) {
                hashMap = new HashMap();
                this.mMap.put(str, hashMap);
            }
            hashMap.put(str2, e);
        }
    }

    public void setObjects(Map<String, E> map, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (map == null) {
            this.mMap.remove(str);
        } else {
            this.mMap.put(str, new HashMap(map));
        }
    }

    public void removeUserObjects(String str) {
        if (!TextUtils.isEmpty(str)) {
            this.mMap.remove(str);
        }
    }

    public void removeAllObjects() {
        this.mMap.clear();
    }

    public void addEntriesFromMap(MXUsersDevicesMap<E> mXUsersDevicesMap) {
        if (mXUsersDevicesMap != null) {
            this.mMap.putAll(mXUsersDevicesMap.getMap());
        }
    }

    public String toString() {
        if (this.mMap == null) {
            return "MXDeviceInfo : null map";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("MXUsersDevicesMap ");
        sb.append(this.mMap.toString());
        return sb.toString();
    }
}
