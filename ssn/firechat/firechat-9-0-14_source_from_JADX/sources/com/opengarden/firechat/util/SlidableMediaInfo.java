package com.opengarden.firechat.util;

import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;
import java.io.Serializable;

public class SlidableMediaInfo implements Serializable {
    public EncryptedFileInfo mEncryptedFileInfo;
    public String mFileName;
    public String mMediaUrl;
    public String mMessageType;
    public String mMimeType;
    public int mOrientation = 0;
    public int mRotationAngle = 0;
    public String mThumbnailUrl;
}
