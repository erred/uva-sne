package com.opengarden.firechat.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.p000v4.content.res.ResourcesCompat;
import android.text.Html.ImageGetter;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.util.ContentManager;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VectorImageGetter implements ImageGetter {
    private static Drawable mPlaceHolder;
    /* access modifiers changed from: private */
    public final String LOG_TAG = VectorImageGetter.class.getSimpleName();
    /* access modifiers changed from: private */
    public Map<String, Drawable> mBitmapCache = new HashMap();
    /* access modifiers changed from: private */
    public OnImageDownloadListener mListener;
    /* access modifiers changed from: private */
    public Set<String> mPendingDownloads = new HashSet();
    /* access modifiers changed from: private */
    public MXSession mSession;

    private class ImageDownloaderTask extends AsyncTask<Object, Void, Bitmap> {
        private String mSource;

        private ImageDownloaderTask() {
        }

        /* access modifiers changed from: protected */
        public Bitmap doInBackground(Object... objArr) {
            this.mSource = objArr[0];
            String downloadableUrl = VectorImageGetter.this.mSession.getContentManager().getDownloadableUrl(this.mSource);
            if (downloadableUrl != null) {
                String access$200 = VectorImageGetter.this.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## doInBackground() : ");
                sb.append(this.mSource);
                Log.m209d(access$200, sb.toString());
                try {
                    return BitmapFactory.decodeStream(new URL(downloadableUrl).openConnection().getInputStream());
                } catch (Throwable th) {
                    String access$2002 = VectorImageGetter.this.LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## ImageDownloader() failed ");
                    sb2.append(th.getMessage());
                    Log.m211e(access$2002, sb2.toString());
                }
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Bitmap bitmap) {
            String access$200 = VectorImageGetter.this.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## doInBackground() : bitmap ");
            sb.append(bitmap);
            Log.m209d(access$200, sb.toString());
            VectorImageGetter.this.mPendingDownloads.remove(this.mSource);
            if (bitmap != null) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(VectorApp.getInstance().getResources(), bitmap);
                bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());
                VectorImageGetter.this.mBitmapCache.put(this.mSource, bitmapDrawable);
                try {
                    if (VectorImageGetter.this.mListener != null) {
                        VectorImageGetter.this.mListener.onImageDownloaded(this.mSource);
                    }
                } catch (Throwable th) {
                    String access$2002 = VectorImageGetter.this.LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## ImageDownloader() failed ");
                    sb2.append(th.getMessage());
                    Log.m211e(access$2002, sb2.toString());
                }
            }
        }
    }

    public interface OnImageDownloadListener {
        void onImageDownloaded(String str);
    }

    public VectorImageGetter(MXSession mXSession) {
        this.mSession = mXSession;
    }

    public void setListener(OnImageDownloadListener onImageDownloadListener) {
        this.mListener = onImageDownloadListener;
    }

    public Drawable getDrawable(String str) {
        if (str != null && str.toLowerCase().startsWith(ContentManager.MATRIX_CONTENT_URI_SCHEME)) {
            if (this.mBitmapCache.containsKey(str)) {
                String str2 = this.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getDrawable() : ");
                sb.append(str);
                sb.append(" already cached");
                Log.m209d(str2, sb.toString());
                return (Drawable) this.mBitmapCache.get(str);
            } else if (!this.mPendingDownloads.contains(str)) {
                String str3 = this.LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## getDrawable() : starts a task to download ");
                sb2.append(str);
                Log.m209d(str3, sb2.toString());
                try {
                    new ImageDownloaderTask().execute(new Object[]{str});
                    this.mPendingDownloads.add(str);
                } catch (Throwable th) {
                    String str4 = this.LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("## getDrawable() failed ");
                    sb3.append(th.getMessage());
                    Log.m211e(str4, sb3.toString());
                }
            } else {
                String str5 = this.LOG_TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("## getDrawable() : ");
                sb4.append(str);
                sb4.append(" is downloading");
                Log.m209d(str5, sb4.toString());
            }
        }
        if (mPlaceHolder == null) {
            mPlaceHolder = ResourcesCompat.getDrawable(VectorApp.getInstance().getResources(), C1299R.C1300drawable.filetype_image, null);
            mPlaceHolder.setBounds(0, 0, mPlaceHolder.getIntrinsicWidth(), mPlaceHolder.getIntrinsicHeight());
        }
        return mPlaceHolder;
    }
}
