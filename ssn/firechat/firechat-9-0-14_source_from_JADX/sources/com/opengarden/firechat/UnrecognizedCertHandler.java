package com.opengarden.firechat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.support.p003v7.app.AlertDialog;
import android.support.p003v7.app.AlertDialog.Builder;
import android.view.View;
import android.widget.TextView;
import com.opengarden.firechat.EventEmitter.Listener;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.ssl.CertUtil;
import com.opengarden.firechat.matrixsdk.ssl.Fingerprint;
import com.opengarden.firechat.matrixsdk.ssl.UnrecognizedCertificateException;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.HashMap;
import java.util.HashSet;

public class UnrecognizedCertHandler {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "UnrecognizedCertHandler";
    /* access modifiers changed from: private */
    public static final HashMap<String, HashSet<Fingerprint>> ignoredFingerprints = new HashMap<>();
    /* access modifiers changed from: private */
    public static final HashSet<String> openDialogIds = new HashSet<>();

    public interface Callback {
        void onAccept();

        void onIgnore();

        void onReject();
    }

    public static boolean handle(HomeServerConnectionConfig homeServerConnectionConfig, Exception exc, Callback callback) {
        UnrecognizedCertificateException certificateException = CertUtil.getCertificateException(exc);
        if (certificateException == null) {
            return false;
        }
        show(homeServerConnectionConfig, certificateException.getFingerprint(), false, callback);
        return true;
    }

    public static void show(final HomeServerConnectionConfig homeServerConnectionConfig, final Fingerprint fingerprint, boolean z, final Callback callback) {
        final String str;
        final Activity currentActivity = VectorApp.getCurrentActivity();
        if (currentActivity != null) {
            if (homeServerConnectionConfig.getCredentials() != null) {
                str = homeServerConnectionConfig.getCredentials().userId;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(homeServerConnectionConfig.getHomeserverUri().toString());
                sb.append(fingerprint.getBytesAsHexString());
                str = sb.toString();
            }
            if (openDialogIds.contains(str)) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Not opening dialog ");
                sb2.append(str);
                sb2.append(" as one is already open.");
                Log.m213i(str2, sb2.toString());
                return;
            }
            if (homeServerConnectionConfig.getCredentials() != null) {
                HashSet hashSet = (HashSet) ignoredFingerprints.get(homeServerConnectionConfig.getCredentials().userId);
                if (hashSet != null && hashSet.contains(fingerprint)) {
                    callback.onIgnore();
                    return;
                }
            }
            Builder builder = new Builder(currentActivity);
            View inflate = currentActivity.getLayoutInflater().inflate(C1299R.layout.ssl_fingerprint_prompt, null);
            ((TextView) inflate.findViewById(C1299R.C1301id.ssl_fingerprint_title)).setText(String.format(VectorApp.getApplicationLocale(), currentActivity.getString(C1299R.string.ssl_fingerprint_hash), new Object[]{fingerprint.getType().toString()}));
            ((TextView) inflate.findViewById(C1299R.C1301id.ssl_fingerprint)).setText(fingerprint.getBytesAsHexString());
            TextView textView = (TextView) inflate.findViewById(C1299R.C1301id.ssl_user_id);
            if (homeServerConnectionConfig.getCredentials() != null) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(currentActivity.getString(C1299R.string.username));
                sb3.append(":  ");
                sb3.append(homeServerConnectionConfig.getCredentials().userId);
                textView.setText(sb3.toString());
            } else {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(currentActivity.getString(C1299R.string.hs_url));
                sb4.append(":  ");
                sb4.append(homeServerConnectionConfig.getHomeserverUri().toString());
                textView.setText(sb4.toString());
            }
            TextView textView2 = (TextView) inflate.findViewById(C1299R.C1301id.ssl_explanation);
            if (!z) {
                textView2.setText(currentActivity.getString(C1299R.string.ssl_cert_new_account_expl));
            } else if (homeServerConnectionConfig.getAllowedFingerprints().size() > 0) {
                textView2.setText(currentActivity.getString(C1299R.string.ssl_expected_existing_expl));
            } else {
                textView2.setText(currentActivity.getString(C1299R.string.ssl_unexpected_existing_expl));
            }
            builder.setView(inflate);
            builder.setTitle((int) C1299R.string.ssl_could_not_verify);
            builder.setPositiveButton((int) C1299R.string.ssl_trust, (OnClickListener) new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    homeServerConnectionConfig.getAllowedFingerprints().add(fingerprint);
                    callback.onAccept();
                }
            });
            if (z) {
                builder.setNegativeButton((int) C1299R.string.ssl_remain_offline, (OnClickListener) new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (homeServerConnectionConfig.getCredentials() != null) {
                            HashSet hashSet = (HashSet) UnrecognizedCertHandler.ignoredFingerprints.get(homeServerConnectionConfig.getCredentials().userId);
                            if (hashSet == null) {
                                hashSet = new HashSet();
                                UnrecognizedCertHandler.ignoredFingerprints.put(homeServerConnectionConfig.getCredentials().userId, hashSet);
                            }
                            hashSet.add(fingerprint);
                        }
                        callback.onIgnore();
                    }
                });
                builder.setNeutralButton((int) C1299R.string.ssl_logout_account, (OnClickListener) new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onReject();
                    }
                });
            } else {
                builder.setNegativeButton((int) C1299R.string.cancel, (OnClickListener) new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onReject();
                    }
                });
            }
            final AlertDialog create = builder.create();
            final C13175 r11 = new Listener<Activity>() {
                public void onEventFired(EventEmitter<Activity> eventEmitter, Activity activity) {
                    if (currentActivity == activity) {
                        Log.m211e(UnrecognizedCertHandler.LOG_TAG, "Dismissed!");
                        UnrecognizedCertHandler.openDialogIds.remove(str);
                        create.dismiss();
                        eventEmitter.unregister(this);
                    }
                }
            };
            final EventEmitter onActivityDestroyedListener = VectorApp.getInstance().getOnActivityDestroyedListener();
            onActivityDestroyedListener.register(r11);
            create.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    Log.m211e(UnrecognizedCertHandler.LOG_TAG, "Dismissed!");
                    UnrecognizedCertHandler.openDialogIds.remove(str);
                    onActivityDestroyedListener.unregister(r11);
                }
            });
            create.show();
            openDialogIds.add(str);
        }
    }
}
