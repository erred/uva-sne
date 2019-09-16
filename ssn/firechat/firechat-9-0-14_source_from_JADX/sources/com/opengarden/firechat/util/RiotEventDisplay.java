package com.opengarden.firechat.util;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.JsonObject;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.interfaces.HtmlToolbox;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.util.EventDisplay;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.widgets.WidgetContent;
import com.opengarden.firechat.widgets.WidgetsManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class RiotEventDisplay extends EventDisplay {
    private static final String LOG_TAG = "RiotEventDisplay";
    private static final Map<String, Event> mClosingWidgetEventByStateKey = new HashMap();

    public RiotEventDisplay(Context context, Event event, RoomState roomState, HtmlToolbox htmlToolbox) {
        super(context, event, roomState, htmlToolbox);
    }

    public RiotEventDisplay(Context context, Event event, RoomState roomState) {
        super(context, event, roomState);
    }

    public CharSequence getTextualDisplay(Integer num) {
        try {
            if (!TextUtils.equals(this.mEvent.getType(), WidgetsManager.WIDGET_EVENT_TYPE)) {
                return super.getTextualDisplay(num);
            }
            JsonObject contentAsJsonObject = this.mEvent.getContentAsJsonObject();
            String senderDisplayNameForEvent = senderDisplayNameForEvent(this.mEvent, JsonUtils.toEventContent(this.mEvent.getContentAsJsonObject()), this.mEvent.getPrevContent(), this.mRoomState);
            if (contentAsJsonObject.entrySet().size() == 0) {
                Event event = (Event) mClosingWidgetEventByStateKey.get(this.mEvent.stateKey);
                if (event == null) {
                    Iterator it = this.mRoomState.getStateEvents(new HashSet(Arrays.asList(new String[]{WidgetsManager.WIDGET_EVENT_TYPE}))).iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        Event event2 = (Event) it.next();
                        if (TextUtils.equals(event2.stateKey, this.mEvent.stateKey) && !event2.getContentAsJsonObject().entrySet().isEmpty()) {
                            event = event2;
                            break;
                        }
                    }
                    if (event != null) {
                        mClosingWidgetEventByStateKey.put(this.mEvent.stateKey, event);
                    }
                }
                return this.mContext.getString(C1299R.string.event_formatter_widget_removed, new Object[]{event != null ? WidgetContent.toWidgetContent(event.getContentAsJsonObject()).getHumanName() : "undefined", senderDisplayNameForEvent});
            }
            return this.mContext.getString(C1299R.string.event_formatter_widget_added, new Object[]{WidgetContent.toWidgetContent(this.mEvent.getContentAsJsonObject()).getHumanName(), senderDisplayNameForEvent});
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("getTextualDisplay() ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return null;
        }
    }
}
