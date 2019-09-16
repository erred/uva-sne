package com.opengarden.firechat.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.p000v4.content.ContextCompat;
import android.support.p003v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorUtils;
import com.opengarden.firechat.view.VectorCircularImageView;
import java.util.ArrayList;
import java.util.List;

public class VectorMemberDetailsAdapter extends BaseExpandableListAdapter {
    /* access modifiers changed from: private */
    public IEnablingActions mActionListener;
    private List<List<AdapterMemberActionItems>> mActionsList = new ArrayList();
    private List<AdapterMemberActionItems> mAdminActionsList = new ArrayList();
    private int mAdminGroupPosition = -1;
    private List<AdapterMemberActionItems> mCallActionsList = new ArrayList();
    private int mCallGroupPosition = -1;
    private final Context mContext;
    private int mDevicesGroupPosition = -1;
    private List<AdapterMemberActionItems> mDevicesList = new ArrayList();
    private int mDirectCallsGroupPosition = -1;
    private List<AdapterMemberActionItems> mDirectCallsList = new ArrayList();
    private final int mHeaderLayoutResourceId;
    private final LayoutInflater mLayoutInflater;
    private final int mRowItemLayoutResourceId;
    private final MXSession mSession;
    private List<AdapterMemberActionItems> mUncategorizedActionsList = new ArrayList();
    private int mUncategorizedGroupPosition = -1;

    public static class AdapterMemberActionItems {
        public final String mActionDescText;
        public final int mActionType;
        public final int mIconResourceId;
        public final Room mRoom;

        public AdapterMemberActionItems(int i, String str, int i2) {
            this.mIconResourceId = i;
            this.mActionDescText = str;
            this.mActionType = i2;
            this.mRoom = null;
        }

        public AdapterMemberActionItems(Room room) {
            this.mIconResourceId = -1;
            this.mActionDescText = null;
            this.mActionType = -1;
            this.mRoom = room;
        }
    }

    public interface IEnablingActions {
        void performItemAction(int i);

        void selectRoom(Room room);
    }

    private static class MemberDetailsViewHolder {
        final TextView mActionDescTextView;
        final ImageView mActionImageView;
        final View mRoomAvatarLayout;
        final VectorCircularImageView mVectorCircularImageView;

        MemberDetailsViewHolder(View view) {
            this.mActionImageView = (ImageView) view.findViewById(C1299R.C1301id.adapter_member_details_icon);
            this.mActionDescTextView = (TextView) view.findViewById(C1299R.C1301id.adapter_member_details_action_text);
            this.mVectorCircularImageView = (VectorCircularImageView) view.findViewById(C1299R.C1301id.room_avatar_image_view);
            this.mRoomAvatarLayout = view.findViewById(C1299R.C1301id.room_avatar_layout);
        }
    }

    public Object getChild(int i, int i2) {
        return null;
    }

    public long getChildId(int i, int i2) {
        return 0;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public VectorMemberDetailsAdapter(Context context, MXSession mXSession, int i, int i2) {
        this.mContext = context;
        this.mSession = mXSession;
        this.mRowItemLayoutResourceId = i;
        this.mHeaderLayoutResourceId = i2;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
    }

    public void setActionListener(IEnablingActions iEnablingActions) {
        this.mActionListener = iEnablingActions;
    }

    public void setUncategorizedActionsList(List<AdapterMemberActionItems> list) {
        this.mUncategorizedActionsList = list;
    }

    public void setAdminActionsList(List<AdapterMemberActionItems> list) {
        this.mAdminActionsList = list;
    }

    public void setCallActionsList(List<AdapterMemberActionItems> list) {
        this.mCallActionsList = list;
    }

    public void setDirectCallsActionsList(List<AdapterMemberActionItems> list) {
        this.mDirectCallsList = list;
    }

    public void setDevicesActionsList(List<AdapterMemberActionItems> list) {
        this.mDevicesList = list;
    }

    public void notifyDataSetChanged() {
        this.mActionsList = new ArrayList();
        this.mUncategorizedGroupPosition = -1;
        this.mAdminGroupPosition = -1;
        this.mCallGroupPosition = -1;
        this.mDirectCallsGroupPosition = -1;
        this.mDevicesGroupPosition = -1;
        int i = 0;
        if (this.mUncategorizedActionsList.size() != 0) {
            this.mActionsList.add(this.mUncategorizedActionsList);
            this.mUncategorizedGroupPosition = 0;
            i = 1;
        }
        if (this.mAdminActionsList.size() != 0) {
            this.mActionsList.add(this.mAdminActionsList);
            this.mAdminGroupPosition = i;
            i++;
        }
        if (this.mCallActionsList.size() != 0) {
            this.mActionsList.add(this.mCallActionsList);
            this.mCallGroupPosition = i;
            i++;
        }
        if (this.mDevicesList.size() != 0) {
            this.mActionsList.add(this.mDevicesList);
            this.mDevicesGroupPosition = i;
            i++;
        }
        if (this.mDirectCallsList.size() != 0) {
            this.mActionsList.add(this.mDirectCallsList);
            this.mDirectCallsGroupPosition = i;
        }
        super.notifyDataSetChanged();
    }

    public int getGroupCount() {
        return this.mActionsList.size();
    }

    private String getGroupTitle(int i) {
        if (i == this.mAdminGroupPosition) {
            return this.mContext.getResources().getString(C1299R.string.room_participants_header_admin_tools);
        }
        if (i == this.mCallGroupPosition) {
            return this.mContext.getResources().getString(C1299R.string.room_participants_header_call);
        }
        if (i == this.mDirectCallsGroupPosition) {
            return this.mContext.getResources().getString(C1299R.string.room_participants_header_direct_chats);
        }
        return i == this.mDevicesGroupPosition ? this.mContext.getResources().getString(C1299R.string.room_participants_header_devices) : "???";
    }

    public Object getGroup(int i) {
        return getGroupTitle(i);
    }

    public long getGroupId(int i) {
        return (long) getGroupTitle(i).hashCode();
    }

    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mHeaderLayoutResourceId, null);
        }
        ((TextView) view.findViewById(C1299R.C1301id.heading)).setText(getGroupTitle(i));
        int i2 = 8;
        view.findViewById(C1299R.C1301id.heading_image).setVisibility(8);
        View findViewById = view.findViewById(C1299R.C1301id.heading_layout);
        if (i != this.mUncategorizedGroupPosition) {
            i2 = 0;
        }
        findViewById.setVisibility(i2);
        return view;
    }

    public int getChildrenCount(int i) {
        if (i < this.mActionsList.size()) {
            return ((List) this.mActionsList.get(i)).size();
        }
        return 0;
    }

    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        MemberDetailsViewHolder memberDetailsViewHolder;
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mRowItemLayoutResourceId, viewGroup, false);
            memberDetailsViewHolder = new MemberDetailsViewHolder(view);
            view.setTag(memberDetailsViewHolder);
        } else {
            memberDetailsViewHolder = (MemberDetailsViewHolder) view.getTag();
        }
        if (i >= this.mActionsList.size() || i2 >= ((List) this.mActionsList.get(i)).size()) {
            return view;
        }
        final AdapterMemberActionItems adapterMemberActionItems = (AdapterMemberActionItems) ((List) this.mActionsList.get(i)).get(i2);
        if (adapterMemberActionItems.mRoom != null) {
            memberDetailsViewHolder.mActionDescTextView.setTextColor(ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.riot_primary_text_color));
            memberDetailsViewHolder.mActionDescTextView.setText(VectorUtils.getRoomDisplayName(this.mContext, this.mSession, adapterMemberActionItems.mRoom));
            memberDetailsViewHolder.mActionImageView.setVisibility(8);
            memberDetailsViewHolder.mRoomAvatarLayout.setVisibility(0);
            VectorUtils.loadRoomAvatar(this.mContext, this.mSession, (ImageView) memberDetailsViewHolder.mVectorCircularImageView, adapterMemberActionItems.mRoom);
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VectorMemberDetailsAdapter.this.mActionListener != null) {
                        VectorMemberDetailsAdapter.this.mActionListener.selectRoom(adapterMemberActionItems.mRoom);
                    }
                }
            });
        } else {
            memberDetailsViewHolder.mActionDescTextView.setText(adapterMemberActionItems.mActionDescText);
            memberDetailsViewHolder.mActionImageView.setVisibility(0);
            memberDetailsViewHolder.mRoomAvatarLayout.setVisibility(8);
            memberDetailsViewHolder.mActionImageView.setImageResource(adapterMemberActionItems.mIconResourceId);
            if (adapterMemberActionItems.mIconResourceId != C1299R.C1300drawable.ic_remove_circle_outline_red) {
                memberDetailsViewHolder.mActionImageView.setImageDrawable(CommonActivityUtils.tintDrawable(this.mContext, memberDetailsViewHolder.mActionImageView.getDrawable(), C1299R.attr.settings_icon_tint_color));
            }
            int color = ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.riot_primary_text_color);
            if (2 == adapterMemberActionItems.mActionType) {
                color = ContextCompat.getColor(this.mContext, C1299R.color.vector_fuchsia_color);
            }
            memberDetailsViewHolder.mActionDescTextView.setTextColor(color);
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VectorMemberDetailsAdapter.this.mActionListener == null) {
                        return;
                    }
                    if (2 == adapterMemberActionItems.mActionType || 3 == adapterMemberActionItems.mActionType) {
                        Builder builder = new Builder(view.getContext());
                        builder.setTitle((int) C1299R.string.dialog_title_confirmation);
                        if (2 == adapterMemberActionItems.mActionType) {
                            builder.setMessage((CharSequence) view.getContext().getString(C1299R.string.room_participants_kick_prompt_msg));
                        } else {
                            builder.setMessage((CharSequence) view.getContext().getString(C1299R.string.room_participants_ban_prompt_msg));
                        }
                        builder.setPositiveButton((int) C1299R.string.f115ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                VectorMemberDetailsAdapter.this.mActionListener.performItemAction(adapterMemberActionItems.mActionType);
                            }
                        });
                        builder.setNegativeButton((int) C1299R.string.cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        builder.show();
                        return;
                    }
                    VectorMemberDetailsAdapter.this.mActionListener.performItemAction(adapterMemberActionItems.mActionType);
                }
            });
        }
        return view;
    }
}
