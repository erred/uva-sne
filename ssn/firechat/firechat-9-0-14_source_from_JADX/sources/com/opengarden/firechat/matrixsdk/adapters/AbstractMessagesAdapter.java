package com.opengarden.firechat.matrixsdk.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.opengarden.firechat.matrixsdk.rest.model.Event;

public abstract class AbstractMessagesAdapter extends ArrayAdapter<MessageRow> {
    public abstract void add(MessageRow messageRow, boolean z);

    public abstract void addToFront(MessageRow messageRow);

    public abstract MessageRow getClosestRow(Event event);

    public abstract MessageRow getClosestRowBeforeTs(String str, long j);

    public abstract MessageRow getClosestRowFromTs(String str, long j);

    public abstract int getMaxThumbnailHeight();

    public abstract int getMaxThumbnailWidth();

    public abstract MessageRow getMessageRow(String str);

    public abstract boolean isUnreadViewMode();

    public abstract void onBingRulesUpdate();

    public abstract void removeEventById(String str);

    public abstract void resetReadMarker();

    public abstract void setIsPreviewMode(boolean z);

    public abstract void setIsUnreadViewMode(boolean z);

    public abstract void setSearchPattern(String str);

    public abstract void updateEventById(Event event, String str);

    public abstract void updateReadMarker(String str, String str2);

    public AbstractMessagesAdapter(Context context, int i) {
        super(context, i);
    }
}
