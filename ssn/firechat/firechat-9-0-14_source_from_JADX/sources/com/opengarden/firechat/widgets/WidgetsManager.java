package com.opengarden.firechat.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.google.android.gms.common.internal.ImagesContract;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WidgetsManager {
    private static final String LOG_TAG = "WidgetsManager";
    private static final String SCALAR_TOKEN_PREFERENCE_KEY = "SCALAR_TOKEN_PREFERENCE_KEY";
    public static final String WIDGET_EVENT_TYPE = "im.vector.modular.widgets";
    private static final String WIDGET_TYPE_JITSI = "jitsi";
    public static final String WIDGET_USER_EVENT_TYPE = "m.widgets";
    private static final Set<onWidgetUpdateListener> mListeners = new HashSet();
    private static final WidgetsManager mSharedInstance = new WidgetsManager();
    /* access modifiers changed from: private */
    public final Map<String, ApiCallback<Widget>> mPendingWidgetCreationCallbacks = new HashMap();

    public class WidgetError extends MatrixError {
        public static final String WIDGET_CREATION_FAILED_ERROR_CODE = "WIDGET_CREATION_FAILED_ERROR_CODE";
        public static final String WIDGET_NOT_ENOUGH_POWER_ERROR_CODE = "WIDGET_NOT_ENOUGH_POWER_ERROR_CODE";

        public WidgetError(String str, String str2) {
            this.errcode = str;
            this.error = str2;
        }
    }

    public interface onWidgetUpdateListener {
        void onWidgetUpdate(Widget widget);
    }

    public static WidgetsManager getSharedInstance() {
        return mSharedInstance;
    }

    public List<Widget> getActiveWidgets(MXSession mXSession, Room room) {
        return getActiveWidgets(mXSession, room, null, null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0070  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<com.opengarden.firechat.widgets.Widget> getActiveWidgets(com.opengarden.firechat.matrixsdk.MXSession r9, com.opengarden.firechat.matrixsdk.data.Room r10, java.util.Set<java.lang.String> r11, java.util.Set<java.lang.String> r12) {
        /*
            r8 = this;
            com.opengarden.firechat.matrixsdk.data.RoomState r0 = r10.getLiveState()
            java.util.HashSet r1 = new java.util.HashSet
            r2 = 1
            java.lang.String[] r2 = new java.lang.String[r2]
            java.lang.String r3 = "im.vector.modular.widgets"
            r4 = 0
            r2[r4] = r3
            java.util.List r2 = java.util.Arrays.asList(r2)
            r1.<init>(r2)
            java.util.List r0 = r0.getStateEvents(r1)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            com.opengarden.firechat.widgets.WidgetsManager$1 r2 = new com.opengarden.firechat.widgets.WidgetsManager$1
            r2.<init>()
            java.util.Collections.sort(r0, r2)
            java.util.Iterator r0 = r0.iterator()
        L_0x002a:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x00dd
            java.lang.Object r2 = r0.next()
            com.opengarden.firechat.matrixsdk.rest.model.Event r2 = (com.opengarden.firechat.matrixsdk.rest.model.Event) r2
            r3 = 0
            if (r11 != 0) goto L_0x003b
            if (r12 == 0) goto L_0x0082
        L_0x003b:
            com.google.gson.JsonObject r4 = r2.getContentAsJsonObject()     // Catch:{ Exception -> 0x0052 }
            java.lang.String r5 = "type"
            boolean r5 = r4.has(r5)     // Catch:{ Exception -> 0x0052 }
            if (r5 == 0) goto L_0x006d
            java.lang.String r5 = "type"
            com.google.gson.JsonElement r4 = r4.get(r5)     // Catch:{ Exception -> 0x0052 }
            java.lang.String r4 = r4.getAsString()     // Catch:{ Exception -> 0x0052 }
            goto L_0x006e
        L_0x0052:
            r4 = move-exception
            java.lang.String r5 = LOG_TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "## getWidgets() failed : "
            r6.append(r7)
            java.lang.String r4 = r4.getMessage()
            r6.append(r4)
            java.lang.String r4 = r6.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r4)
        L_0x006d:
            r4 = r3
        L_0x006e:
            if (r4 == 0) goto L_0x0082
            if (r11 == 0) goto L_0x0079
            boolean r5 = r11.contains(r4)
            if (r5 != 0) goto L_0x0079
            goto L_0x002a
        L_0x0079:
            if (r12 == 0) goto L_0x0082
            boolean r4 = r12.contains(r4)
            if (r4 == 0) goto L_0x0082
            goto L_0x002a
        L_0x0082:
            java.lang.String r4 = r2.stateKey
            if (r4 == 0) goto L_0x002a
            java.lang.String r4 = r2.stateKey
            boolean r4 = r1.containsKey(r4)
            if (r4 != 0) goto L_0x002a
            java.lang.String r4 = r2.roomId     // Catch:{ Exception -> 0x00b7 }
            if (r4 != 0) goto L_0x00b0
            java.lang.String r4 = LOG_TAG     // Catch:{ Exception -> 0x00b7 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b7 }
            r5.<init>()     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r6 = "## getWidgets() : set the room id to the event "
            r5.append(r6)     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r6 = r2.eventId     // Catch:{ Exception -> 0x00b7 }
            r5.append(r6)     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x00b7 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r5)     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r4 = r10.getRoomId()     // Catch:{ Exception -> 0x00b7 }
            r2.roomId = r4     // Catch:{ Exception -> 0x00b7 }
        L_0x00b0:
            com.opengarden.firechat.widgets.Widget r4 = new com.opengarden.firechat.widgets.Widget     // Catch:{ Exception -> 0x00b7 }
            r4.<init>(r9, r2)     // Catch:{ Exception -> 0x00b7 }
            r3 = r4
            goto L_0x00d2
        L_0x00b7:
            r2 = move-exception
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "## getWidgets() : widget creation failed "
            r5.append(r6)
            java.lang.String r2 = r2.getMessage()
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r2)
        L_0x00d2:
            if (r3 == 0) goto L_0x002a
            java.lang.String r2 = r3.getWidgetId()
            r1.put(r2, r3)
            goto L_0x002a
        L_0x00dd:
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            java.util.Collection r10 = r1.values()
            java.util.Iterator r10 = r10.iterator()
        L_0x00ea:
            boolean r11 = r10.hasNext()
            if (r11 == 0) goto L_0x0100
            java.lang.Object r11 = r10.next()
            com.opengarden.firechat.widgets.Widget r11 = (com.opengarden.firechat.widgets.Widget) r11
            boolean r12 = r11.isActive()
            if (r12 == 0) goto L_0x00ea
            r9.add(r11)
            goto L_0x00ea
        L_0x0100:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.widgets.WidgetsManager.getActiveWidgets(com.opengarden.firechat.matrixsdk.MXSession, com.opengarden.firechat.matrixsdk.data.Room, java.util.Set, java.util.Set):java.util.List");
    }

    public List<Widget> getActiveJitsiWidgets(MXSession mXSession, Room room) {
        return getActiveWidgets(mXSession, room, new HashSet(Arrays.asList(new String[]{WIDGET_TYPE_JITSI})), null);
    }

    public List<Widget> getActiveWebviewWidgets(MXSession mXSession, Room room) {
        return getActiveWidgets(mXSession, room, null, new HashSet(Arrays.asList(new String[]{WIDGET_TYPE_JITSI})));
    }

    public WidgetError checkWidgetPermission(MXSession mXSession, Room room) {
        if (room == null || room.getLiveState() == null || room.getLiveState().getPowerLevels() == null || room.getLiveState().getPowerLevels().getUserPowerLevel(mXSession.getMyUserId()) >= room.getLiveState().getPowerLevels().state_default) {
            return null;
        }
        return new WidgetError(WidgetError.WIDGET_NOT_ENOUGH_POWER_ERROR_CODE, VectorApp.getInstance().getString(C1299R.string.widget_no_power_to_manage));
    }

    private void createWidget(MXSession mXSession, Room room, String str, Map<String, Object> map, final ApiCallback<Widget> apiCallback) {
        WidgetError checkWidgetPermission = checkWidgetPermission(mXSession, room);
        if (checkWidgetPermission != null) {
            if (apiCallback != null) {
                apiCallback.onMatrixError(checkWidgetPermission);
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(mXSession.getMyUserId());
        sb.append("_");
        sb.append(str);
        final String sb2 = sb.toString();
        if (apiCallback != null) {
            this.mPendingWidgetCreationCallbacks.put(sb2, apiCallback);
        }
        mXSession.getRoomsApiClient().sendStateEvent(room.getRoomId(), WIDGET_EVENT_TYPE, str, map, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
            }

            public void onNetworkError(Exception exc) {
                if (apiCallback != null) {
                    apiCallback.onNetworkError(exc);
                }
                WidgetsManager.this.mPendingWidgetCreationCallbacks.remove(sb2);
            }

            public void onMatrixError(MatrixError matrixError) {
                if (apiCallback != null) {
                    apiCallback.onMatrixError(matrixError);
                }
                WidgetsManager.this.mPendingWidgetCreationCallbacks.remove(sb2);
            }

            public void onUnexpectedError(Exception exc) {
                if (apiCallback != null) {
                    apiCallback.onUnexpectedError(exc);
                }
                WidgetsManager.this.mPendingWidgetCreationCallbacks.remove(sb2);
            }
        });
    }

    public void createJitsiWidget(MXSession mXSession, Room room, boolean z, ApiCallback<Widget> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("jitsi_");
        sb.append(mXSession.getMyUserId());
        sb.append("_");
        sb.append(System.currentTimeMillis());
        String sb2 = sb.toString();
        String uuid = UUID.randomUUID().toString();
        if (uuid.length() > 8) {
            uuid = uuid.substring(0, 7);
        }
        String roomId = room.getRoomId();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(roomId.substring(1, roomId.indexOf(":") - 1));
        sb3.append(uuid.toLowerCase(VectorApp.getApplicationLocale()));
        String sb4 = sb3.toString();
        StringBuilder sb5 = new StringBuilder();
        sb5.append("https://scalar.vector.im/api/widgets/jitsi.html?confId=");
        sb5.append(sb4);
        sb5.append("&isAudioConf=");
        sb5.append(z ? "false" : "true");
        sb5.append("&displayName=$matrix_display_name&avatarUrl=$matrix_avatar_url&email=$matrix_user_id");
        String sb6 = sb5.toString();
        HashMap hashMap = new HashMap();
        hashMap.put(ImagesContract.URL, sb6);
        hashMap.put("type", WIDGET_TYPE_JITSI);
        HashMap hashMap2 = new HashMap();
        hashMap2.put("widgetSessionId", uuid);
        hashMap.put("data", hashMap2);
        createWidget(mXSession, room, sb2, hashMap, apiCallback);
    }

    public void closeWidget(MXSession mXSession, Room room, String str, ApiCallback<Void> apiCallback) {
        if (!(mXSession == null || room == null || str == null)) {
            WidgetError checkWidgetPermission = checkWidgetPermission(mXSession, room);
            if (checkWidgetPermission != null) {
                if (apiCallback != null) {
                    apiCallback.onMatrixError(checkWidgetPermission);
                }
                return;
            }
            mXSession.getRoomsApiClient().sendStateEvent(room.getRoomId(), WIDGET_EVENT_TYPE, str, new HashMap(), apiCallback);
        }
    }

    public static void addListener(onWidgetUpdateListener onwidgetupdatelistener) {
        if (onwidgetupdatelistener != null) {
            synchronized (mListeners) {
                mListeners.add(onwidgetupdatelistener);
            }
        }
    }

    public static void removeListener(onWidgetUpdateListener onwidgetupdatelistener) {
        if (onwidgetupdatelistener != null) {
            synchronized (mListeners) {
                mListeners.remove(onwidgetupdatelistener);
            }
        }
    }

    private void onWidgetUpdate(Widget widget) {
        synchronized (mListeners) {
            for (onWidgetUpdateListener onWidgetUpdate : mListeners) {
                try {
                    onWidgetUpdate.onWidgetUpdate(widget);
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onWidgetUpdate failed: ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }
    }

    public void onLiveEvent(MXSession mXSession, Event event) {
        if (TextUtils.equals(WIDGET_EVENT_TYPE, event.getType())) {
            String str = event.stateKey;
            StringBuilder sb = new StringBuilder();
            sb.append(mXSession.getMyUserId());
            sb.append("_");
            sb.append(str);
            String sb2 = sb.toString();
            String str2 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## onLiveEvent() : New widget detected: ");
            sb3.append(str);
            sb3.append(" in room ");
            sb3.append(event.roomId);
            Log.m209d(str2, sb3.toString());
            Widget widget = null;
            try {
                widget = new Widget(mXSession, event);
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("## onLiveEvent () : widget creation failed ");
                sb4.append(e.getMessage());
                Log.m211e(str3, sb4.toString());
            }
            if (widget != null) {
                if (this.mPendingWidgetCreationCallbacks.containsKey(sb2)) {
                    try {
                        ((ApiCallback) this.mPendingWidgetCreationCallbacks.get(sb2)).onSuccess(widget);
                    } catch (Exception e2) {
                        String str4 = LOG_TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("## onLiveEvent() : get(callbackKey).onSuccess failed ");
                        sb5.append(e2.getMessage());
                        Log.m211e(str4, sb5.toString());
                    }
                }
                onWidgetUpdate(widget);
            } else {
                String str5 = LOG_TAG;
                StringBuilder sb6 = new StringBuilder();
                sb6.append("## onLiveEvent() : Cannot decode new widget - event: ");
                sb6.append(event);
                Log.m211e(str5, sb6.toString());
                if (this.mPendingWidgetCreationCallbacks.containsKey(sb2)) {
                    try {
                        ((ApiCallback) this.mPendingWidgetCreationCallbacks.get(sb2)).onMatrixError(new WidgetError(WidgetError.WIDGET_CREATION_FAILED_ERROR_CODE, VectorApp.getInstance().getString(C1299R.string.widget_creation_failure)));
                    } catch (Exception e3) {
                        String str6 = LOG_TAG;
                        StringBuilder sb7 = new StringBuilder();
                        sb7.append("## onLiveEvent() : get(callbackKey).onMatrixError failed ");
                        sb7.append(e3.getMessage());
                        Log.m211e(str6, sb7.toString());
                    }
                }
            }
            this.mPendingWidgetCreationCallbacks.remove(sb2);
        }
    }

    public static void getFormattedWidgetUrl(Context context, final Widget widget, final ApiCallback<String> apiCallback) {
        getScalarToken(context, Matrix.getInstance(context).getSession(widget.getSessionId()), new ApiCallback<String>() {
            public void onSuccess(String str) {
                if (str == null) {
                    apiCallback.onSuccess(widget.getUrl());
                    return;
                }
                ApiCallback apiCallback = apiCallback;
                StringBuilder sb = new StringBuilder();
                sb.append(widget.getUrl());
                sb.append("&scalar_token=");
                sb.append(str);
                apiCallback.onSuccess(sb.toString());
            }

            public void onNetworkError(Exception exc) {
                if (apiCallback != null) {
                    apiCallback.onNetworkError(exc);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                if (apiCallback != null) {
                    apiCallback.onMatrixError(matrixError);
                }
            }

            public void onUnexpectedError(Exception exc) {
                if (apiCallback != null) {
                    apiCallback.onUnexpectedError(exc);
                }
            }
        });
    }

    public static void getScalarToken(final Context context, MXSession mXSession, final ApiCallback<String> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append(SCALAR_TOKEN_PREFERENCE_KEY);
        sb.append(mXSession.getMyUserId());
        final String sb2 = sb.toString();
        final String string = PreferenceManager.getDefaultSharedPreferences(context).getString(sb2, null);
        if (string != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if (string != null) {
                        apiCallback.onSuccess(string);
                    }
                }
            });
        } else {
            mXSession.openIdToken(new ApiCallback<Map<Object, Object>>() {
                public void onSuccess(Map<Object, Object> map) {
                    new WidgetsRestClient(context).register(map, new ApiCallback<Map<String, String>>() {
                        public void onSuccess(Map<String, String> map) {
                            String str = (String) map.get("scalar_token");
                            if (str != null) {
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(sb2, str).apply();
                            }
                            if (apiCallback != null) {
                                apiCallback.onSuccess(str);
                            }
                        }

                        public void onNetworkError(Exception exc) {
                            if (apiCallback != null) {
                                apiCallback.onNetworkError(exc);
                            }
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            if (apiCallback != null) {
                                apiCallback.onMatrixError(matrixError);
                            }
                        }

                        public void onUnexpectedError(Exception exc) {
                            if (apiCallback != null) {
                                apiCallback.onUnexpectedError(exc);
                            }
                        }
                    });
                }

                public void onNetworkError(Exception exc) {
                    if (apiCallback != null) {
                        apiCallback.onNetworkError(exc);
                    }
                }

                public void onMatrixError(MatrixError matrixError) {
                    if (apiCallback != null) {
                        apiCallback.onMatrixError(matrixError);
                    }
                }

                public void onUnexpectedError(Exception exc) {
                    if (apiCallback != null) {
                        apiCallback.onUnexpectedError(exc);
                    }
                }
            });
        }
    }

    public static void clearScalarToken(Context context, MXSession mXSession) {
        StringBuilder sb = new StringBuilder();
        sb.append(SCALAR_TOKEN_PREFERENCE_KEY);
        sb.append(mXSession.getMyUserId());
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(sb.toString()).apply();
    }
}
