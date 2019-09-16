package p010me.leolin.shortcutbadger.impl;

import android.content.Context;
import android.content.Intent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import p010me.leolin.shortcutbadger.ShortcutBadgeException;
import p010me.leolin.shortcutbadger.ShortcutBadger;

/* renamed from: me.leolin.shortcutbadger.impl.XiaomiHomeBadger */
public class XiaomiHomeBadger extends ShortcutBadger {
    public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
    public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";
    public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";

    public XiaomiHomeBadger(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void executeBadge(int i) throws ShortcutBadgeException {
        Object obj;
        Object obj2;
        try {
            Object newInstance = Class.forName("android.app.MiuiNotification").newInstance();
            Field declaredField = newInstance.getClass().getDeclaredField("messageCount");
            declaredField.setAccessible(true);
            if (i == 0) {
                obj2 = "";
            } else {
                obj2 = Integer.valueOf(i);
            }
            declaredField.set(newInstance, String.valueOf(obj2));
        } catch (Exception unused) {
            Intent intent = new Intent(INTENT_ACTION);
            String str = EXTRA_UPDATE_APP_COMPONENT_NAME;
            StringBuilder sb = new StringBuilder();
            sb.append(getContextPackageName());
            sb.append("/");
            sb.append(getEntryActivityName());
            intent.putExtra(str, sb.toString());
            String str2 = EXTRA_UPDATE_APP_MSG_TEXT;
            if (i == 0) {
                obj = "";
            } else {
                obj = Integer.valueOf(i);
            }
            intent.putExtra(str2, String.valueOf(obj));
            this.mContext.sendBroadcast(intent);
        }
    }

    public List<String> getSupportLaunchers() {
        return Arrays.asList(new String[]{"com.miui.miuilite", "com.miui.home", "com.miui.miuihome", "com.miui.miuihome2", "com.miui.mihome", "com.miui.mihome2"});
    }
}
