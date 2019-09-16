package com.facebook.react.uimanager.layoutanimation;

enum InterpolatorType {
    LINEAR("linear"),
    EASE_IN("easeIn"),
    EASE_OUT("easeOut"),
    EASE_IN_EASE_OUT("easeInEaseOut"),
    SPRING("spring");
    
    private final String mName;

    private InterpolatorType(String str) {
        this.mName = str;
    }

    public static InterpolatorType fromString(String str) {
        InterpolatorType[] values;
        for (InterpolatorType interpolatorType : values()) {
            if (interpolatorType.toString().equalsIgnoreCase(str)) {
                return interpolatorType;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unsupported interpolation type : ");
        sb.append(str);
        throw new IllegalArgumentException(sb.toString());
    }

    public String toString() {
        return this.mName;
    }
}
