package p010me.leolin.shortcutbadger.impl;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import java.util.Arrays;
import java.util.List;
import p010me.leolin.shortcutbadger.ShortcutBadgeException;
import p010me.leolin.shortcutbadger.ShortcutBadger;

/* renamed from: me.leolin.shortcutbadger.impl.NovaHomeBadger */
public class NovaHomeBadger extends ShortcutBadger {
    private static final String CONTENT_URI = "content://com.teslacoilsw.notifier/unread_count";
    private static final String COUNT = "count";
    private static final String TAG = "tag";

    public NovaHomeBadger(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void executeBadge(int i) throws ShortcutBadgeException {
        try {
            ContentValues contentValues = new ContentValues();
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(getContextPackageName());
            sb.append("/");
            sb.append(getEntryActivityName());
            contentValues.put(str, sb.toString());
            contentValues.put("count", Integer.valueOf(i));
            this.mContext.getContentResolver().insert(Uri.parse(CONTENT_URI), contentValues);
        } catch (IllegalArgumentException unused) {
        } catch (Exception e) {
            throw new ShortcutBadgeException(e.getMessage());
        }
    }

    public List<String> getSupportLaunchers() {
        return Arrays.asList(new String[]{"com.teslacoilsw.launcher"});
    }
}
