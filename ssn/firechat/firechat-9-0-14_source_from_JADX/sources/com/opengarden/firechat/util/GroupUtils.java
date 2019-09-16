package com.opengarden.firechat.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.VectorMemberDetailsActivity;
import com.opengarden.firechat.activity.VectorRoomActivity;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupRoom;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupUser;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GroupUtils {
    private static final String LOG_TAG = "GroupUtils";

    public static List<Group> getFilteredGroups(List<Group> list, CharSequence charSequence) {
        String trim = charSequence != null ? charSequence.toString().trim() : null;
        if (TextUtils.isEmpty(trim)) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        Pattern compile = Pattern.compile(Pattern.quote(trim), 2);
        for (Group group : list) {
            if (compile.matcher(group.getDisplayName()).find()) {
                arrayList.add(group);
            }
        }
        return arrayList;
    }

    public static List<GroupUser> getFilteredGroupUsers(List<GroupUser> list, CharSequence charSequence) {
        String trim = charSequence != null ? charSequence.toString().trim() : null;
        if (TextUtils.isEmpty(trim)) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        Pattern compile = Pattern.compile(Pattern.quote(trim), 2);
        for (GroupUser groupUser : list) {
            if (compile.matcher(groupUser.getDisplayname()).find()) {
                arrayList.add(groupUser);
            }
        }
        return arrayList;
    }

    public static List<GroupRoom> getFilteredGroupRooms(List<GroupRoom> list, CharSequence charSequence) {
        String trim = charSequence != null ? charSequence.toString().trim() : null;
        if (TextUtils.isEmpty(trim)) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        Pattern compile = Pattern.compile(Pattern.quote(trim), 2);
        for (GroupRoom groupRoom : list) {
            if (compile.matcher(groupRoom.getDisplayName()).find()) {
                arrayList.add(groupRoom);
            }
        }
        return arrayList;
    }

    public static void openGroupUserPage(Activity activity, MXSession mXSession, GroupUser groupUser) {
        Intent intent = new Intent(activity, VectorMemberDetailsActivity.class);
        intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, groupUser.userId);
        if (!TextUtils.isEmpty(groupUser.avatarUrl)) {
            intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_AVATAR_URL, groupUser.avatarUrl);
        }
        if (!TextUtils.isEmpty(groupUser.displayname)) {
            intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_DISPLAY_NAME, groupUser.displayname);
        }
        intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", mXSession.getCredentials().userId);
        activity.startActivity(intent);
    }

    public static void openGroupRoom(final Activity activity, MXSession mXSession, final GroupRoom groupRoom, final ApiCallback<Void> apiCallback) {
        Room room = mXSession.getDataHandler().getStore().getRoom(groupRoom.roomId);
        if (room == null || room.getMember(mXSession.getMyUserId()) == null) {
            final RoomPreviewData roomPreviewData = new RoomPreviewData(mXSession, groupRoom.roomId, null, groupRoom.getAlias(), null);
            roomPreviewData.fetchPreviewData(new ApiCallback<Void>() {
                private void onDone() {
                    if (apiCallback != null) {
                        apiCallback.onSuccess(null);
                    }
                    CommonActivityUtils.previewRoom(activity, roomPreviewData);
                }

                public void onSuccess(Void voidR) {
                    onDone();
                }

                private void onError() {
                    roomPreviewData.setRoomState(groupRoom);
                    roomPreviewData.setRoomName(groupRoom.name);
                    onDone();
                }

                public void onNetworkError(Exception exc) {
                    onError();
                }

                public void onMatrixError(MatrixError matrixError) {
                    onError();
                }

                public void onUnexpectedError(Exception exc) {
                    onError();
                }
            });
            return;
        }
        Intent intent = new Intent(activity, VectorRoomActivity.class);
        intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", mXSession.getMyUserId());
        intent.putExtra("EXTRA_ROOM_ID", groupRoom.roomId);
        activity.startActivity(intent);
        if (apiCallback != null) {
            apiCallback.onSuccess(null);
        }
    }
}
