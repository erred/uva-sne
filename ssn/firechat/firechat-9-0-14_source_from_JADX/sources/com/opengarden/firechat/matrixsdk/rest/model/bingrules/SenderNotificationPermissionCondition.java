package com.opengarden.firechat.matrixsdk.rest.model.bingrules;

import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;

public class SenderNotificationPermissionCondition extends Condition {
    private static final String LOG_TAG = "SenderNotificationPermissionCondition";
    public String key;

    public SenderNotificationPermissionCondition() {
        this.kind = Condition.KIND_SENDER_NOTIFICATION_PERMISSION;
    }

    public boolean isSatisfied(PowerLevels powerLevels, String str) {
        return (powerLevels == null || str == null || powerLevels.getUserPowerLevel(str) < powerLevels.notificationLevel(this.key)) ? false : true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SenderNotificationPermissionCondition{key=");
        sb.append(this.key);
        return sb.toString();
    }
}
