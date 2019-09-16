package com.opengarden.firechat.widgets;

import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class WidgetContent implements Serializable {
    private static final String LOG_TAG = "WidgetContent";
    public String creatorUserId;
    public Map<String, Object> data;

    /* renamed from: id */
    public String f143id;
    public String name;
    public String type;
    public String url;

    public String getHumanName() {
        if (!TextUtils.isEmpty(this.name)) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.name);
            sb.append(" widget");
            return sb.toString();
        } else if (TextUtils.isEmpty(this.type)) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Widget ");
            sb2.append(this.f143id);
            return sb2.toString();
        } else if (this.type.contains("widget")) {
            return this.type;
        } else {
            if (this.f143id != null) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(this.type);
                sb3.append(StringUtils.SPACE);
                sb3.append(this.f143id);
                return sb3.toString();
            }
            StringBuilder sb4 = new StringBuilder();
            sb4.append(this.type);
            sb4.append(" widget");
            return sb4.toString();
        }
    }

    public static WidgetContent toWidgetContent(JsonElement jsonElement) {
        try {
            return (WidgetContent) JsonUtils.getGson(false).fromJson(jsonElement, WidgetContent.class);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## toWidgetContent() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return new WidgetContent();
        }
    }
}
