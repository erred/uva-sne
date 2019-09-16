package p010me.leolin.shortcutbadger;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import p010me.leolin.shortcutbadger.impl.AdwHomeBadger;
import p010me.leolin.shortcutbadger.impl.ApexHomeBadger;
import p010me.leolin.shortcutbadger.impl.AsusHomeLauncher;
import p010me.leolin.shortcutbadger.impl.DefaultBadger;
import p010me.leolin.shortcutbadger.impl.LGHomeBadger;
import p010me.leolin.shortcutbadger.impl.NewHtcHomeBadger;
import p010me.leolin.shortcutbadger.impl.NovaHomeBadger;
import p010me.leolin.shortcutbadger.impl.SamsungHomeBadger;
import p010me.leolin.shortcutbadger.impl.SolidHomeBadger;
import p010me.leolin.shortcutbadger.impl.SonyHomeBadger;
import p010me.leolin.shortcutbadger.impl.XiaomiHomeBadger;

/* renamed from: me.leolin.shortcutbadger.ShortcutBadger */
public abstract class ShortcutBadger {
    private static final List<Class<? extends ShortcutBadger>> BADGERS = new LinkedList();
    private static final String LOG_TAG = "ShortcutBadger";
    private static ShortcutBadger mShortcutBadger;
    protected Context mContext;

    /* access modifiers changed from: protected */
    public abstract void executeBadge(int i) throws ShortcutBadgeException;

    /* access modifiers changed from: protected */
    public abstract List<String> getSupportLaunchers();

    static {
        BADGERS.add(AdwHomeBadger.class);
        BADGERS.add(ApexHomeBadger.class);
        BADGERS.add(LGHomeBadger.class);
        BADGERS.add(NewHtcHomeBadger.class);
        BADGERS.add(NovaHomeBadger.class);
        BADGERS.add(SamsungHomeBadger.class);
        BADGERS.add(SolidHomeBadger.class);
        BADGERS.add(SonyHomeBadger.class);
        BADGERS.add(XiaomiHomeBadger.class);
        BADGERS.add(AsusHomeLauncher.class);
    }

    public static ShortcutBadger with(Context context) {
        return getShortcutBadger(context);
    }

    public static void setBadge(Context context, int i) throws ShortcutBadgeException {
        try {
            getShortcutBadger(context).executeBadge(i);
        } catch (Throwable th) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to execute badge:");
            sb.append(th.getMessage());
            throw new ShortcutBadgeException(sb.toString());
        }
    }

    private static ShortcutBadger getShortcutBadger(Context context) {
        if (mShortcutBadger != null) {
            return mShortcutBadger;
        }
        Log.d(LOG_TAG, "Finding badger");
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            String str = context.getPackageManager().resolveActivity(intent, 65536).activityInfo.packageName;
            if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                mShortcutBadger = new XiaomiHomeBadger(context);
                return mShortcutBadger;
            }
            Iterator it = BADGERS.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                ShortcutBadger shortcutBadger = (ShortcutBadger) ((Class) it.next()).getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
                if (shortcutBadger.getSupportLaunchers().contains(str)) {
                    mShortcutBadger = shortcutBadger;
                    break;
                }
            }
            if (mShortcutBadger == null) {
                mShortcutBadger = new DefaultBadger(context);
            }
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Returning badger:");
            sb.append(mShortcutBadger.getClass().getCanonicalName());
            Log.d(str2, sb.toString());
            return mShortcutBadger;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private ShortcutBadger() {
    }

    protected ShortcutBadger(Context context) {
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public String getEntryActivityName() {
        return this.mContext.getPackageManager().getLaunchIntentForPackage(this.mContext.getPackageName()).getComponent().getClassName();
    }

    /* access modifiers changed from: protected */
    public String getContextPackageName() {
        return this.mContext.getPackageName();
    }

    public void count(int i) {
        try {
            executeBadge(i);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    public void remove() {
        count(0);
    }
}
