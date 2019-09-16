package com.opengarden.firechat.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.p003v7.widget.RecyclerView.Adapter;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RoomDirectoryData;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomDirectoryAdapter extends Adapter<RoomDirectoryViewHolder> {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "RoomDirectoryAdapter";
    /* access modifiers changed from: private */
    public static final Map<String, Bitmap> mAvatarByUrl = new HashMap();
    /* access modifiers changed from: private */
    public static final Map<String, List<WeakReference<ImageView>>> mPendingDownloadByUrl = new HashMap();
    private final List<RoomDirectoryData> mList;
    /* access modifiers changed from: private */
    public final OnSelectRoomDirectoryListener mListener;

    public interface OnSelectRoomDirectoryListener {
        void onSelectRoomDirectory(RoomDirectoryData roomDirectoryData);
    }

    class RoomDirectoryViewHolder extends ViewHolder {
        final ImageView vAvatarView;
        final TextView vDescriptionTextView;
        final View vMainView;
        final TextView vServerTextView;

        private RoomDirectoryViewHolder(View view) {
            super(view);
            this.vMainView = view;
            this.vAvatarView = (ImageView) view.findViewById(C1299R.C1301id.room_directory_avatar);
            this.vServerTextView = (TextView) view.findViewById(C1299R.C1301id.room_directory_display_name);
            this.vDescriptionTextView = (TextView) view.findViewById(C1299R.C1301id.room_directory_description);
        }

        /* access modifiers changed from: private */
        public void populateViews(final RoomDirectoryData roomDirectoryData) {
            CharSequence charSequence;
            this.vServerTextView.setText(roomDirectoryData.getDisplayName());
            Drawable drawable = null;
            int i = 0;
            if (roomDirectoryData.isIncludedAllNetworks()) {
                charSequence = this.vServerTextView.getContext().getString(C1299R.string.directory_server_all_rooms_on_server, new Object[]{roomDirectoryData.getDisplayName()});
            } else if (TextUtils.equals("Matrix", roomDirectoryData.getDisplayName())) {
                charSequence = this.vServerTextView.getContext().getString(C1299R.string.directory_server_native_rooms, new Object[]{roomDirectoryData.getDisplayName()});
            } else {
                charSequence = null;
            }
            this.vDescriptionTextView.setText(charSequence);
            TextView textView = this.vDescriptionTextView;
            if (TextUtils.isEmpty(charSequence)) {
                i = 8;
            }
            textView.setVisibility(i);
            RoomDirectoryAdapter roomDirectoryAdapter = RoomDirectoryAdapter.this;
            ImageView imageView = this.vAvatarView;
            String avatarUrl = roomDirectoryData.getAvatarUrl();
            if (!roomDirectoryData.isIncludedAllNetworks()) {
                drawable = this.vServerTextView.getContext().getResources().getDrawable(C1299R.C1300drawable.firechat);
            }
            roomDirectoryAdapter.setAvatar(imageView, avatarUrl, drawable);
            this.vMainView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    RoomDirectoryAdapter.this.mListener.onSelectRoomDirectory(roomDirectoryData);
                }
            });
        }
    }

    public RoomDirectoryAdapter(List<RoomDirectoryData> list, OnSelectRoomDirectoryListener onSelectRoomDirectoryListener) {
        this.mList = list == null ? new ArrayList() : new ArrayList(list);
        this.mListener = onSelectRoomDirectoryListener;
    }

    public void updateDirectoryServersList(List<RoomDirectoryData> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public RoomDirectoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RoomDirectoryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C1299R.layout.item_room_directory, viewGroup, false));
    }

    public void onBindViewHolder(RoomDirectoryViewHolder roomDirectoryViewHolder, int i) {
        if (i < this.mList.size()) {
            roomDirectoryViewHolder.populateViews((RoomDirectoryData) this.mList.get(i));
        }
    }

    public int getItemCount() {
        return this.mList.size();
    }

    /* access modifiers changed from: private */
    public void setAvatar(ImageView imageView, String str, Drawable drawable) {
        imageView.setImageDrawable(drawable);
        imageView.setTag(null);
        if (str != null) {
            Bitmap bitmap = (Bitmap) mAvatarByUrl.get(str);
            if (bitmap == null) {
                downloadAvatar(imageView, str);
            } else {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void downloadAvatar(final ImageView imageView, final String str) {
        if (imageView != null && str != null) {
            imageView.setTag(str);
            WeakReference weakReference = new WeakReference(imageView);
            if (mPendingDownloadByUrl.containsKey(str)) {
                ((List) mPendingDownloadByUrl.get(str)).add(weakReference);
                return;
            }
            mPendingDownloadByUrl.put(str, new ArrayList(Arrays.asList(new WeakReference[]{weakReference})));
            C17441 r0 = new AsyncTask<Void, Void, Bitmap>() {
                /* access modifiers changed from: protected */
                public Bitmap doInBackground(Void... voidArr) {
                    try {
                        return BitmapFactory.decodeStream((InputStream) new URL(str).getContent());
                    } catch (Exception unused) {
                        String access$400 = RoomDirectoryAdapter.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## downloadAvatar() : cannot load the avatar ");
                        sb.append(str);
                        Log.m211e(access$400, sb.toString());
                        return null;
                    }
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null && !RoomDirectoryAdapter.mAvatarByUrl.containsKey(str)) {
                        RoomDirectoryAdapter.mAvatarByUrl.put(str, bitmap);
                    }
                    if (RoomDirectoryAdapter.mPendingDownloadByUrl.containsKey(str)) {
                        List<WeakReference> list = (List) RoomDirectoryAdapter.mPendingDownloadByUrl.get(str);
                        RoomDirectoryAdapter.mPendingDownloadByUrl.remove(str);
                        for (WeakReference weakReference : list) {
                            ImageView imageView = (ImageView) weakReference.get();
                            if (imageView != null && TextUtils.equals((String) imageView.getTag(), str)) {
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    }
                }
            };
            try {
                r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## downloadAvatar() failed ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
                r0.cancel(true);
            }
        }
    }
}
