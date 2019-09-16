package org.jitsi.meet.sdk;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.google.android.gms.dynamite.ProviderConstants;
import java.util.HashMap;
import java.util.Map;

class AppInfoModule extends ReactContextBaseJavaModule {
    public String getName() {
        return "AppInfo";
    }

    public AppInfoModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
    }

    public Map<String, Object> getConstants() {
        PackageInfo packageInfo;
        Object obj;
        String str;
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        PackageManager packageManager = reactApplicationContext.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            String packageName = reactApplicationContext.getPackageName();
            ApplicationInfo applicationInfo2 = packageManager.getApplicationInfo(packageName, 0);
            packageInfo = packageManager.getPackageInfo(packageName, 0);
            applicationInfo = applicationInfo2;
        } catch (NameNotFoundException unused) {
            packageInfo = null;
        }
        HashMap hashMap = new HashMap();
        String str2 = "name";
        if (applicationInfo == null) {
            obj = "";
        } else {
            obj = packageManager.getApplicationLabel(applicationInfo);
        }
        hashMap.put(str2, obj);
        String str3 = ProviderConstants.API_COLNAME_FEATURE_VERSION;
        if (packageInfo == null) {
            str = "";
        } else {
            str = packageInfo.versionName;
        }
        hashMap.put(str3, str);
        return hashMap;
    }
}
