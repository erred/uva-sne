package com.opengarden.firechat.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.VectorMemberDetailsActivity;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.model.ReceiptData;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.util.VectorUtils;

public class VectorReadReceiptsAdapter extends ArrayAdapter<ReceiptData> {
    /* access modifiers changed from: private */
    public final Context mContext;
    private final LayoutInflater mLayoutInflater = LayoutInflater.from(this.mContext);
    private final int mLayoutResourceId;
    /* access modifiers changed from: private */
    public final Room mRoom;
    /* access modifiers changed from: private */
    public final MXSession mSession;

    public VectorReadReceiptsAdapter(Context context, int i, MXSession mXSession, Room room, MXMediasCache mXMediasCache) {
        super(context, i);
        this.mContext = context;
        this.mLayoutResourceId = i;
        this.mSession = mXSession;
        this.mRoom = room;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mLayoutResourceId, viewGroup, false);
        }
        ReceiptData receiptData = (ReceiptData) getItem(i);
        final TextView textView = (TextView) view.findViewById(C1299R.C1301id.accountAdapter_name);
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.avatar_img_vector);
        final RoomMember member = this.mRoom.getMember(receiptData.userId);
        if (member == null) {
            textView.setText(receiptData.userId);
            VectorUtils.loadUserAvatar(this.mContext, this.mSession, imageView, null, receiptData.userId, receiptData.userId);
        } else {
            textView.setText(member.getName());
            VectorUtils.loadRoomMemberAvatar(this.mContext, this.mSession, imageView, member);
        }
        TextView textView2 = (TextView) view.findViewById(C1299R.C1301id.read_receipt_ts);
        final String tsToString = AdapterUtils.tsToString(this.mContext, receiptData.originServerTs, false);
        StringBuilder sb = new StringBuilder();
        sb.append(this.mContext.getString(C1299R.string.read_receipt));
        sb.append(" : ");
        sb.append(tsToString);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sb.toString());
        spannableStringBuilder.setSpan(new StyleSpan(1), 0, this.mContext.getString(C1299R.string.read_receipt).length(), 33);
        textView2.setText(spannableStringBuilder);
        textView.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                VectorUtils.copyToClipboard(VectorReadReceiptsAdapter.this.mContext, textView.getText());
                return true;
            }
        });
        textView2.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                ((ClipboardManager) VectorReadReceiptsAdapter.this.mContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("", tsToString));
                Toast.makeText(VectorReadReceiptsAdapter.this.mContext, VectorReadReceiptsAdapter.this.mContext.getResources().getString(C1299R.string.copied_to_clipboard), 0).show();
                return true;
            }
        });
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (member != null) {
                    Intent intent = new Intent(VectorReadReceiptsAdapter.this.mContext, VectorMemberDetailsActivity.class);
                    intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, member.getUserId());
                    intent.putExtra("EXTRA_ROOM_ID", VectorReadReceiptsAdapter.this.mRoom.getRoomId());
                    intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorReadReceiptsAdapter.this.mSession.getCredentials().userId);
                    VectorReadReceiptsAdapter.this.mContext.startActivity(intent);
                }
            }
        });
        return view;
    }
}
