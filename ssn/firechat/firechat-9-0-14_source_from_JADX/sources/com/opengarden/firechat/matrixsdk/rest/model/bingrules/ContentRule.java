package com.opengarden.firechat.matrixsdk.rest.model.bingrules;

public class ContentRule extends BingRule {
    public String pattern;

    public ContentRule(String str, String str2, boolean z, boolean z2, boolean z3) {
        super(str, str2, Boolean.valueOf(z), Boolean.valueOf(z2), z3);
        this.pattern = str2;
    }
}
