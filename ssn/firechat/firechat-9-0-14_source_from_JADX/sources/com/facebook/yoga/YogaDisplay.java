package com.facebook.yoga;

import com.facebook.proguard.annotations.DoNotStrip;

@DoNotStrip
public enum YogaDisplay {
    FLEX(0),
    NONE(1);
    
    private int mIntValue;

    private YogaDisplay(int i) {
        this.mIntValue = i;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaDisplay fromInt(int i) {
        switch (i) {
            case 0:
                return FLEX;
            case 1:
                return NONE;
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("Unknown enum value: ");
                sb.append(i);
                throw new IllegalArgumentException(sb.toString());
        }
    }
}
