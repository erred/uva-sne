package com.opengarden.firechat.widgets;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import java.io.Serializable;
import java.net.URLEncoder;

public class Widget implements Serializable {
    private String mSessionId;
    private String mUrl;
    private WidgetContent mWidgetContent;
    private Event mWidgetEvent;
    private String mWidgetId;

    public Widget(MXSession mXSession, Event event) throws Exception {
        if (!TextUtils.equals(event.type, WidgetsManager.WIDGET_EVENT_TYPE)) {
            StringBuilder sb = new StringBuilder();
            sb.append("unsupported event type ");
            sb.append(event.type);
            throw new Exception(sb.toString());
        }
        this.mWidgetId = event.stateKey;
        this.mWidgetEvent = event;
        this.mSessionId = mXSession.getMyUserId();
        this.mWidgetContent = WidgetContent.toWidgetContent(event.getContentAsJsonObject());
        this.mUrl = this.mWidgetContent.url;
        if (this.mUrl != null) {
            this.mUrl = this.mUrl.replace("$matrix_user_id", mXSession.getMyUserId());
            String str = mXSession.getMyUser().displayname;
            String str2 = this.mUrl;
            String str3 = "$matrix_display_name";
            if (str == null) {
                str = mXSession.getMyUserId();
            }
            this.mUrl = str2.replace(str3, str);
            String avatarUrl = mXSession.getMyUser().getAvatarUrl();
            String str4 = this.mUrl;
            String str5 = "$matrix_avatar_url";
            if (avatarUrl == null) {
                avatarUrl = "";
            }
            this.mUrl = str4.replace(str5, avatarUrl);
        }
        if (this.mWidgetContent.data != null) {
            for (String str6 : this.mWidgetContent.data.keySet()) {
                Object obj = this.mWidgetContent.data.get(str6);
                if (obj instanceof String) {
                    String str7 = this.mUrl;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("$");
                    sb2.append(str6);
                    this.mUrl = str7.replace(sb2.toString(), URLEncoder.encode((String) obj, "utf-8"));
                }
            }
        }
    }

    public boolean isActive() {
        return (this.mWidgetContent.type == null || this.mUrl == null) ? false : true;
    }

    public String getWidgetId() {
        return this.mWidgetId;
    }

    public Event getWidgetEvent() {
        return this.mWidgetEvent;
    }

    public String getSessionId() {
        return this.mSessionId;
    }

    public String getRoomId() {
        return this.mWidgetEvent.roomId;
    }

    private String getType() {
        return this.mWidgetContent.type;
    }

    public String getUrl() {
        return this.mUrl;
    }

    private String getName() {
        return this.mWidgetContent.name;
    }

    public String getHumanName() {
        return this.mWidgetContent.getHumanName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Widget: ");
        sb.append(this);
        sb.append("p> id: ");
        sb.append(getWidgetId());
        sb.append(" - type: ");
        sb.append(getType());
        sb.append(" - name: ");
        sb.append(getName());
        sb.append(" - url: ");
        sb.append(getUrl());
        return sb.toString();
    }
}
