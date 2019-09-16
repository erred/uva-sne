package com.opengarden.firechat.matrixsdk.rest.model.message;

import android.net.Uri;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.File;

public class LocationMessage extends Message {
    private static final String LOG_TAG = "LocationMessage";
    public String geo_uri;
    public ThumbnailInfo thumbnail_info;
    public String thumbnail_url;

    public LocationMessage() {
        this.msgtype = Message.MSGTYPE_LOCATION;
    }

    public LocationMessage deepCopy() {
        LocationMessage locationMessage = new LocationMessage();
        locationMessage.msgtype = this.msgtype;
        locationMessage.body = this.body;
        locationMessage.geo_uri = this.geo_uri;
        locationMessage.thumbnail_url = this.thumbnail_url;
        if (this.thumbnail_info != null) {
            locationMessage.thumbnail_info = this.thumbnail_info.deepCopy();
        }
        return locationMessage;
    }

    public boolean isLocalThumbnailContent() {
        return this.thumbnail_url != null && this.thumbnail_url.startsWith("file://");
    }

    public void checkMediaUrls() {
        if (this.thumbnail_url != null && this.thumbnail_url.startsWith("file://")) {
            try {
                if (!new File(Uri.parse(this.thumbnail_url).getPath()).exists()) {
                    this.thumbnail_url = null;
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## checkMediaUrls() failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }
}
