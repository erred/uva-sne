package com.opengarden.firechat.matrixsdk.data;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.data.EventTimeline.Direction;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.RoomsRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.TokensChunkResponse;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.Collection;
import java.util.HashMap;

public class DataRetriever {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "DataRetriever";
    private RoomsRestClient mCustomRestClient;
    /* access modifiers changed from: private */
    public final HashMap<String, String> mPendingBackwardRequestTokenByRoomId = new HashMap<>();
    /* access modifiers changed from: private */
    public final HashMap<String, String> mPendingFordwardRequestTokenByRoomId = new HashMap<>();
    /* access modifiers changed from: private */
    public final HashMap<String, String> mPendingRemoteRequestTokenByRoomId = new HashMap<>();
    private RoomsRestClient mRestClient;

    public RoomsRestClient getRoomsRestClient() {
        return this.mRestClient;
    }

    public void setRoomsRestClient(RoomsRestClient roomsRestClient) {
        this.mRestClient = roomsRestClient;
    }

    public void setRooomCustomRestClient(RoomsRestClient roomsRestClient) {
        this.mCustomRestClient = roomsRestClient;
    }

    public Collection<Event> getCachedRoomMessages(IMXStore iMXStore, String str) {
        return iMXStore.getRoomMessages(str);
    }

    public void cancelHistoryRequest(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## cancelHistoryRequest() : roomId ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        clearPendingToken(this.mPendingFordwardRequestTokenByRoomId, str);
        clearPendingToken(this.mPendingBackwardRequestTokenByRoomId, str);
    }

    public void cancelRemoteHistoryRequest(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## cancelRemoteHistoryRequest() : roomId ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        clearPendingToken(this.mPendingRemoteRequestTokenByRoomId, str);
    }

    public void backPaginate(IMXStore iMXStore, String str, String str2, int i, ApiCallback<TokensChunkResponse<Event>> apiCallback) {
        String str3 = str;
        String str4 = str2;
        if (TextUtils.equals(str4, Event.PAGINATE_BACK_TOKEN_END)) {
            final Handler handler = new Handler(Looper.getMainLooper());
            final ApiCallback<TokensChunkResponse<Event>> apiCallback2 = apiCallback;
            handler.post(new Runnable() {
                public void run() {
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            apiCallback2.onSuccess(new TokensChunkResponse());
                        }
                    }, 0);
                }
            });
            return;
        }
        final ApiCallback<TokensChunkResponse<Event>> apiCallback3 = apiCallback;
        String str5 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## backPaginate() : starts for roomId ");
        sb.append(str3);
        Log.m209d(str5, sb.toString());
        TokensChunkResponse earlierMessages = iMXStore.getEarlierMessages(str, str2, i);
        putPendingToken(this.mPendingBackwardRequestTokenByRoomId, str3, str4);
        if (earlierMessages != null) {
            final Handler handler2 = new Handler(Looper.getMainLooper());
            String str6 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## backPaginate() : some data has been retrieved into the local storage (");
            sb2.append(earlierMessages.chunk.size());
            sb2.append(" events)");
            Log.m209d(str6, sb2.toString());
            final String str7 = str3;
            final String str8 = str4;
            final ApiCallback<TokensChunkResponse<Event>> apiCallback4 = apiCallback3;
            final TokensChunkResponse tokensChunkResponse = earlierMessages;
            C25412 r0 = new Runnable() {
                public void run() {
                    handler2.postDelayed(new Runnable() {
                        public void run() {
                            String access$100 = DataRetriever.this.getPendingToken(DataRetriever.this.mPendingBackwardRequestTokenByRoomId, str7);
                            String access$200 = DataRetriever.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## backPaginate() : local store roomId ");
                            sb.append(str7);
                            sb.append(" token ");
                            sb.append(str8);
                            sb.append(" vs ");
                            sb.append(access$100);
                            Log.m209d(access$200, sb.toString());
                            if (TextUtils.equals(access$100, str8)) {
                                DataRetriever.this.clearPendingToken(DataRetriever.this.mPendingBackwardRequestTokenByRoomId, str7);
                                apiCallback4.onSuccess(tokensChunkResponse);
                            }
                        }
                    }, 0);
                }
            };
            new Thread(r0).start();
        } else {
            Log.m209d(LOG_TAG, "## backPaginate() : trigger a remote request");
            RoomsRestClient roomsRestClient = this.mRestClient;
            Direction direction = Direction.BACKWARDS;
            final String str9 = str3;
            final String str10 = str4;
            final IMXStore iMXStore2 = iMXStore;
            C25433 r02 = new SimpleApiCallback<TokensChunkResponse<Event>>(apiCallback3) {
                public void onSuccess(TokensChunkResponse<Event> tokensChunkResponse) {
                    String access$100 = DataRetriever.this.getPendingToken(DataRetriever.this.mPendingBackwardRequestTokenByRoomId, str9);
                    String access$200 = DataRetriever.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## backPaginate() succeeds : roomId ");
                    sb.append(str9);
                    sb.append(" token ");
                    sb.append(str10);
                    sb.append(" vs ");
                    sb.append(access$100);
                    Log.m209d(access$200, sb.toString());
                    if (TextUtils.equals(access$100, str10)) {
                        DataRetriever.this.clearPendingToken(DataRetriever.this.mPendingBackwardRequestTokenByRoomId, str9);
                        Event oldestEvent = iMXStore2.getOldestEvent(str9);
                        if (tokensChunkResponse.chunk.size() != 0) {
                            ((Event) tokensChunkResponse.chunk.get(0)).mToken = tokensChunkResponse.start;
                            if (tokensChunkResponse.end == null) {
                                tokensChunkResponse.end = Event.PAGINATE_BACK_TOKEN_END;
                            }
                            ((Event) tokensChunkResponse.chunk.get(tokensChunkResponse.chunk.size() - 1)).mToken = tokensChunkResponse.end;
                            Event event = (Event) tokensChunkResponse.chunk.get(0);
                            if (!(oldestEvent == null || event == null || !TextUtils.equals(oldestEvent.eventId, event.eventId))) {
                                tokensChunkResponse.chunk.remove(0);
                            }
                            iMXStore2.storeRoomEvents(str9, tokensChunkResponse, Direction.BACKWARDS);
                        }
                        String access$2002 = DataRetriever.LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("## backPaginate() succeed : roomId ");
                        sb2.append(str9);
                        sb2.append(" token ");
                        sb2.append(str10);
                        sb2.append(" got ");
                        sb2.append(tokensChunkResponse.chunk.size());
                        Log.m209d(access$2002, sb2.toString());
                        apiCallback3.onSuccess(tokensChunkResponse);
                    }
                }

                private void logErrorMessage(String str, String str2) {
                    String access$200 = DataRetriever.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## backPaginate() failed : roomId ");
                    sb.append(str9);
                    sb.append(" token ");
                    sb.append(str10);
                    sb.append(" expected ");
                    sb.append(str);
                    sb.append(" with ");
                    sb.append(str2);
                    Log.m211e(access$200, sb.toString());
                }

                public void onNetworkError(Exception exc) {
                    String access$100 = DataRetriever.this.getPendingToken(DataRetriever.this.mPendingBackwardRequestTokenByRoomId, str9);
                    logErrorMessage(access$100, exc.getMessage());
                    if (TextUtils.equals(str10, access$100)) {
                        DataRetriever.this.clearPendingToken(DataRetriever.this.mPendingBackwardRequestTokenByRoomId, str9);
                        apiCallback3.onNetworkError(exc);
                    }
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$100 = DataRetriever.this.getPendingToken(DataRetriever.this.mPendingBackwardRequestTokenByRoomId, str9);
                    logErrorMessage(access$100, matrixError.getMessage());
                    if (TextUtils.equals(str10, access$100)) {
                        DataRetriever.this.clearPendingToken(DataRetriever.this.mPendingBackwardRequestTokenByRoomId, str9);
                        apiCallback3.onMatrixError(matrixError);
                    }
                }

                public void onUnexpectedError(Exception exc) {
                    String access$100 = DataRetriever.this.getPendingToken(DataRetriever.this.mPendingBackwardRequestTokenByRoomId, str9);
                    logErrorMessage(access$100, exc.getMessage());
                    if (TextUtils.equals(str10, access$100)) {
                        DataRetriever.this.clearPendingToken(DataRetriever.this.mPendingBackwardRequestTokenByRoomId, str9);
                        apiCallback3.onUnexpectedError(exc);
                    }
                }
            };
            roomsRestClient.getRoomMessagesFrom(str3, str4, direction, i, r02);
        }
    }

    private void forwardPaginate(IMXStore iMXStore, String str, String str2, ApiCallback<TokensChunkResponse<Event>> apiCallback) {
        String str3 = str;
        String str4 = str2;
        putPendingToken(this.mPendingFordwardRequestTokenByRoomId, str3, str4);
        RoomsRestClient roomsRestClient = this.mRestClient;
        Direction direction = Direction.FORWARDS;
        final String str5 = str3;
        final String str6 = str4;
        final IMXStore iMXStore2 = iMXStore;
        final ApiCallback<TokensChunkResponse<Event>> apiCallback2 = apiCallback;
        C25444 r0 = new SimpleApiCallback<TokensChunkResponse<Event>>(apiCallback) {
            public void onSuccess(TokensChunkResponse<Event> tokensChunkResponse) {
                if (TextUtils.equals(DataRetriever.this.getPendingToken(DataRetriever.this.mPendingFordwardRequestTokenByRoomId, str5), str6)) {
                    DataRetriever.this.clearPendingToken(DataRetriever.this.mPendingFordwardRequestTokenByRoomId, str5);
                    iMXStore2.storeRoomEvents(str5, tokensChunkResponse, Direction.FORWARDS);
                    apiCallback2.onSuccess(tokensChunkResponse);
                }
            }
        };
        roomsRestClient.getRoomMessagesFrom(str3, str4, direction, 30, r0);
    }

    public void paginate(IMXStore iMXStore, String str, String str2, Direction direction, ApiCallback<TokensChunkResponse<Event>> apiCallback) {
        if (direction == Direction.BACKWARDS) {
            backPaginate(iMXStore, str, str2, 30, apiCallback);
        } else {
            forwardPaginate(iMXStore, str, str2, apiCallback);
        }
    }

    public void requestServerRoomHistory(String str, String str2, int i, ApiCallback<TokensChunkResponse<Event>> apiCallback) {
        putPendingToken(this.mPendingRemoteRequestTokenByRoomId, str, str2);
        RoomsRestClient roomsRestClient = this.mRestClient;
        Direction direction = Direction.BACKWARDS;
        final String str3 = str;
        final String str4 = str2;
        final ApiCallback<TokensChunkResponse<Event>> apiCallback2 = apiCallback;
        C25455 r5 = new SimpleApiCallback<TokensChunkResponse<Event>>(apiCallback) {
            public void onSuccess(TokensChunkResponse<Event> tokensChunkResponse) {
                if (TextUtils.equals(DataRetriever.this.getPendingToken(DataRetriever.this.mPendingRemoteRequestTokenByRoomId, str3), str4)) {
                    if (tokensChunkResponse.chunk.size() != 0) {
                        ((Event) tokensChunkResponse.chunk.get(0)).mToken = tokensChunkResponse.start;
                        ((Event) tokensChunkResponse.chunk.get(tokensChunkResponse.chunk.size() - 1)).mToken = tokensChunkResponse.end;
                    }
                    DataRetriever.this.clearPendingToken(DataRetriever.this.mPendingRemoteRequestTokenByRoomId, str3);
                    apiCallback2.onSuccess(tokensChunkResponse);
                }
            }
        };
        roomsRestClient.getRoomMessagesFrom(str, str2, direction, i, r5);
    }

    /* access modifiers changed from: private */
    public void clearPendingToken(HashMap<String, String> hashMap, String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## clearPendingToken() : roomId ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        if (str != null) {
            synchronized (hashMap) {
                hashMap.remove(str);
            }
        }
    }

    /* access modifiers changed from: private */
    public String getPendingToken(HashMap<String, String> hashMap, String str) {
        String str2 = "Not a valid token";
        synchronized (hashMap) {
            if (hashMap.containsKey(str)) {
                str2 = (String) hashMap.get(str);
                if (TextUtils.isEmpty(str2)) {
                    str2 = null;
                }
            }
        }
        String str3 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## getPendingToken() : roomId ");
        sb.append(str);
        sb.append(" token ");
        sb.append(str2);
        Log.m209d(str3, sb.toString());
        return str2;
    }

    private void putPendingToken(HashMap<String, String> hashMap, String str, String str2) {
        String str3 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## putPendingToken() : roomId ");
        sb.append(str);
        sb.append(" token ");
        sb.append(str2);
        Log.m209d(str3, sb.toString());
        synchronized (hashMap) {
            if (str2 == null) {
                try {
                    hashMap.put(str, "");
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                hashMap.put(str, str2);
            }
        }
    }
}
