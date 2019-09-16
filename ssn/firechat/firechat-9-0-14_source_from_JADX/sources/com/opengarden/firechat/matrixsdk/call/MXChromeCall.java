package com.opengarden.firechat.matrixsdk.call;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.p000v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.amplitude.api.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class MXChromeCall extends MXCall {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXChromeCall";
    private JsonObject mCallInviteParams = null;
    /* access modifiers changed from: private */
    public CallWebAppInterface mCallWebAppInterface = null;
    /* access modifiers changed from: private */
    public boolean mIsIncomingPrepared = false;
    private JsonArray mPendingCandidates = new JsonArray();
    /* access modifiers changed from: private */
    public WebView mWebView = null;

    private class CallWebAppInterface {
        public String mCallState = IMXCall.CALL_STATE_CREATING_CALL_VIEW;
        /* access modifiers changed from: private */
        public Timer mCallTimeoutTimer = null;

        CallWebAppInterface() {
            if (MXChromeCall.this.mCallingRoom == null) {
                throw new AssertionError("MXChromeCall : room cannot be null");
            }
        }

        @JavascriptInterface
        public String wgetCallId() {
            return MXChromeCall.this.mCallId;
        }

        @JavascriptInterface
        public String wgetRoomId() {
            return MXChromeCall.this.mCallSignalingRoom.getRoomId();
        }

        @JavascriptInterface
        public String wgetTurnServer() {
            if (MXChromeCall.this.mTurnServer != null) {
                return MXChromeCall.this.mTurnServer.toString();
            }
            return null;
        }

        @JavascriptInterface
        public void wlog(String str) {
            String access$300 = MXChromeCall.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("WebView Message : ");
            sb.append(str);
            Log.m209d(access$300, sb.toString());
        }

        @JavascriptInterface
        public void wCallError(String str) {
            String access$300 = MXChromeCall.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("WebView error Message : ");
            sb.append(str);
            Log.m211e(access$300, sb.toString());
            if ("ice_failed".equals(str)) {
                MXChromeCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_ICE_FAILED);
            } else if ("user_media_failed".equals(str)) {
                MXChromeCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
            }
        }

        @JavascriptInterface
        public void wOnStateUpdate(String str) {
            String str2 = "fledgling".equals(str) ? IMXCall.CALL_STATE_READY : "wait_local_media".equals(str) ? IMXCall.CALL_STATE_WAIT_LOCAL_MEDIA : "create_offer".equals(str) ? IMXCall.CALL_STATE_WAIT_CREATE_OFFER : "invite_sent".equals(str) ? IMXCall.CALL_STATE_INVITE_SENT : "ringing".equals(str) ? IMXCall.CALL_STATE_RINGING : "create_answer".equals(str) ? IMXCall.CALL_STATE_CREATE_ANSWER : "connecting".equals(str) ? IMXCall.CALL_STATE_CONNECTING : "connected".equals(str) ? IMXCall.CALL_STATE_CONNECTED : "ended".equals(str) ? IMXCall.CALL_STATE_ENDED : null;
            if (str2 != null && !this.mCallState.equals(str2)) {
                this.mCallState = str2;
                MXChromeCall.this.mUIThreadHandler.post(new Runnable() {
                    public void run() {
                        if ((IMXCall.CALL_STATE_CONNECTING.equals(CallWebAppInterface.this.mCallState) || IMXCall.CALL_STATE_CONNECTING.equals(CallWebAppInterface.this.mCallState)) && CallWebAppInterface.this.mCallTimeoutTimer != null) {
                            CallWebAppInterface.this.mCallTimeoutTimer.cancel();
                            CallWebAppInterface.this.mCallTimeoutTimer = null;
                        }
                        MXChromeCall.this.dispatchOnStateDidChange(CallWebAppInterface.this.mCallState);
                    }
                });
            }
        }

        @JavascriptInterface
        public void wOnLoaded() {
            this.mCallState = IMXCall.CALL_STATE_READY;
            MXChromeCall.this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXChromeCall.this.dispatchOnReady();
                }
            });
        }

        /* access modifiers changed from: private */
        public void sendHangup(final Event event) {
            if (this.mCallTimeoutTimer != null) {
                this.mCallTimeoutTimer.cancel();
                this.mCallTimeoutTimer = null;
            }
            MXChromeCall.this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXChromeCall.this.dispatchOnCallEnd(-1);
                }
            });
            MXChromeCall.this.mPendingEvents.clear();
            MXChromeCall.this.mCallSignalingRoom.sendEvent(event, new ApiCallback<Void>() {
                public void onMatrixError(MatrixError matrixError) {
                }

                public void onSuccess(Void voidR) {
                }

                public void onUnexpectedError(Exception exc) {
                }

                public void onNetworkError(Exception exc) {
                    CallWebAppInterface.this.sendHangup(event);
                }
            });
        }

        @JavascriptInterface
        public void wSendEvent(String str, final String str2, final String str3) {
            MXChromeCall.this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    try {
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(str3);
                        boolean z = true;
                        if (TextUtils.equals(str2, Event.EVENT_TYPE_CALL_CANDIDATES) && MXChromeCall.this.mPendingEvents.size() > 0) {
                            try {
                                Event event = (Event) MXChromeCall.this.mPendingEvents.get(MXChromeCall.this.mPendingEvents.size() - 1);
                                if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_CALL_CANDIDATES)) {
                                    JsonObject contentAsJsonObject = event.getContentAsJsonObject();
                                    JsonArray asJsonArray = contentAsJsonObject.get("candidates").getAsJsonArray();
                                    JsonArray asJsonArray2 = jsonObject.get("candidates").getAsJsonArray();
                                    String access$300 = MXChromeCall.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Merge candidates from ");
                                    sb.append(asJsonArray.size());
                                    sb.append(" to ");
                                    sb.append(asJsonArray.size() + asJsonArray2.size());
                                    sb.append(" items.");
                                    Log.m209d(access$300, sb.toString());
                                    asJsonArray.addAll(asJsonArray2);
                                    contentAsJsonObject.remove("candidates");
                                    contentAsJsonObject.add("candidates", asJsonArray);
                                    z = false;
                                }
                            } catch (Exception e) {
                                String access$3002 = MXChromeCall.LOG_TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("## wSendEvent() ; ");
                                sb2.append(e.getMessage());
                                Log.m211e(access$3002, sb2.toString());
                            }
                        }
                        if (z) {
                            Event event2 = new Event(str2, jsonObject, MXChromeCall.this.mSession.getCredentials().userId, MXChromeCall.this.mCallSignalingRoom.getRoomId());
                            if (event2 != null) {
                                if (TextUtils.equals(str2, Event.EVENT_TYPE_CALL_HANGUP)) {
                                    CallWebAppInterface.this.sendHangup(event2);
                                } else {
                                    MXChromeCall.this.mPendingEvents.add(event2);
                                }
                                if (TextUtils.equals(str2, Event.EVENT_TYPE_CALL_INVITE)) {
                                    try {
                                        CallWebAppInterface.this.mCallTimeoutTimer = new Timer();
                                        CallWebAppInterface.this.mCallTimeoutTimer.schedule(new TimerTask() {
                                            public void run() {
                                                try {
                                                    if (MXChromeCall.this.getCallState().equals(IMXCall.CALL_STATE_RINGING) || MXChromeCall.this.getCallState().equals(IMXCall.CALL_STATE_INVITE_SENT)) {
                                                        MXChromeCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_USER_NOT_RESPONDING);
                                                        MXChromeCall.this.hangup(null);
                                                    }
                                                    CallWebAppInterface.this.mCallTimeoutTimer.cancel();
                                                    CallWebAppInterface.this.mCallTimeoutTimer = null;
                                                } catch (Exception e) {
                                                    String access$300 = MXChromeCall.LOG_TAG;
                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append("## wSendEvent() ; ");
                                                    sb.append(e.getMessage());
                                                    Log.m211e(access$300, sb.toString());
                                                }
                                            }
                                        }, 120000);
                                    } catch (Throwable th) {
                                        if (CallWebAppInterface.this.mCallTimeoutTimer != null) {
                                            CallWebAppInterface.this.mCallTimeoutTimer.cancel();
                                            CallWebAppInterface.this.mCallTimeoutTimer = null;
                                        }
                                        String access$3003 = MXChromeCall.LOG_TAG;
                                        StringBuilder sb3 = new StringBuilder();
                                        sb3.append("## wSendEvent() ; ");
                                        sb3.append(th.getMessage());
                                        Log.m211e(access$3003, sb3.toString());
                                    }
                                }
                            }
                        }
                        MXChromeCall.this.sendNextEvent();
                    } catch (Exception e2) {
                        String access$3004 = MXChromeCall.LOG_TAG;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("## wSendEvent() ; ");
                        sb4.append(e2.getMessage());
                        Log.m211e(access$3004, sb4.toString());
                    }
                }
            });
        }
    }

    public static boolean isSupported() {
        return VERSION.SDK_INT >= 21;
    }

    public MXChromeCall(MXSession mXSession, Context context, JsonElement jsonElement) {
        if (!isSupported()) {
            throw new AssertionError("MXChromeCall : not supported with the current android version");
        } else if (mXSession == null) {
            throw new AssertionError("MXChromeCall : session cannot be null");
        } else if (context == null) {
            throw new AssertionError("MXChromeCall : context cannot be null");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("c");
            sb.append(System.currentTimeMillis());
            this.mCallId = sb.toString();
            this.mSession = mXSession;
            this.mContext = context;
            this.mTurnServer = jsonElement;
        }
    }

    @SuppressLint({"NewApi"})
    public void createCallView() {
        super.createCallView();
        this.mUIThreadHandler.post(new Runnable() {
            public void run() {
                MXChromeCall.this.mWebView = new WebView(MXChromeCall.this.mContext);
                MXChromeCall.this.mWebView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
                MXChromeCall.this.dispatchOnCallViewCreated(MXChromeCall.this.mWebView);
                MXChromeCall.this.mUIThreadHandler.post(new Runnable() {
                    public void run() {
                        MXChromeCall.this.mCallWebAppInterface = new CallWebAppInterface();
                        MXChromeCall.this.mWebView.addJavascriptInterface(MXChromeCall.this.mCallWebAppInterface, Constants.PLATFORM);
                        WebView.setWebContentsDebuggingEnabled(true);
                        WebSettings settings = MXChromeCall.this.mWebView.getSettings();
                        settings.setJavaScriptEnabled(true);
                        settings.setUseWideViewPort(true);
                        settings.setLoadWithOverviewMode(true);
                        settings.setBuiltInZoomControls(true);
                        settings.setDomStorageEnabled(true);
                        settings.setAllowFileAccessFromFileURLs(true);
                        settings.setAllowUniversalAccessFromFileURLs(true);
                        settings.setDisplayZoomControls(false);
                        MXChromeCall.this.mWebView.setWebViewClient(new WebViewClient());
                        CookieManager.getInstance().setAcceptThirdPartyCookies(MXChromeCall.this.mWebView, true);
                        MXChromeCall.this.mWebView.loadUrl("file:///android_asset/www/call.html");
                        MXChromeCall.this.mWebView.setWebChromeClient(new WebChromeClient() {
                            public void onPermissionRequest(final PermissionRequest permissionRequest) {
                                MXChromeCall.this.mUIThreadHandler.post(new Runnable() {
                                    public void run() {
                                        permissionRequest.grant(permissionRequest.getResources());
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void placeCall(VideoLayoutConfiguration videoLayoutConfiguration) {
        super.placeCall(videoLayoutConfiguration);
        if (IMXCall.CALL_STATE_READY.equals(getCallState())) {
            this.mIsIncoming = false;
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXChromeCall.this.mWebView.loadUrl(MXChromeCall.this.mIsVideoCall ? "javascript:placeVideoCall()" : "javascript:placeVoiceCall()");
                }
            });
        }
    }

    public void prepareIncomingCall(final JsonObject jsonObject, final String str, VideoLayoutConfiguration videoLayoutConfiguration) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## prepareIncomingCall : call state ");
        sb.append(getCallState());
        Log.m209d(str2, sb.toString());
        super.prepareIncomingCall(jsonObject, str, videoLayoutConfiguration);
        this.mCallId = str;
        if (IMXCall.CALL_STATE_READY.equals(getCallState())) {
            this.mIsIncoming = true;
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    WebView access$000 = MXChromeCall.this.mWebView;
                    StringBuilder sb = new StringBuilder();
                    sb.append("javascript:initWithInvite('");
                    sb.append(str);
                    sb.append("',");
                    sb.append(jsonObject.toString());
                    sb.append(")");
                    access$000.loadUrl(sb.toString());
                    MXChromeCall.this.mIsIncomingPrepared = true;
                    MXChromeCall.this.mWebView.post(new Runnable() {
                        public void run() {
                            MXChromeCall.this.checkPendingCandidates();
                        }
                    });
                }
            });
        } else if (IMXCall.CALL_STATE_CREATED.equals(getCallState())) {
            this.mCallInviteParams = jsonObject;
            try {
                setIsVideo(this.mCallInviteParams.get("offer").getAsJsonObject().get("sdp").getAsString().contains("m=video"));
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## prepareIncomingCall() ; ");
                sb2.append(e.getMessage());
                Log.m211e(str3, sb2.toString());
            }
        }
    }

    public void launchIncomingCall(VideoLayoutConfiguration videoLayoutConfiguration) {
        super.launchIncomingCall(videoLayoutConfiguration);
        if (IMXCall.CALL_STATE_READY.equals(getCallState())) {
            prepareIncomingCall(this.mCallInviteParams, this.mCallId, null);
        }
    }

    private void onCallAnswer(final Event event) {
        if (!IMXCall.CALL_STATE_CREATED.equals(getCallState()) && this.mWebView != null) {
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    WebView access$000 = MXChromeCall.this.mWebView;
                    StringBuilder sb = new StringBuilder();
                    sb.append("javascript:receivedAnswer(");
                    sb.append(event.getContent().toString());
                    sb.append(")");
                    access$000.loadUrl(sb.toString());
                }
            });
        }
    }

    private void onCallHangup(final Event event) {
        if (!IMXCall.CALL_STATE_CREATED.equals(getCallState()) && this.mWebView != null) {
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    WebView access$000 = MXChromeCall.this.mWebView;
                    StringBuilder sb = new StringBuilder();
                    sb.append("javascript:onHangupReceived(");
                    sb.append(event.getContent().toString());
                    sb.append(")");
                    access$000.loadUrl(sb.toString());
                    MXChromeCall.this.mWebView.post(new Runnable() {
                        public void run() {
                            MXChromeCall.this.dispatchOnCallEnd(0);
                        }
                    });
                }
            });
        }
    }

    public void onNewCandidates(final JsonElement jsonElement) {
        if (!IMXCall.CALL_STATE_CREATED.equals(getCallState()) && this.mWebView != null) {
            this.mWebView.post(new Runnable() {
                public void run() {
                    WebView access$000 = MXChromeCall.this.mWebView;
                    StringBuilder sb = new StringBuilder();
                    sb.append("javascript:gotRemoteCandidates(");
                    sb.append(jsonElement.toString());
                    sb.append(")");
                    access$000.loadUrl(sb.toString());
                }
            });
        }
    }

    private void addCandidates(JsonArray jsonArray) {
        if (this.mIsIncomingPrepared || !isIncoming()) {
            onNewCandidates(jsonArray);
            return;
        }
        synchronized (LOG_TAG) {
            this.mPendingCandidates.addAll(jsonArray);
        }
    }

    public void checkPendingCandidates() {
        synchronized (LOG_TAG) {
            onNewCandidates(this.mPendingCandidates);
            this.mPendingCandidates = new JsonArray();
        }
    }

    public void handleCallEvent(Event event) {
        super.handleCallEvent(event);
        String type = event.getType();
        if (!event.isCallEvent()) {
            return;
        }
        if (!TextUtils.equals(event.getSender(), this.mSession.getMyUserId())) {
            if (Event.EVENT_TYPE_CALL_ANSWER.equals(type) && !this.mIsIncoming) {
                onCallAnswer(event);
            } else if (Event.EVENT_TYPE_CALL_CANDIDATES.equals(type)) {
                addCandidates(event.getContentAsJsonObject().getAsJsonArray("candidates"));
            } else if (Event.EVENT_TYPE_CALL_HANGUP.equals(type)) {
                onCallHangup(event);
            }
        } else if (Event.EVENT_TYPE_CALL_INVITE.equals(type)) {
            this.mCallWebAppInterface.mCallState = IMXCall.CALL_STATE_RINGING;
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXChromeCall.this.dispatchOnStateDidChange(MXChromeCall.this.mCallWebAppInterface.mCallState);
                }
            });
        } else if (Event.EVENT_TYPE_CALL_ANSWER.equals(type)) {
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    if (MXChromeCall.this.getCallState().equals(IMXCall.CALL_STATE_RINGING)) {
                        MXChromeCall.this.onAnsweredElsewhere();
                    }
                }
            });
        }
    }

    public void answer() {
        super.answer();
        if (!IMXCall.CALL_STATE_CREATED.equals(getCallState()) && this.mWebView != null) {
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXChromeCall.this.mWebView.loadUrl("javascript:answerCall()");
                }
            });
        }
    }

    public void hangup(String str) {
        super.hangup(str);
        if (IMXCall.CALL_STATE_CREATED.equals(getCallState()) || this.mWebView == null) {
            sendHangup(str);
        } else {
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXChromeCall.this.mWebView.loadUrl("javascript:hangup()");
                }
            });
        }
    }

    public String getCallState() {
        return this.mCallWebAppInterface != null ? this.mCallWebAppInterface.mCallState : IMXCall.CALL_STATE_CREATED;
    }

    public View getCallView() {
        return this.mWebView;
    }

    public int getVisibility() {
        if (this.mWebView != null) {
            return this.mWebView.getVisibility();
        }
        return 8;
    }

    public boolean setVisibility(int i) {
        if (this.mWebView == null) {
            return false;
        }
        this.mWebView.setVisibility(i);
        return true;
    }

    public void onAnsweredElsewhere() {
        super.onAnsweredElsewhere();
        this.mUIThreadHandler.post(new Runnable() {
            public void run() {
                MXChromeCall.this.mWebView.loadUrl("javascript:onAnsweredElsewhere()");
            }
        });
        dispatchAnsweredElsewhere();
    }
}
