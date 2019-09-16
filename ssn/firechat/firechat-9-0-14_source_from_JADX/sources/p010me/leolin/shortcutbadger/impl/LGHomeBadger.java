package p010me.leolin.shortcutbadger.impl;

import android.content.Context;
import android.content.Intent;
import java.util.Arrays;
import java.util.List;
import p010me.leolin.shortcutbadger.ShortcutBadger;

/* renamed from: me.leolin.shortcutbadger.impl.LGHomeBadger */
public class LGHomeBadger extends ShortcutBadger {
    private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";
    private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
    private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";

    public LGHomeBadger(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void executeBadge(int i) {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, i);
        intent.putExtra(INTENT_EXTRA_PACKAGENAME, getContextPackageName());
        intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, getEntryActivityName());
        this.mContext.sendBroadcast(intent);
    }

    public List<String> getSupportLaunchers() {
        return Arrays.asList(new String[]{"com.lge.launcher", "com.lge.launcher2"});
    }
}
