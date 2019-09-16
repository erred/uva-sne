package com.opengarden.firechat.util;

import android.content.Context;
import android.text.TextUtils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.AdapterUtils;
import com.opengarden.firechat.matrixsdk.adapters.MessageRow;
import com.opengarden.firechat.matrixsdk.call.MXCallsManager;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.cli.HelpFormatter;

public class EventGroup extends Event {
    private static final String LOG_TAG = "EventGroup";
    private final Set<String> mHiddenEventIds;
    private boolean mIsExpanded;
    private final List<MessageRow> mRows = new ArrayList();
    private final Map<String, MessageRow> mRowsMap = new HashMap();

    public EventGroup(Set<String> set) {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append('@');
        sb.append(Integer.toHexString(hashCode()));
        sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
        sb.append(System.currentTimeMillis());
        this.eventId = sb.toString();
        this.mHiddenEventIds = set;
    }

    public static boolean isSupported(MessageRow messageRow) {
        return messageRow != null && messageRow.getEvent() != null && TextUtils.equals(messageRow.getEvent().getType(), Event.EVENT_TYPE_STATE_ROOM_MEMBER) && !TextUtils.equals(messageRow.getEvent().stateKey, MXCallsManager.getConferenceUserId(messageRow.getEvent().roomId));
    }

    public boolean contains(String str) {
        return str != null && this.mRowsMap.containsKey(str);
    }

    private boolean contains(MessageRow messageRow) {
        return (messageRow == null || messageRow.getEvent() == null || !this.mRowsMap.containsKey(messageRow.getEvent().eventId)) ? false : true;
    }

    private void refreshOriginServerTs() {
        if (this.mRows.size() > 0) {
            this.originServerTs = ((MessageRow) this.mRows.get(0)).getEvent().originServerTs;
        }
    }

    private void onRowAdded(MessageRow messageRow) {
        String str = messageRow.getEvent().eventId;
        this.mRowsMap.put(str, messageRow);
        if (this.mRowsMap.size() <= 1) {
            this.mHiddenEventIds.removeAll(this.mRowsMap.keySet());
            this.mHiddenEventIds.add(this.eventId);
        } else if (this.mHiddenEventIds.contains(this.eventId)) {
            this.mHiddenEventIds.remove(this.eventId);
            if (this.mIsExpanded) {
                this.mHiddenEventIds.removeAll(this.mRowsMap.keySet());
            } else {
                this.mHiddenEventIds.addAll(this.mRowsMap.keySet());
            }
        } else if (this.mIsExpanded) {
            this.mHiddenEventIds.remove(str);
        } else {
            this.mHiddenEventIds.add(str);
        }
        refreshOriginServerTs();
    }

    public void add(MessageRow messageRow) {
        if (!contains(messageRow)) {
            this.mRows.add(messageRow);
            onRowAdded(messageRow);
        }
    }

    public void addToFront(MessageRow messageRow) {
        if (!contains(messageRow)) {
            if (this.mRows.size() > 0) {
                this.mRows.add(0, messageRow);
            } else {
                this.mRows.add(messageRow);
            }
            onRowAdded(messageRow);
        }
    }

    public boolean isEmpty() {
        return this.mRows.isEmpty();
    }

    public boolean isExpanded() {
        return this.mIsExpanded;
    }

    public void setIsExpanded(boolean z) {
        if (this.mRows.size() < 2) {
            Log.m211e(LOG_TAG, "## setIsExpanded() : cannot collapse a group when there is only one item");
            this.mIsExpanded = true;
            this.mHiddenEventIds.add(this.eventId);
        } else {
            this.mIsExpanded = z;
        }
        if (this.mIsExpanded) {
            this.mHiddenEventIds.removeAll(this.mRowsMap.keySet());
        } else {
            this.mHiddenEventIds.addAll(this.mRowsMap.keySet());
        }
    }

    public boolean canAddRow(MessageRow messageRow) {
        return isEmpty() || AdapterUtils.zeroTimeDate(new Date(messageRow.getEvent().getOriginServerTs())).getTime() == AdapterUtils.zeroTimeDate(new Date(getOriginServerTs())).getTime();
    }

    public void removeByEventId(String str) {
        if (str != null) {
            MessageRow messageRow = (MessageRow) this.mRowsMap.get(str);
            if (messageRow != null) {
                this.mRowsMap.remove(str);
                this.mRows.remove(messageRow);
                this.mHiddenEventIds.remove(str);
            }
            if (this.mRowsMap.size() == 1) {
                this.mHiddenEventIds.removeAll(this.mRowsMap.keySet());
                this.mHiddenEventIds.add(this.eventId);
            }
            refreshOriginServerTs();
        }
    }

    public List<MessageRow> getRows() {
        return new ArrayList(this.mRows);
    }

    public List<MessageRow> getAvatarRows(int i) {
        HashSet hashSet = new HashSet();
        ArrayList arrayList = new ArrayList();
        for (MessageRow messageRow : this.mRows) {
            String str = messageRow.getEvent().sender;
            if (str != null && !hashSet.contains(str)) {
                arrayList.add(messageRow);
                hashSet.add(str);
                if (hashSet.size() == i) {
                    break;
                }
            }
        }
        return arrayList;
    }

    public String toString(Context context) {
        return context.getResources().getQuantityString(C1299R.plurals.membership_changes, this.mRowsMap.size(), new Object[]{Integer.valueOf(this.mRowsMap.size())});
    }
}
