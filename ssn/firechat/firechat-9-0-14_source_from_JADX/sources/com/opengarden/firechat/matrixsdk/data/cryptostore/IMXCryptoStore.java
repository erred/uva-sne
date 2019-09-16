package com.opengarden.firechat.matrixsdk.data.cryptostore;

import android.content.Context;
import com.opengarden.firechat.matrixsdk.crypto.IncomingRoomKeyRequest;
import com.opengarden.firechat.matrixsdk.crypto.OutgoingRoomKeyRequest;
import com.opengarden.firechat.matrixsdk.crypto.OutgoingRoomKeyRequest.RequestState;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matrix.olm.OlmAccount;
import org.matrix.olm.OlmSession;

public interface IMXCryptoStore {
    void close();

    void deleteIncomingRoomKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest);

    void deleteOutgoingRoomKeyRequest(String str);

    void deleteStore();

    OlmAccount getAccount();

    String getDeviceId();

    Map<String, OlmSession> getDeviceSessions(String str);

    int getDeviceTrackingStatus(String str, int i);

    Map<String, Integer> getDeviceTrackingStatuses();

    boolean getGlobalBlacklistUnverifiedDevices();

    MXOlmInboundGroupSession2 getInboundGroupSession(String str, String str2);

    List<MXOlmInboundGroupSession2> getInboundGroupSessions();

    IncomingRoomKeyRequest getIncomingRoomKeyRequest(String str, String str2, String str3);

    OutgoingRoomKeyRequest getOrAddOutgoingRoomKeyRequest(OutgoingRoomKeyRequest outgoingRoomKeyRequest);

    OutgoingRoomKeyRequest getOutgoingRoomKeyRequest(Map<String, String> map);

    OutgoingRoomKeyRequest getOutgoingRoomKeyRequestByState(Set<RequestState> set);

    List<IncomingRoomKeyRequest> getPendingIncomingRoomKeyRequests();

    String getRoomAlgorithm(String str);

    List<String> getRoomsListBlacklistUnverifiedDevices();

    MXDeviceInfo getUserDevice(String str, String str2);

    Map<String, MXDeviceInfo> getUserDevices(String str);

    boolean hasData();

    void initWithCredentials(Context context, Credentials credentials);

    boolean isCorrupted();

    void open();

    void removeInboundGroupSession(String str, String str2);

    void saveDeviceTrackingStatuses(Map<String, Integer> map);

    void setGlobalBlacklistUnverifiedDevices(boolean z);

    void setRoomsListBlacklistUnverifiedDevices(List<String> list);

    void storeAccount(OlmAccount olmAccount);

    void storeDeviceId(String str);

    void storeInboundGroupSession(MXOlmInboundGroupSession2 mXOlmInboundGroupSession2);

    void storeIncomingRoomKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest);

    void storeRoomAlgorithm(String str, String str2);

    void storeSession(OlmSession olmSession, String str);

    void storeUserDevice(String str, MXDeviceInfo mXDeviceInfo);

    void storeUserDevices(String str, Map<String, MXDeviceInfo> map);

    void updateOutgoingRoomKeyRequest(OutgoingRoomKeyRequest outgoingRoomKeyRequest);
}
