package com.opengarden.firechat.matrixsdk.rest.model;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.listeners.IMXEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class User implements Serializable {
    public static final String PRESENCE_FREE_FOR_CHAT = "free_for_chat";
    public static final String PRESENCE_HIDDEN = "hidden";
    public static final String PRESENCE_OFFLINE = "offline";
    public static final String PRESENCE_ONLINE = "online";
    public static final String PRESENCE_UNAVAILABLE = "unavailable";
    private static final long serialVersionUID = 5234056937639712713L;
    public String avatar_url;
    public Boolean currently_active;
    public String displayname;
    public Long lastActiveAgo;
    /* access modifiers changed from: protected */
    public transient MXDataHandler mDataHandler;
    private transient Map<IMXEventListener, IMXEventListener> mEventListeners = new HashMap();
    private transient boolean mIsPresenceRefreshed;
    private boolean mIsRetrievedFromRoomMember = false;
    private long mLastPresenceTs;
    private transient ArrayList<IMXEventListener> mPendingListeners = new ArrayList<>();
    private Integer mStorageHashKey = null;
    public String presence;
    public String statusMsg;
    public String user_id;

    public String getAvatarUrl() {
        return this.avatar_url;
    }

    public void setAvatarUrl(String str) {
        this.avatar_url = str;
    }

    public boolean isRetrievedFromRoomMember() {
        return this.mIsRetrievedFromRoomMember;
    }

    public void setRetrievedFromRoomMember() {
        this.mIsRetrievedFromRoomMember = true;
    }

    private Map<IMXEventListener, IMXEventListener> getEventListeners() {
        if (this.mEventListeners == null) {
            this.mEventListeners = new HashMap();
        }
        return this.mEventListeners;
    }

    private ArrayList<IMXEventListener> getPendingListeners() {
        if (this.mPendingListeners == null) {
            this.mPendingListeners = new ArrayList<>();
        }
        return this.mPendingListeners;
    }

    public int getStorageHashKey() {
        if (this.mStorageHashKey == null) {
            this.mStorageHashKey = Integer.valueOf(Math.abs(this.user_id.hashCode() % 100));
        }
        return this.mStorageHashKey.intValue();
    }

    public boolean isPresenceObsolete() {
        return !this.mIsPresenceRefreshed || this.presence == null;
    }

    /* access modifiers changed from: protected */
    public void clone(User user) {
        if (user != null) {
            this.user_id = user.user_id;
            this.displayname = user.displayname;
            this.avatar_url = user.avatar_url;
            this.presence = user.presence;
            this.currently_active = user.currently_active;
            this.lastActiveAgo = user.lastActiveAgo;
            this.statusMsg = user.statusMsg;
            this.mIsPresenceRefreshed = user.mIsPresenceRefreshed;
            this.mLastPresenceTs = user.mLastPresenceTs;
            this.mEventListeners = new HashMap(user.getEventListeners());
            this.mDataHandler = user.mDataHandler;
            this.mPendingListeners = user.getPendingListeners();
        }
    }

    public User deepCopy() {
        User user = new User();
        user.clone(this);
        return user;
    }

    public boolean isActive() {
        return TextUtils.equals(this.presence, PRESENCE_ONLINE) || (this.currently_active != null && this.currently_active.booleanValue());
    }

    public void setLatestPresenceTs(long j) {
        this.mIsPresenceRefreshed = true;
        this.mLastPresenceTs = j;
    }

    public long getLatestPresenceTs() {
        return this.mLastPresenceTs;
    }

    public long getAbsoluteLastActiveAgo() {
        if (this.lastActiveAgo == null) {
            return 0;
        }
        return System.currentTimeMillis() - (this.mLastPresenceTs - this.lastActiveAgo.longValue());
    }

    public void setDataHandler(MXDataHandler mXDataHandler) {
        this.mDataHandler = mXDataHandler;
        Iterator it = getPendingListeners().iterator();
        while (it.hasNext()) {
            this.mDataHandler.addListener((IMXEventListener) it.next());
        }
    }

    public void addEventListener(final IMXEventListener iMXEventListener) {
        C19531 r0 = new MXEventListener() {
            public void onPresenceUpdate(Event event, User user) {
                if (user.user_id.equals(User.this.user_id)) {
                    iMXEventListener.onPresenceUpdate(event, user);
                }
            }
        };
        getEventListeners().put(iMXEventListener, r0);
        if (this.mDataHandler != null) {
            this.mDataHandler.addListener(r0);
        } else {
            getPendingListeners().add(r0);
        }
    }

    public void removeEventListener(IMXEventListener iMXEventListener) {
        if (this.mDataHandler != null) {
            this.mDataHandler.removeListener((IMXEventListener) getEventListeners().get(iMXEventListener));
        } else {
            getPendingListeners().remove(getEventListeners().get(iMXEventListener));
        }
        getEventListeners().remove(iMXEventListener);
    }
}
