package p010me.leolin.shortcutbadger.impl;

import android.content.Context;
import android.content.Intent;
import java.util.Arrays;
import java.util.List;
import p010me.leolin.shortcutbadger.ShortcutBadgeException;
import p010me.leolin.shortcutbadger.ShortcutBadger;

/* renamed from: me.leolin.shortcutbadger.impl.ApexHomeBadger */
public class ApexHomeBadger extends ShortcutBadger {
    private static final String CLASS = "class";
    private static final String COUNT = "count";
    private static final String INTENT_UPDATE_COUNTER = "com.anddoes.launcher.COUNTER_CHANGED";
    private static final String PACKAGENAME = "package";

    public ApexHomeBadger(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void executeBadge(int i) throws ShortcutBadgeException {
        Intent intent = new Intent(INTENT_UPDATE_COUNTER);
        intent.putExtra(PACKAGENAME, getContextPackageName());
        intent.putExtra("count", i);
        intent.putExtra(CLASS, getEntryActivityName());
        this.mContext.sendBroadcast(intent);
    }

    public List<String> getSupportLaunchers() {
        return Arrays.asList(new String[]{"com.anddoes.launcher"});
    }
}
