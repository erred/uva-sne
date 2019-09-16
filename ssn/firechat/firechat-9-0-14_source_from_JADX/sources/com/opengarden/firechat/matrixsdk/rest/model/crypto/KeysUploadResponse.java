package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import android.text.TextUtils;
import java.util.Map;

public class KeysUploadResponse {
    public Map<String, Integer> oneTimeKeyCounts;

    public int oneTimeKeyCountsForAlgorithm(String str) {
        if (this.oneTimeKeyCounts != null && !TextUtils.isEmpty(str)) {
            Integer num = (Integer) this.oneTimeKeyCounts.get(str);
            if (num != null) {
                return num.intValue();
            }
        }
        return 0;
    }

    public boolean hasOneTimeKeyCountsForAlgorithm(String str) {
        return (this.oneTimeKeyCounts == null || str == null || !this.oneTimeKeyCounts.containsKey(str)) ? false : true;
    }
}
