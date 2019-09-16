package com.opengarden.firechat.matrixsdk.rest.model.bingrules;

import com.opengarden.firechat.matrixsdk.rest.model.Event;

public class UnknownCondition extends Condition {
    public boolean isSatisfied(Event event) {
        return false;
    }

    public String toString() {
        return "UnknownCondition";
    }

    public UnknownCondition() {
        this.kind = Condition.KIND_UNKNOWN;
    }
}
