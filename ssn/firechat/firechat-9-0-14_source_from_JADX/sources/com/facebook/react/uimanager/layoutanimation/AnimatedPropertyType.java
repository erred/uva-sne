package com.facebook.react.uimanager.layoutanimation;

import com.facebook.react.uimanager.ViewProps;

enum AnimatedPropertyType {
    OPACITY(ViewProps.OPACITY),
    SCALE_XY("scaleXY");
    
    private final String mName;

    private AnimatedPropertyType(String str) {
        this.mName = str;
    }

    public static AnimatedPropertyType fromString(String str) {
        AnimatedPropertyType[] values;
        for (AnimatedPropertyType animatedPropertyType : values()) {
            if (animatedPropertyType.toString().equalsIgnoreCase(str)) {
                return animatedPropertyType;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unsupported animated property : ");
        sb.append(str);
        throw new IllegalArgumentException(sb.toString());
    }

    public String toString() {
        return this.mName;
    }
}
