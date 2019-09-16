package com.opengarden.firechat;

public class ViewedRoomTracker {
    private static ViewedRoomTracker instance;
    private String mMatrixId = null;
    private String mViewedRoomId = null;

    private ViewedRoomTracker() {
    }

    public static synchronized ViewedRoomTracker getInstance() {
        ViewedRoomTracker viewedRoomTracker;
        synchronized (ViewedRoomTracker.class) {
            if (instance == null) {
                instance = new ViewedRoomTracker();
            }
            viewedRoomTracker = instance;
        }
        return viewedRoomTracker;
    }

    public String getViewedRoomId() {
        return this.mViewedRoomId;
    }

    public void setViewedRoomId(String str) {
        this.mViewedRoomId = str;
    }

    public void setMatrixId(String str) {
        this.mMatrixId = str;
    }
}
