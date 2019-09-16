package com.opengarden.firechat.matrixsdk.rest.model;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import java.io.Serializable;
import java.util.Map;

public class URLPreview implements Serializable {
    private static final String OG_DESCRIPTION = "og:description";
    private static final String OG_IMAGE = "og:image";
    private static final String OG_IMAGE_HEIGHT = "og:image:height";
    private static final String OG_IMAGE_SIZE = "matrix:image:size";
    private static final String OG_IMAGE_TYPE = "og:image:type";
    private static final String OG_IMAGE_WIDTH = "og:image:width";
    private static final String OG_SITE_NAME = "og:site_name";
    private static final String OG_TITLE = "og:title";
    private static final String OG_TYPE = "og:type";
    private static final String OG_URL = "og:url";
    private final String mDescription;
    private boolean mIsDismissed;
    private final String mRequestedURL;
    private final String mSiteName;
    private final String mThumbnailMimeType;
    private final String mThumbnailURL;
    private final String mTitle = "";
    private final String mType;

    public String getTitle() {
        return "";
    }

    public URLPreview(Map<String, Object> map, String str) {
        this.mDescription = JsonUtils.getAsString(map, OG_DESCRIPTION);
        this.mType = JsonUtils.getAsString(map, OG_TYPE);
        this.mSiteName = JsonUtils.getAsString(map, OG_SITE_NAME);
        String asString = JsonUtils.getAsString(map, OG_URL);
        if (TextUtils.isEmpty(asString)) {
            this.mRequestedURL = str;
        } else {
            this.mRequestedURL = asString;
        }
        this.mThumbnailURL = JsonUtils.getAsString(map, OG_IMAGE);
        this.mThumbnailMimeType = JsonUtils.getAsString(map, OG_IMAGE_TYPE);
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getType() {
        return this.mType;
    }

    public String getSiteName() {
        return this.mSiteName;
    }

    public String getRequestedURL() {
        return this.mRequestedURL;
    }

    public String getThumbnailURL() {
        return this.mThumbnailURL;
    }

    public String getThumbnailMimeType() {
        return this.mThumbnailMimeType;
    }

    public boolean IsDismissed() {
        return this.mIsDismissed;
    }

    public void setIsDismissed(boolean z) {
        this.mIsDismissed = z;
    }
}
