package p010me.leolin.shortcutbadger.impl;

import android.content.Context;
import android.content.Intent;
import java.util.Arrays;
import java.util.List;
import p010me.leolin.shortcutbadger.ShortcutBadgeException;
import p010me.leolin.shortcutbadger.ShortcutBadger;

/* renamed from: me.leolin.shortcutbadger.impl.SolidHomeBadger */
public class SolidHomeBadger extends ShortcutBadger {
    private static final String CLASS = "com.majeur.launcher.intent.extra.BADGE_CLASS";
    private static final String COUNT = "com.majeur.launcher.intent.extra.BADGE_COUNT";
    private static final String INTENT_UPDATE_COUNTER = "com.majeur.launcher.intent.action.UPDATE_BADGE";
    private static final String PACKAGENAME = "com.majeur.launcher.intent.extra.BADGE_PACKAGE";

    public SolidHomeBadger(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void executeBadge(int i) throws ShortcutBadgeException {
        Intent intent = new Intent(INTENT_UPDATE_COUNTER);
        intent.putExtra(PACKAGENAME, getContextPackageName());
        intent.putExtra(COUNT, i);
        intent.putExtra(CLASS, getEntryActivityName());
        this.mContext.sendBroadcast(intent);
    }

    public List<String> getSupportLaunchers() {
        return Arrays.asList(new String[]{"com.majeur.launcher"});
    }
}
