package p010me.leolin.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import java.util.Arrays;
import java.util.List;
import p010me.leolin.shortcutbadger.ShortcutBadgeException;
import p010me.leolin.shortcutbadger.ShortcutBadger;

/* renamed from: me.leolin.shortcutbadger.impl.NewHtcHomeBadger */
public class NewHtcHomeBadger extends ShortcutBadger {
    public static final String COUNT = "count";
    public static final String EXTRA_COMPONENT = "com.htc.launcher.extra.COMPONENT";
    public static final String EXTRA_COUNT = "com.htc.launcher.extra.COUNT";
    public static final String INTENT_SET_NOTIFICATION = "com.htc.launcher.action.SET_NOTIFICATION";
    public static final String INTENT_UPDATE_SHORTCUT = "com.htc.launcher.action.UPDATE_SHORTCUT";
    public static final String PACKAGENAME = "packagename";

    public NewHtcHomeBadger(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void executeBadge(int i) throws ShortcutBadgeException {
        Intent intent = new Intent(INTENT_SET_NOTIFICATION);
        intent.putExtra(EXTRA_COMPONENT, new ComponentName(getContextPackageName(), getEntryActivityName()).flattenToShortString());
        intent.putExtra(EXTRA_COUNT, i);
        this.mContext.sendBroadcast(intent);
        Intent intent2 = new Intent(INTENT_UPDATE_SHORTCUT);
        intent2.putExtra(PACKAGENAME, getContextPackageName());
        intent2.putExtra(COUNT, i);
        this.mContext.sendBroadcast(intent2);
    }

    public List<String> getSupportLaunchers() {
        return Arrays.asList(new String[]{"com.htc.launcher"});
    }
}
