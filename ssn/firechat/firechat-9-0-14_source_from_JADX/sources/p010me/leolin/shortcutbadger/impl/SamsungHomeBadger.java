package p010me.leolin.shortcutbadger.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.util.Arrays;
import java.util.List;
import p010me.leolin.shortcutbadger.ShortcutBadgeException;
import p010me.leolin.shortcutbadger.ShortcutBadger;
import p010me.leolin.shortcutbadger.util.CloseHelper;

/* renamed from: me.leolin.shortcutbadger.impl.SamsungHomeBadger */
public class SamsungHomeBadger extends ShortcutBadger {
    private static final String[] CONTENT_PROJECTION = {"_id", "class"};
    private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";

    public SamsungHomeBadger(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void executeBadge(int i) throws ShortcutBadgeException {
        Cursor cursor;
        Uri parse = Uri.parse(CONTENT_URI);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        try {
            ContentResolver contentResolver2 = contentResolver;
            Uri uri = parse;
            cursor = contentResolver2.query(uri, CONTENT_PROJECTION, "package=?", new String[]{getContextPackageName()}, null);
            if (cursor != null) {
                try {
                    String entryActivityName = getEntryActivityName();
                    boolean z = false;
                    while (cursor.moveToNext()) {
                        contentResolver.update(parse, getContentValues(i, false), "_id=?", new String[]{String.valueOf(cursor.getInt(0))});
                        if (entryActivityName.equals(cursor.getString(cursor.getColumnIndex("class")))) {
                            z = true;
                        }
                    }
                    if (!z) {
                        contentResolver.insert(parse, getContentValues(i, true));
                    }
                } catch (Throwable th) {
                    th = th;
                    CloseHelper.close(cursor);
                    throw th;
                }
            }
            CloseHelper.close(cursor);
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
            CloseHelper.close(cursor);
            throw th;
        }
    }

    private ContentValues getContentValues(int i, boolean z) {
        ContentValues contentValues = new ContentValues();
        if (z) {
            contentValues.put("package", getContextPackageName());
            contentValues.put("class", getEntryActivityName());
        }
        contentValues.put("badgecount", Integer.valueOf(i));
        return contentValues;
    }

    public List<String> getSupportLaunchers() {
        return Arrays.asList(new String[]{"com.sec.android.app.launcher", "com.sec.android.app.twlauncher"});
    }
}
