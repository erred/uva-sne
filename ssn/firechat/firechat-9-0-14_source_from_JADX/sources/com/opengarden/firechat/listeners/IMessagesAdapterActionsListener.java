package com.opengarden.firechat.listeners;

import android.net.Uri;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import java.util.List;

public interface IMessagesAdapterActionsListener {
    void onAvatarClick(String str);

    boolean onAvatarLongClick(String str);

    void onContentClick(int i);

    boolean onContentLongClick(int i);

    void onE2eIconClick(Event event, MXDeviceInfo mXDeviceInfo);

    void onEventAction(Event event, String str, int i);

    void onGroupFlairClick(String str, List<String> list);

    void onGroupIdClick(String str);

    void onInvalidIndexes();

    void onMatrixUserIdClick(String str);

    void onMediaDownloaded(int i);

    void onMessageIdClick(String str);

    void onMoreReadReceiptClick(String str);

    void onRoomAliasClick(String str);

    void onRoomIdClick(String str);

    void onRowClick(int i);

    boolean onRowLongClick(int i);

    void onSenderNameClick(String str, String str2);

    void onURLClick(Uri uri);

    boolean shouldHighlightEvent(Event event);
}
