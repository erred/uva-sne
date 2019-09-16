package com.opengarden.firechat.matrixsdk.data.store;

import android.content.Context;
import android.support.annotation.Nullable;
import com.opengarden.firechat.matrixsdk.data.EventTimeline.Direction;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomAccountData;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.data.metrics.MetricsListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.ReceiptData;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.TokensChunkResponse;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyIdentifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IMXStore {
    void addMXStoreListener(IMXStoreListener iMXStoreListener);

    boolean areReceiptsReady();

    String avatarURL();

    void clear();

    void close();

    void commit();

    void deleteAllRoomMessages(String str, boolean z);

    void deleteEvent(Event event);

    void deleteGroup(String str);

    void deleteRoom(String str);

    void deleteRoomData(String str);

    long diskUsage();

    String displayName();

    boolean doesEventExist(String str, String str2);

    int eventsCountAfter(String str, String str2);

    void flushGroup(Group group);

    void flushRoomEvents(String str);

    void flushSummaries();

    void flushSummary(RoomSummary roomSummary);

    @Nullable
    String getAntivirusServerPublicKey();

    Context getContext();

    Map<String, List<String>> getDirectChatRoomsDict();

    TokensChunkResponse<Event> getEarlierMessages(String str, String str2, int i);

    Event getEvent(String str, String str2);

    List<ReceiptData> getEventReceipts(String str, String str2, boolean z, boolean z2);

    String getEventStreamToken();

    Group getGroup(String str);

    Collection<Group> getGroups();

    List<String> getIgnoredUserIdsList();

    Event getLatestEvent(String str);

    List<Event> getLatestUnsentEvents(String str);

    Event getOldestEvent(String str);

    long getPreloadTime();

    ReceiptData getReceipt(String str, String str2);

    Room getRoom(String str);

    Collection<Event> getRoomMessages(String str);

    void getRoomStateEvents(String str, ApiCallback<List<Event>> apiCallback);

    Collection<Room> getRooms();

    Set<String> getRoomsWithoutURLPreviews();

    Map<String, Long> getStats();

    Collection<RoomSummary> getSummaries();

    RoomSummary getSummary(String str);

    List<Event> getUndeliverableEvents(String str);

    List<Event> getUnknownDeviceEvents(String str);

    User getUser(String str);

    Map<String, Object> getUserWidgets();

    Collection<User> getUsers();

    boolean isCorrupted();

    boolean isEventRead(String str, String str2, String str3);

    boolean isPermanent();

    boolean isReady();

    boolean isURLPreviewEnabled();

    void open();

    void post(Runnable runnable);

    void removeMXStoreListener(IMXStoreListener iMXStoreListener);

    void setAntivirusServerPublicKey(@Nullable String str);

    boolean setAvatarURL(String str, long j);

    void setCorrupted(String str);

    void setDirectChatRoomsDict(Map<String, List<String>> map);

    boolean setDisplayName(String str, long j);

    void setEventStreamToken(String str);

    void setIgnoredUserIdsList(List<String> list);

    void setMetricsListener(MetricsListener metricsListener);

    void setRoomsWithoutURLPreview(Set<String> set);

    void setThirdPartyIdentifiers(List<ThirdPartyIdentifier> list);

    void setURLPreviewEnabled(boolean z);

    void setUserWidgets(Map<String, Object> map);

    void storeAccountData(String str, RoomAccountData roomAccountData);

    void storeBackToken(String str, String str2);

    void storeGroup(Group group);

    void storeLiveRoomEvent(Event event);

    void storeLiveStateForRoom(String str);

    boolean storeReceipt(ReceiptData receiptData, String str);

    void storeRoom(Room room);

    void storeRoomEvents(String str, TokensChunkResponse<Event> tokensChunkResponse, Direction direction);

    void storeRoomStateEvent(String str, Event event);

    void storeSummary(RoomSummary roomSummary);

    void storeUser(User user);

    List<ThirdPartyIdentifier> thirdPartyIdentifiers();

    List<Event> unreadEvents(String str, List<String> list);

    void updateUserWithRoomMemberEvent(RoomMember roomMember);
}
