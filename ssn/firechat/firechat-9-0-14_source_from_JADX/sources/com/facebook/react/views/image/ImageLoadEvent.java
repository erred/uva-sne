package com.facebook.react.views.image;

import com.RNFetchBlob.RNFetchBlobConst;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import javax.annotation.Nullable;

public class ImageLoadEvent extends Event<ImageLoadEvent> {
    public static final int ON_ERROR = 1;
    public static final int ON_LOAD = 2;
    public static final int ON_LOAD_END = 3;
    public static final int ON_LOAD_START = 4;
    public static final int ON_PROGRESS = 5;
    private final int mEventType;
    private final int mHeight;
    @Nullable
    private final String mImageUri;
    private final int mWidth;

    public ImageLoadEvent(int i, int i2) {
        this(i, i2, null);
    }

    public ImageLoadEvent(int i, int i2, String str) {
        this(i, i2, str, 0, 0);
    }

    public ImageLoadEvent(int i, int i2, @Nullable String str, int i3, int i4) {
        super(i);
        this.mEventType = i2;
        this.mImageUri = str;
        this.mWidth = i3;
        this.mHeight = i4;
    }

    public static String eventNameForType(int i) {
        switch (i) {
            case 1:
                return "topError";
            case 2:
                return "topLoad";
            case 3:
                return "topLoadEnd";
            case 4:
                return "topLoadStart";
            case 5:
                return "topProgress";
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("Invalid image event: ");
                sb.append(Integer.toString(i));
                throw new IllegalStateException(sb.toString());
        }
    }

    public String getEventName() {
        return eventNameForType(this.mEventType);
    }

    public short getCoalescingKey() {
        return (short) this.mEventType;
    }

    public void dispatch(RCTEventEmitter rCTEventEmitter) {
        WritableMap writableMap;
        if (this.mImageUri != null || this.mEventType == 2) {
            writableMap = Arguments.createMap();
            if (this.mImageUri != null) {
                writableMap.putString(RNFetchBlobConst.DATA_ENCODE_URI, this.mImageUri);
            }
            if (this.mEventType == 2) {
                WritableMap createMap = Arguments.createMap();
                createMap.putDouble("width", (double) this.mWidth);
                createMap.putDouble("height", (double) this.mHeight);
                if (this.mImageUri != null) {
                    createMap.putString(ImagesContract.URL, this.mImageUri);
                }
                writableMap.putMap(Param.SOURCE, createMap);
            }
        } else {
            writableMap = null;
        }
        rCTEventEmitter.receiveEvent(getViewTag(), getEventName(), writableMap);
    }
}
