package com.opengarden.firechat.util;

public class CountryPhoneData {
    private final int mCallingCode;
    private final String mCountryCode;
    private final String mCountryName;

    CountryPhoneData(String str, String str2, int i) {
        this.mCountryCode = str;
        this.mCountryName = str2;
        this.mCallingCode = i;
    }

    public String getCountryCode() {
        return this.mCountryCode;
    }

    public String getCountryName() {
        return this.mCountryName;
    }

    public int getCallingCode() {
        return this.mCallingCode;
    }

    public String getFormattedCallingCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        sb.append(this.mCallingCode);
        return sb.toString();
    }
}
