package com.opengarden.firechat.matrixsdk.rest.model.message;

import android.net.Uri;
import com.opengarden.firechat.matrixsdk.crypto.MXEncryptedAttachments.EncryptionResult;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.File;

public class MediaMessage extends Message {
    public static final String LOG_TAG = "MediaMessage";

    public String getMimeType() {
        return null;
    }

    public String getThumbnailUrl() {
        return null;
    }

    public String getUrl() {
        return null;
    }

    public void setThumbnailUrl(EncryptionResult encryptionResult, String str) {
    }

    public void setUrl(EncryptionResult encryptionResult, String str) {
    }

    public boolean isThumbnailLocalContent() {
        String thumbnailUrl = getThumbnailUrl();
        return thumbnailUrl != null && thumbnailUrl.startsWith("file://");
    }

    public boolean isLocalContent() {
        String url = getUrl();
        return url != null && url.startsWith("file://");
    }

    public void checkMediaUrls() {
        String thumbnailUrl = getThumbnailUrl();
        if (thumbnailUrl != null && thumbnailUrl.startsWith("file://")) {
            try {
                if (!new File(Uri.parse(thumbnailUrl).getPath()).exists()) {
                    setThumbnailUrl(null, null);
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## checkMediaUrls() failed");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        String url = getUrl();
        if (url != null && url.startsWith("file://")) {
            try {
                if (!new File(Uri.parse(url).getPath()).exists()) {
                    setUrl(null, null);
                }
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## checkMediaUrls() failed");
                sb2.append(e2.getMessage());
                Log.m211e(str2, sb2.toString());
            }
        }
    }
}
