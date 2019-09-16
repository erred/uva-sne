package com.opengarden.firechat.matrixsdk.rest.model.bingrules;

import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.util.Log;

public class RoomMemberCountCondition extends Condition {
    private static final String LOG_TAG = "RoomMemberCountCondition";
    private static final String[] PREFIX_ARR = {"==", "<=", ">=", "<", ">", ""};
    private String comparisonPrefix;

    /* renamed from: is */
    public String f131is;
    private int limit;
    private boolean parseError;

    public RoomMemberCountCondition() {
        this.comparisonPrefix = null;
        this.parseError = false;
        this.kind = Condition.KIND_ROOM_MEMBER_COUNT;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RoomMemberCountCondition{is='");
        sb.append(this.f131is);
        sb.append("'}'");
        return sb.toString();
    }

    public boolean isSatisfied(Room room) {
        boolean z = false;
        if (room == null || this.parseError) {
            return false;
        }
        if (this.comparisonPrefix == null) {
            parseIsField();
            if (this.parseError) {
                return false;
            }
        }
        int numberOfMembers = getNumberOfMembers(room);
        if ("==".equals(this.comparisonPrefix) || "".equals(this.comparisonPrefix)) {
            if (numberOfMembers == this.limit) {
                z = true;
            }
            return z;
        } else if ("<".equals(this.comparisonPrefix)) {
            if (numberOfMembers < this.limit) {
                z = true;
            }
            return z;
        } else if (">".equals(this.comparisonPrefix)) {
            if (numberOfMembers > this.limit) {
                z = true;
            }
            return z;
        } else if ("<=".equals(this.comparisonPrefix)) {
            if (numberOfMembers <= this.limit) {
                z = true;
            }
            return z;
        } else if (!">=".equals(this.comparisonPrefix)) {
            return false;
        } else {
            if (numberOfMembers >= this.limit) {
                z = true;
            }
            return z;
        }
    }

    private int getNumberOfMembers(Room room) {
        int i = 0;
        for (RoomMember roomMember : room.getMembers()) {
            if (RoomMember.MEMBERSHIP_JOIN.equals(roomMember.membership)) {
                i++;
            }
        }
        return i;
    }

    /* access modifiers changed from: protected */
    public void parseIsField() {
        String[] strArr = PREFIX_ARR;
        int length = strArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String str = strArr[i];
            if (this.f131is.startsWith(str)) {
                this.comparisonPrefix = str;
                break;
            }
            i++;
        }
        if (this.comparisonPrefix == null) {
            this.parseError = true;
        } else {
            try {
                this.limit = Integer.parseInt(this.f131is.substring(this.comparisonPrefix.length()));
            } catch (NumberFormatException unused) {
                this.parseError = true;
            }
        }
        if (this.parseError) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("parsing error : ");
            sb.append(this.f131is);
            Log.m211e(str2, sb.toString());
        }
    }
}
