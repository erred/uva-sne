package com.opengarden.firechat.matrixsdk.rest.model.sync;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeviceInfo {
    public String device_id;
    public String display_name;
    public String last_seen_ip;
    public long last_seen_ts = 0;
    public String user_id;

    public static void sortByLastSeen(List<DeviceInfo> list) {
        if (list != null) {
            Collections.sort(list, new Comparator<DeviceInfo>() {
                public int compare(DeviceInfo deviceInfo, DeviceInfo deviceInfo2) {
                    if (deviceInfo.last_seen_ts == deviceInfo2.last_seen_ts) {
                        return 0;
                    }
                    return deviceInfo.last_seen_ts > deviceInfo2.last_seen_ts ? -1 : 1;
                }
            });
        }
    }
}
