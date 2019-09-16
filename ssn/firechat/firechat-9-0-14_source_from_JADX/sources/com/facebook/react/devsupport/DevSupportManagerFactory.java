package com.facebook.react.devsupport;

import android.content.Context;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import javax.annotation.Nullable;

public class DevSupportManagerFactory {
    private static final String DEVSUPPORT_IMPL_CLASS = "DevSupportManagerImpl";
    private static final String DEVSUPPORT_IMPL_PACKAGE = "com.facebook.react.devsupport";

    public static DevSupportManager create(Context context, ReactInstanceDevCommandsHandler reactInstanceDevCommandsHandler, @Nullable String str, boolean z, int i) {
        return create(context, reactInstanceDevCommandsHandler, str, z, null, null, i);
    }

    public static DevSupportManager create(Context context, ReactInstanceDevCommandsHandler reactInstanceDevCommandsHandler, @Nullable String str, boolean z, @Nullable RedBoxHandler redBoxHandler, @Nullable DevBundleDownloadListener devBundleDownloadListener, int i) {
        if (!z) {
            return new DisabledDevSupportManager();
        }
        try {
            StringBuilder sb = new StringBuilder(DEVSUPPORT_IMPL_PACKAGE);
            sb.append(".");
            sb.append(DEVSUPPORT_IMPL_CLASS);
            return (DevSupportManager) Class.forName(sb.toString()).getConstructor(new Class[]{Context.class, ReactInstanceDevCommandsHandler.class, String.class, Boolean.TYPE, RedBoxHandler.class, DevBundleDownloadListener.class, Integer.TYPE}).newInstance(new Object[]{context, reactInstanceDevCommandsHandler, str, Boolean.valueOf(true), redBoxHandler, devBundleDownloadListener, Integer.valueOf(i)});
        } catch (Exception e) {
            throw new RuntimeException("Requested enabled DevSupportManager, but DevSupportManagerImpl class was not found or could not be created", e);
        }
    }
}
