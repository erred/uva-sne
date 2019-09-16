package com.opengarden.firechat.matrixsdk.util;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import java.util.regex.Pattern;

public class EventUtils {
    private static final String LOG_TAG = "EventUtils";

    public static boolean shouldHighlight(MXSession mXSession, Event event) {
        boolean z = false;
        if (mXSession == null || event == null) {
            return false;
        }
        BingRule fulfillRule = mXSession.fulfillRule(event);
        if (fulfillRule != null) {
            z = fulfillRule.shouldHighlight();
            if (z) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## shouldHighlight() : the event ");
                sb.append(event.roomId);
                sb.append("/");
                sb.append(event.eventId);
                sb.append(" is higlighted by ");
                sb.append(fulfillRule);
                Log.m209d(str, sb.toString());
            }
        }
        return z;
    }

    public static boolean shouldNotify(MXSession mXSession, Event event, String str) {
        boolean z = false;
        if (event == null || mXSession == null) {
            Log.m211e(LOG_TAG, "shouldNotify invalid params");
            return false;
        } else if (event.roomId == null) {
            Log.m211e(LOG_TAG, "shouldNotify null room ID");
            return false;
        } else if (event.getSender() == null) {
            Log.m211e(LOG_TAG, "shouldNotify null room ID");
            return false;
        } else if (TextUtils.equals(event.roomId, str)) {
            return false;
        } else {
            if (shouldHighlight(mXSession, event)) {
                return true;
            }
            if (RoomState.DIRECTORY_VISIBILITY_PRIVATE.equals(mXSession.getDataHandler().getRoom(event.roomId).getVisibility()) && !TextUtils.equals(event.getSender(), mXSession.getCredentials().userId)) {
                z = true;
            }
            return z;
        }
    }

    public static boolean caseInsensitiveFind(String str, String str2) {
        boolean z;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("(\\W|^)");
            sb.append(Pattern.quote(str));
            sb.append("(\\W|$)");
            z = Pattern.compile(sb.toString(), 2).matcher(str2).find();
        } catch (Exception unused) {
            Log.m211e(LOG_TAG, "## caseInsensitiveFind() : failed");
            z = false;
        }
        return z;
    }
}
