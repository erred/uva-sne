package com.opengarden.firechat.matrixsdk.rest.model.bingrules;

public class DeviceCondition extends Condition {
    public String profileTag;

    public DeviceCondition() {
        this.kind = Condition.KIND_DEVICE;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeviceCondition{profileTag='");
        sb.append(this.profileTag);
        sb.append("'}'");
        return sb.toString();
    }
}
