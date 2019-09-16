package com.opengarden.firechat.matrixsdk.util;

import android.content.Context;
import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang3.StringUtils;
import retrofit2.C3224Response;

public class UnsentEventsManager {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "UnsentEventsManager";
    private static final int MAX_MESSAGE_LIFETIME_MS = 180000;
    private static final int MAX_RETRIES = 4;
    private static final int RETRY_JITTER_MS = 3000;
    /* access modifiers changed from: private */
    public final MXDataHandler mDataHandler;
    private final NetworkConnectivityReceiver mNetworkConnectivityReceiver;
    /* access modifiers changed from: private */
    public final List<UnsentEventSnapshot> mUnsentEvents = new ArrayList();
    /* access modifiers changed from: private */
    public final HashMap<Object, UnsentEventSnapshot> mUnsentEventsMap = new HashMap<>();
    /* access modifiers changed from: private */
    public boolean mbIsConnected = false;

    private class UnsentEventSnapshot {
        /* access modifiers changed from: private */
        public long mAge;
        private Timer mAutoResendTimer;
        public String mEventDescription;
        public boolean mIsResending;
        public Timer mLifeTimeTimer;
        /* access modifiers changed from: private */
        public RequestRetryCallBack mRequestRetryCallBack;
        /* access modifiers changed from: private */
        public int mRetryCount;

        private UnsentEventSnapshot() {
            this.mAutoResendTimer = null;
            this.mLifeTimeTimer = null;
            this.mIsResending = false;
            this.mEventDescription = null;
        }

        public boolean waitToBeResent() {
            return this.mAutoResendTimer != null;
        }

        public boolean resendEventAfter(int i) {
            stopTimer();
            try {
                if (this.mEventDescription != null) {
                    String access$000 = UnsentEventsManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Resend after ");
                    sb.append(i);
                    sb.append(" [");
                    sb.append(this.mEventDescription);
                    sb.append("]");
                    Log.m209d(access$000, sb.toString());
                }
                this.mAutoResendTimer = new Timer();
                this.mAutoResendTimer.schedule(new TimerTask() {
                    public void run() {
                        try {
                            UnsentEventSnapshot.this.mIsResending = true;
                            if (UnsentEventSnapshot.this.mEventDescription != null) {
                                String access$000 = UnsentEventsManager.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("Resend [");
                                sb.append(UnsentEventSnapshot.this.mEventDescription);
                                sb.append("]");
                                Log.m209d(access$000, sb.toString());
                            }
                            UnsentEventSnapshot.this.mRequestRetryCallBack.onRetry();
                        } catch (Throwable th) {
                            UnsentEventSnapshot.this.mIsResending = false;
                            String access$0002 = UnsentEventsManager.LOG_TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("## resendEventAfter() : ");
                            sb2.append(UnsentEventSnapshot.this.mEventDescription);
                            sb2.append(" + onRetry failed ");
                            sb2.append(th.getMessage());
                            Log.m211e(access$0002, sb2.toString());
                        }
                    }
                }, (long) i);
                return true;
            } catch (Throwable th) {
                String access$0002 = UnsentEventsManager.LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## resendEventAfter failed ");
                sb2.append(th.getMessage());
                Log.m211e(access$0002, sb2.toString());
                return false;
            }
        }

        public void stopTimer() {
            if (this.mAutoResendTimer != null) {
                this.mAutoResendTimer.cancel();
                this.mAutoResendTimer = null;
            }
        }

        public void stopTimers() {
            if (this.mAutoResendTimer != null) {
                this.mAutoResendTimer.cancel();
                this.mAutoResendTimer = null;
            }
            if (this.mLifeTimeTimer != null) {
                this.mLifeTimeTimer.cancel();
                this.mLifeTimeTimer = null;
            }
        }
    }

    public UnsentEventsManager(NetworkConnectivityReceiver networkConnectivityReceiver, MXDataHandler mXDataHandler) {
        this.mNetworkConnectivityReceiver = networkConnectivityReceiver;
        this.mNetworkConnectivityReceiver.addEventListener(new IMXNetworkEventListener() {
            public void onNetworkConnectionUpdate(boolean z) {
                UnsentEventsManager.this.mbIsConnected = z;
                if (z) {
                    UnsentEventsManager.this.resentUnsents();
                }
            }
        });
        this.mbIsConnected = this.mNetworkConnectivityReceiver.isConnected();
        this.mDataHandler = mXDataHandler;
    }

    public void onEventSent(ApiCallback apiCallback) {
        if (apiCallback != null) {
            UnsentEventSnapshot unsentEventSnapshot = null;
            synchronized (this.mUnsentEventsMap) {
                if (this.mUnsentEventsMap.containsKey(apiCallback)) {
                    unsentEventSnapshot = (UnsentEventSnapshot) this.mUnsentEventsMap.get(apiCallback);
                }
            }
            if (unsentEventSnapshot != null) {
                if (unsentEventSnapshot.mEventDescription != null) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Resend Succeeded [");
                    sb.append(unsentEventSnapshot.mEventDescription);
                    sb.append("]");
                    Log.m209d(str, sb.toString());
                }
                unsentEventSnapshot.stopTimers();
                synchronized (this.mUnsentEventsMap) {
                    this.mUnsentEventsMap.remove(apiCallback);
                    this.mUnsentEvents.remove(unsentEventSnapshot);
                }
                resentUnsents();
            }
        }
    }

    public void clear() {
        synchronized (this.mUnsentEventsMap) {
            for (UnsentEventSnapshot stopTimers : this.mUnsentEvents) {
                stopTimers.stopTimers();
            }
            this.mUnsentEvents.clear();
            this.mUnsentEventsMap.clear();
        }
    }

    public NetworkConnectivityReceiver getNetworkConnectivityReceiver() {
        return this.mNetworkConnectivityReceiver;
    }

    public Context getContext() {
        return this.mDataHandler.getStore().getContext();
    }

    /* access modifiers changed from: private */
    public static void triggerErrorCallback(MXDataHandler mXDataHandler, String str, C3224Response response, Exception exc, ApiCallback apiCallback) {
        MatrixError matrixError;
        if (exc != null && !TextUtils.isEmpty(exc.getMessage())) {
            Log.m211e(LOG_TAG, exc.getLocalizedMessage());
        }
        if (exc == null) {
            if (str != null) {
                try {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unexpected Error ");
                    sb.append(str);
                    Log.m211e(str2, sb.toString());
                } catch (Exception e) {
                    String str3 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Exception UnexpectedError ");
                    sb2.append(e.getMessage());
                    Log.m211e(str3, sb2.toString());
                    return;
                }
            }
            if (apiCallback != null) {
                apiCallback.onUnexpectedError(null);
            }
        } else if (exc instanceof IOException) {
            if (str != null) {
                try {
                    String str4 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Network Error ");
                    sb3.append(str);
                    Log.m211e(str4, sb3.toString());
                } catch (Exception e2) {
                    String str5 = LOG_TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Exception NetworkError ");
                    sb4.append(e2.getMessage());
                    Log.m211e(str5, sb4.toString());
                    return;
                }
            }
            if (apiCallback != null) {
                apiCallback.onNetworkError(exc);
            }
        } else {
            try {
                matrixError = (MatrixError) JsonUtils.getGson(false).fromJson(response.errorBody().string(), MatrixError.class);
            } catch (Exception unused) {
                matrixError = null;
            }
            if (matrixError != null) {
                if (str != null) {
                    try {
                        String str6 = LOG_TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("Matrix Error ");
                        sb5.append(matrixError);
                        sb5.append(StringUtils.SPACE);
                        sb5.append(str);
                        Log.m211e(str6, sb5.toString());
                    } catch (Exception e3) {
                        String str7 = LOG_TAG;
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append("Exception MatrixError ");
                        sb6.append(e3.getLocalizedMessage());
                        Log.m211e(str7, sb6.toString());
                        return;
                    }
                }
                if (MatrixError.isConfigurationErrorCode(matrixError.errcode)) {
                    mXDataHandler.onConfigurationError(matrixError.errcode);
                } else if (apiCallback != null) {
                    apiCallback.onMatrixError(matrixError);
                }
            } else {
                if (str != null) {
                    try {
                        String str8 = LOG_TAG;
                        StringBuilder sb7 = new StringBuilder();
                        sb7.append("Unexpected Error ");
                        sb7.append(str);
                        Log.m211e(str8, sb7.toString());
                    } catch (Exception e4) {
                        String str9 = LOG_TAG;
                        StringBuilder sb8 = new StringBuilder();
                        sb8.append("Exception UnexpectedError ");
                        sb8.append(e4.getLocalizedMessage());
                        Log.m211e(str9, sb8.toString());
                        return;
                    }
                }
                if (apiCallback != null) {
                    apiCallback.onUnexpectedError(exc);
                }
            }
        }
    }

    public void onConfigurationErrorCode(String str, String str2) {
        String str3 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append(" failed because of an unknown matrix token");
        Log.m211e(str3, sb.toString());
        this.mDataHandler.onConfigurationError(str);
    }

    /* JADX WARNING: Removed duplicated region for block: B:107:0x0244 A[Catch:{ all -> 0x0276, all -> 0x027c }] */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x008d A[Catch:{ all -> 0x0049 }] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00c0 A[Catch:{ all -> 0x0049 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00fe A[SYNTHETIC, Splitter:B:48:0x00fe] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0166 A[SYNTHETIC, Splitter:B:64:0x0166] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0208 A[Catch:{ all -> 0x0276, all -> 0x027c }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onEventSendingFailed(java.lang.String r23, boolean r24, retrofit2.C3224Response r25, java.lang.Exception r26, com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback r27, com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack r28) {
        /*
            r22 = this;
            r8 = r22
            r9 = r23
            r10 = r25
            r11 = r26
            r12 = r27
            r2 = r28
            if (r9 == 0) goto L_0x0029
            java.lang.String r3 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Fail to send ["
            r4.append(r5)
            r4.append(r9)
            java.lang.String r5 = "]"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r3, r4)
        L_0x0029:
            r3 = 0
            if (r2 == 0) goto L_0x027f
            if (r12 == 0) goto L_0x027f
            java.util.HashMap<java.lang.Object, com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot> r13 = r8.mUnsentEventsMap
            monitor-enter(r13)
            r4 = 0
            if (r10 == 0) goto L_0x004f
            com.google.gson.Gson r5 = com.opengarden.firechat.matrixsdk.util.JsonUtils.getGson(r3)     // Catch:{ Exception -> 0x004f }
            okhttp3.ResponseBody r6 = r25.errorBody()     // Catch:{ Exception -> 0x004f }
            java.lang.String r6 = r6.string()     // Catch:{ Exception -> 0x004f }
            java.lang.Class<com.opengarden.firechat.matrixsdk.rest.model.MatrixError> r7 = com.opengarden.firechat.matrixsdk.rest.model.MatrixError.class
            java.lang.Object r5 = r5.fromJson(r6, r7)     // Catch:{ Exception -> 0x004f }
            com.opengarden.firechat.matrixsdk.rest.model.MatrixError r5 = (com.opengarden.firechat.matrixsdk.rest.model.MatrixError) r5     // Catch:{ Exception -> 0x004f }
            goto L_0x0050
        L_0x0049:
            r0 = move-exception
            r1 = r0
            r21 = r13
            goto L_0x027a
        L_0x004f:
            r5 = r4
        L_0x0050:
            if (r9 == 0) goto L_0x00a2
            if (r5 == 0) goto L_0x00a2
            java.lang.String r6 = LOG_TAG     // Catch:{ all -> 0x0049 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0049 }
            r7.<init>()     // Catch:{ all -> 0x0049 }
            java.lang.String r14 = "Matrix error "
            r7.append(r14)     // Catch:{ all -> 0x0049 }
            java.lang.String r14 = r5.errcode     // Catch:{ all -> 0x0049 }
            r7.append(r14)     // Catch:{ all -> 0x0049 }
            java.lang.String r14 = " "
            r7.append(r14)     // Catch:{ all -> 0x0049 }
            java.lang.String r14 = r5.getMessage()     // Catch:{ all -> 0x0049 }
            r7.append(r14)     // Catch:{ all -> 0x0049 }
            java.lang.String r14 = " ["
            r7.append(r14)     // Catch:{ all -> 0x0049 }
            r7.append(r9)     // Catch:{ all -> 0x0049 }
            java.lang.String r14 = "]"
            r7.append(r14)     // Catch:{ all -> 0x0049 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0049 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r6, r7)     // Catch:{ all -> 0x0049 }
            java.lang.String r6 = r5.errcode     // Catch:{ all -> 0x0049 }
            boolean r6 = com.opengarden.firechat.matrixsdk.rest.model.MatrixError.isConfigurationErrorCode(r6)     // Catch:{ all -> 0x0049 }
            if (r6 == 0) goto L_0x00a2
            java.lang.String r1 = LOG_TAG     // Catch:{ all -> 0x0049 }
            java.lang.String r2 = "## onEventSendingFailed() : invalid token detected"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)     // Catch:{ all -> 0x0049 }
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r8.mDataHandler     // Catch:{ all -> 0x0049 }
            java.lang.String r2 = r5.errcode     // Catch:{ all -> 0x0049 }
            r1.onConfigurationError(r2)     // Catch:{ all -> 0x0049 }
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r8.mDataHandler     // Catch:{ all -> 0x0049 }
            triggerErrorCallback(r1, r9, r10, r11, r12)     // Catch:{ all -> 0x0049 }
            monitor-exit(r13)     // Catch:{ all -> 0x0049 }
            return
        L_0x00a2:
            r6 = -1
            if (r5 == 0) goto L_0x00bd
            java.lang.String r7 = "M_LIMIT_EXCEEDED"
            java.lang.String r14 = r5.errcode     // Catch:{ all -> 0x0049 }
            boolean r7 = r7.equals(r14)     // Catch:{ all -> 0x0049 }
            if (r7 == 0) goto L_0x00bd
            java.lang.Integer r7 = r5.retry_after_ms     // Catch:{ all -> 0x0049 }
            if (r7 == 0) goto L_0x00bd
            java.lang.Integer r6 = r5.retry_after_ms     // Catch:{ all -> 0x0049 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0049 }
            int r6 = r6 + 200
            r14 = r6
            goto L_0x00be
        L_0x00bd:
            r14 = -1
        L_0x00be:
            if (r11 == 0) goto L_0x00d9
            com.opengarden.firechat.matrixsdk.ssl.UnrecognizedCertificateException r6 = com.opengarden.firechat.matrixsdk.ssl.CertUtil.getCertificateException(r26)     // Catch:{ all -> 0x0049 }
            if (r6 == 0) goto L_0x00d9
            java.lang.String r1 = LOG_TAG     // Catch:{ all -> 0x0049 }
            java.lang.String r2 = "## onEventSendingFailed() : SSL issue detected"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)     // Catch:{ all -> 0x0049 }
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r8.mDataHandler     // Catch:{ all -> 0x0049 }
            r1.onSSLCertificateError(r6)     // Catch:{ all -> 0x0049 }
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r8.mDataHandler     // Catch:{ all -> 0x0049 }
            triggerErrorCallback(r1, r9, r10, r11, r12)     // Catch:{ all -> 0x0049 }
            monitor-exit(r13)     // Catch:{ all -> 0x0049 }
            return
        L_0x00d9:
            if (r5 == 0) goto L_0x00f2
            boolean r6 = r5.isSupportedErrorCode()     // Catch:{ all -> 0x0049 }
            if (r6 == 0) goto L_0x00f2
            java.lang.String r6 = "M_LIMIT_EXCEEDED"
            java.lang.String r5 = r5.errcode     // Catch:{ all -> 0x0049 }
            boolean r5 = r6.equals(r5)     // Catch:{ all -> 0x0049 }
            if (r5 == 0) goto L_0x00ec
            goto L_0x00f2
        L_0x00ec:
            r4 = r9
            r2 = r12
            r21 = r13
            goto L_0x0274
        L_0x00f2:
            java.util.HashMap<java.lang.Object, com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot> r5 = r8.mUnsentEventsMap     // Catch:{ all -> 0x0276 }
            boolean r5 = r5.containsKey(r12)     // Catch:{ all -> 0x0276 }
            r6 = 180000(0x2bf20, double:8.8932E-319)
            r15 = 1
            if (r5 == 0) goto L_0x0166
            java.util.HashMap<java.lang.Object, com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot> r1 = r8.mUnsentEventsMap     // Catch:{ all -> 0x0049 }
            java.lang.Object r1 = r1.get(r12)     // Catch:{ all -> 0x0049 }
            com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot r1 = (com.opengarden.firechat.matrixsdk.util.UnsentEventsManager.UnsentEventSnapshot) r1     // Catch:{ all -> 0x0049 }
            r1.mIsResending = r3     // Catch:{ all -> 0x0049 }
            r1.stopTimer()     // Catch:{ all -> 0x0049 }
            if (r14 >= 0) goto L_0x0110
            r1.mRetryCount = r1.mRetryCount + 1     // Catch:{ all -> 0x0049 }
        L_0x0110:
            long r4 = r1.mAge     // Catch:{ all -> 0x0049 }
            r16 = 0
            int r2 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x0127
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0049 }
            long r16 = r1.mAge     // Catch:{ all -> 0x0049 }
            r2 = 0
            long r18 = r4 - r16
            r16 = r18
        L_0x0127:
            int r2 = (r16 > r6 ? 1 : (r16 == r6 ? 0 : -1))
            if (r2 > 0) goto L_0x0132
            int r2 = r1.mRetryCount     // Catch:{ all -> 0x0049 }
            r4 = 4
            if (r2 <= r4) goto L_0x015d
        L_0x0132:
            r1.stopTimers()     // Catch:{ all -> 0x0049 }
            java.util.HashMap<java.lang.Object, com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot> r2 = r8.mUnsentEventsMap     // Catch:{ all -> 0x0049 }
            r2.remove(r12)     // Catch:{ all -> 0x0049 }
            java.util.List<com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot> r2 = r8.mUnsentEvents     // Catch:{ all -> 0x0049 }
            r2.remove(r1)     // Catch:{ all -> 0x0049 }
            if (r9 == 0) goto L_0x015c
            java.lang.String r2 = LOG_TAG     // Catch:{ all -> 0x0049 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0049 }
            r4.<init>()     // Catch:{ all -> 0x0049 }
            java.lang.String r5 = "Cancel ["
            r4.append(r5)     // Catch:{ all -> 0x0049 }
            r4.append(r9)     // Catch:{ all -> 0x0049 }
            java.lang.String r5 = "]"
            r4.append(r5)     // Catch:{ all -> 0x0049 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0049 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r4)     // Catch:{ all -> 0x0049 }
        L_0x015c:
            r15 = 0
        L_0x015d:
            r4 = r9
            r2 = r12
            r21 = r13
            r20 = r14
            r9 = r1
            goto L_0x0242
        L_0x0166:
            com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot r5 = new com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot     // Catch:{ all -> 0x0276 }
            r5.<init>()     // Catch:{ all -> 0x0276 }
            if (r24 == 0) goto L_0x0170
            r3 = -1
            goto L_0x0174
        L_0x0170:
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ Throwable -> 0x01e3 }
        L_0x0174:
            r5.mAge = r3     // Catch:{ Throwable -> 0x01e3 }
            r5.mRequestRetryCallBack = r2     // Catch:{ Throwable -> 0x01e3 }
            r5.mRetryCount = r15     // Catch:{ Throwable -> 0x01e3 }
            r5.mEventDescription = r9     // Catch:{ Throwable -> 0x01e3 }
            java.util.HashMap<java.lang.Object, com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot> r2 = r8.mUnsentEventsMap     // Catch:{ Throwable -> 0x01e3 }
            r2.put(r12, r5)     // Catch:{ Throwable -> 0x01e3 }
            java.util.List<com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot> r2 = r8.mUnsentEvents     // Catch:{ Throwable -> 0x01e3 }
            r2.add(r5)     // Catch:{ Throwable -> 0x01e3 }
            boolean r2 = r8.mbIsConnected     // Catch:{ Throwable -> 0x01e3 }
            if (r2 != 0) goto L_0x01bc
            if (r24 != 0) goto L_0x0190
            goto L_0x01bc
        L_0x0190:
            if (r24 == 0) goto L_0x01b6
            java.lang.String r1 = LOG_TAG     // Catch:{ Throwable -> 0x01ae }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x01ae }
            r2.<init>()     // Catch:{ Throwable -> 0x01ae }
            java.lang.String r3 = "The request "
            r2.append(r3)     // Catch:{ Throwable -> 0x01ae }
            r2.append(r9)     // Catch:{ Throwable -> 0x01ae }
            java.lang.String r3 = " will be sent when a network will be available"
            r2.append(r3)     // Catch:{ Throwable -> 0x01ae }
            java.lang.String r2 = r2.toString()     // Catch:{ Throwable -> 0x01ae }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r2)     // Catch:{ Throwable -> 0x01ae }
            goto L_0x01b6
        L_0x01ae:
            r0 = move-exception
            r1 = r0
            r9 = r5
            r21 = r13
            r20 = r14
            goto L_0x01ea
        L_0x01b6:
            r9 = r5
            r21 = r13
            r20 = r14
            goto L_0x01dc
        L_0x01bc:
            java.util.Timer r1 = new java.util.Timer     // Catch:{ Throwable -> 0x01e3 }
            r1.<init>()     // Catch:{ Throwable -> 0x01e3 }
            r5.mLifeTimeTimer = r1     // Catch:{ Throwable -> 0x01e3 }
            java.util.Timer r4 = r5.mLifeTimeTimer     // Catch:{ Throwable -> 0x01e3 }
            com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$2 r3 = new com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$2     // Catch:{ Throwable -> 0x01e3 }
            r1 = r3
            r2 = r8
            r15 = r3
            r3 = r9
            r20 = r14
            r14 = r4
            r4 = r5
            r9 = r5
            r5 = r12
            r21 = r13
            r12 = r6
            r6 = r10
            r7 = r11
            r1.<init>(r3, r4, r5, r6, r7)     // Catch:{ Throwable -> 0x01e1 }
            r14.schedule(r15, r12)     // Catch:{ Throwable -> 0x01e1 }
        L_0x01dc:
            r2 = r27
            r4 = r23
            goto L_0x0241
        L_0x01e1:
            r0 = move-exception
            goto L_0x01e9
        L_0x01e3:
            r0 = move-exception
            r9 = r5
            r21 = r13
            r20 = r14
        L_0x01e9:
            r1 = r0
        L_0x01ea:
            java.lang.String r2 = LOG_TAG     // Catch:{ all -> 0x027c }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x027c }
            r3.<init>()     // Catch:{ all -> 0x027c }
            java.lang.String r4 = "## snapshot creation failed "
            r3.append(r4)     // Catch:{ all -> 0x027c }
            java.lang.String r1 = r1.getMessage()     // Catch:{ all -> 0x027c }
            r3.append(r1)     // Catch:{ all -> 0x027c }
            java.lang.String r1 = r3.toString()     // Catch:{ all -> 0x027c }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r1)     // Catch:{ all -> 0x027c }
            java.util.Timer r1 = r9.mLifeTimeTimer     // Catch:{ all -> 0x027c }
            if (r1 == 0) goto L_0x020d
            java.util.Timer r1 = r9.mLifeTimeTimer     // Catch:{ all -> 0x027c }
            r1.cancel()     // Catch:{ all -> 0x027c }
        L_0x020d:
            java.util.HashMap<java.lang.Object, com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot> r1 = r8.mUnsentEventsMap     // Catch:{ all -> 0x027c }
            r2 = r27
            r1.remove(r2)     // Catch:{ all -> 0x027c }
            java.util.List<com.opengarden.firechat.matrixsdk.util.UnsentEventsManager$UnsentEventSnapshot> r1 = r8.mUnsentEvents     // Catch:{ all -> 0x027c }
            r1.remove(r9)     // Catch:{ all -> 0x027c }
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r8.mDataHandler     // Catch:{ Exception -> 0x0223 }
            r4 = r23
            triggerErrorCallback(r1, r4, r10, r11, r2)     // Catch:{ Exception -> 0x0221 }
            goto L_0x0241
        L_0x0221:
            r0 = move-exception
            goto L_0x0226
        L_0x0223:
            r0 = move-exception
            r4 = r23
        L_0x0226:
            r1 = r0
            java.lang.String r3 = LOG_TAG     // Catch:{ all -> 0x027c }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x027c }
            r5.<init>()     // Catch:{ all -> 0x027c }
            java.lang.String r6 = "## onEventSendingFailed() : failure Msg="
            r5.append(r6)     // Catch:{ all -> 0x027c }
            java.lang.String r1 = r1.getMessage()     // Catch:{ all -> 0x027c }
            r5.append(r1)     // Catch:{ all -> 0x027c }
            java.lang.String r1 = r5.toString()     // Catch:{ all -> 0x027c }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r1)     // Catch:{ all -> 0x027c }
        L_0x0241:
            r15 = 1
        L_0x0242:
            if (r15 == 0) goto L_0x0273
            boolean r1 = r8.mbIsConnected     // Catch:{ all -> 0x027c }
            if (r1 == 0) goto L_0x0273
            r5 = 4611686018427387904(0x4000000000000000, double:2.0)
            int r1 = r9.mRetryCount     // Catch:{ all -> 0x027c }
            double r12 = (double) r1     // Catch:{ all -> 0x027c }
            double r5 = java.lang.Math.pow(r5, r12)     // Catch:{ all -> 0x027c }
            int r1 = (int) r5     // Catch:{ all -> 0x027c }
            java.util.Random r3 = new java.util.Random     // Catch:{ all -> 0x027c }
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x027c }
            r3.<init>(r5)     // Catch:{ all -> 0x027c }
            int r3 = r3.nextInt()     // Catch:{ all -> 0x027c }
            int r3 = java.lang.Math.abs(r3)     // Catch:{ all -> 0x027c }
            int r3 = r3 % 3000
            int r14 = r1 + r3
            if (r20 <= 0) goto L_0x026d
            r14 = r20
        L_0x026d:
            boolean r1 = r9.resendEventAfter(r14)     // Catch:{ all -> 0x027c }
            r3 = r1
            goto L_0x0274
        L_0x0273:
            r3 = r15
        L_0x0274:
            monitor-exit(r21)     // Catch:{ all -> 0x027c }
            goto L_0x0281
        L_0x0276:
            r0 = move-exception
            r21 = r13
            goto L_0x027d
        L_0x027a:
            monitor-exit(r21)     // Catch:{ all -> 0x027c }
            throw r1
        L_0x027c:
            r0 = move-exception
        L_0x027d:
            r1 = r0
            goto L_0x027a
        L_0x027f:
            r4 = r9
            r2 = r12
        L_0x0281:
            if (r3 != 0) goto L_0x028f
            java.lang.String r1 = LOG_TAG
            java.lang.String r3 = "Cannot resend it"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r3)
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r8.mDataHandler
            triggerErrorCallback(r1, r4, r10, r11, r2)
        L_0x028f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.util.UnsentEventsManager.onEventSendingFailed(java.lang.String, boolean, retrofit2.Response, java.lang.Exception, com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback, com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback$RequestRetryCallBack):void");
    }

    /* access modifiers changed from: private */
    public void resentUnsents() {
        Log.m209d(LOG_TAG, "resentUnsents");
        synchronized (this.mUnsentEventsMap) {
            if (this.mUnsentEvents.size() > 0) {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < this.mUnsentEvents.size(); i++) {
                    UnsentEventSnapshot unsentEventSnapshot = (UnsentEventSnapshot) this.mUnsentEvents.get(i);
                    if (!unsentEventSnapshot.waitToBeResent()) {
                        if (!unsentEventSnapshot.mIsResending) {
                            if (unsentEventSnapshot.mEventDescription != null) {
                                String str = LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("Automatically resend ");
                                sb.append(unsentEventSnapshot.mEventDescription);
                                Log.m209d(str, sb.toString());
                            }
                            try {
                                unsentEventSnapshot.mIsResending = true;
                                unsentEventSnapshot.mRequestRetryCallBack.onRetry();
                                break;
                            } catch (Exception e) {
                                unsentEventSnapshot.mIsResending = false;
                                arrayList.add(unsentEventSnapshot);
                                String str2 = LOG_TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("## resentUnsents() : ");
                                sb2.append(unsentEventSnapshot.mEventDescription);
                                sb2.append(" onRetry() failed ");
                                sb2.append(e.getMessage());
                                Log.m211e(str2, sb2.toString());
                            }
                        }
                    }
                }
                this.mUnsentEvents.removeAll(arrayList);
            }
        }
    }
}
