package com.opengarden.firechat.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.p000v4.view.ViewPager;
import android.support.p000v4.view.ViewPager.OnPageChangeListener;
import android.support.p000v4.view.ViewPager.PageTransformer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.gson.JsonElement;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.adapters.VectorMediasViewerAdapter;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.listeners.MXMediaDownloadListener;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.p005db.VectorContentProvider;
import com.opengarden.firechat.util.SlidableMediaInfo;
import com.opengarden.firechat.util.ThemeUtils;
import java.io.File;
import java.util.List;

public class VectorMediasViewerActivity extends MXCActionBarActivity {
    public static final String EXTRA_MATRIX_ID = "ImageSliderActivity.EXTRA_MATRIX_ID";
    public static final String KEY_INFO_LIST = "ImageSliderActivity.KEY_INFO_LIST";
    public static final String KEY_INFO_LIST_INDEX = "ImageSliderActivity.KEY_INFO_LIST_INDEX";
    public static final String KEY_THUMBNAIL_HEIGHT = "ImageSliderActivity.KEY_THUMBNAIL_HEIGHT";
    public static final String KEY_THUMBNAIL_WIDTH = "ImageSliderActivity.KEY_THUMBNAIL_WIDTH";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorMediasViewerActivity";
    private VectorMediasViewerAdapter mAdapter;
    /* access modifiers changed from: private */
    public List<SlidableMediaInfo> mMediasList;
    private MXSession mSession;
    /* access modifiers changed from: private */
    public MenuItem mShareMenuItem;
    private ViewPager mViewPager;

    public class DepthPageTransformer implements PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public DepthPageTransformer() {
        }

        public void transformPage(View view, float f) {
            int width = view.getWidth();
            if (f < -1.0f) {
                view.setAlpha(0.0f);
            } else if (f <= 0.0f) {
                view.setAlpha(1.0f);
                view.setTranslationX(0.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            } else if (f <= 1.0f) {
                view.setAlpha(1.0f - f);
                view.setTranslationX(((float) width) * (-f));
                float abs = ((1.0f - Math.abs(f)) * 0.25f) + MIN_SCALE;
                view.setScaleX(abs);
                view.setScaleY(abs);
            } else {
                view.setAlpha(0.0f);
            }
        }
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_medias_viewer;
    }

    public void initUiAndData() {
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m209d(LOG_TAG, "onCreate : restart the application");
            CommonActivityUtils.restartApp(this);
        } else if (CommonActivityUtils.isGoingToSplash(this)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
        } else {
            String str = null;
            Intent intent = getIntent();
            if (intent.hasExtra(EXTRA_MATRIX_ID)) {
                str = intent.getStringExtra(EXTRA_MATRIX_ID);
            }
            this.mSession = Matrix.getInstance(getApplicationContext()).getSession(str);
            if (this.mSession == null || !this.mSession.isAlive()) {
                finish();
                Log.m209d(LOG_TAG, "onCreate : invalid session");
                return;
            }
            this.mMediasList = (List) intent.getSerializableExtra(KEY_INFO_LIST);
            if (this.mMediasList == null || this.mMediasList.size() == 0) {
                finish();
                return;
            }
            setContentView((int) C1299R.layout.activity_vector_medias_viewer);
            this.mViewPager = (ViewPager) findViewById(C1299R.C1301id.view_pager);
            int min = Math.min(intent.getIntExtra(KEY_INFO_LIST_INDEX, 0), this.mMediasList.size() - 1);
            VectorMediasViewerAdapter vectorMediasViewerAdapter = new VectorMediasViewerAdapter(this, this.mSession, this.mSession.getMediasCache(), this.mMediasList, intent.getIntExtra(KEY_THUMBNAIL_WIDTH, 0), intent.getIntExtra(KEY_THUMBNAIL_HEIGHT, 0));
            this.mAdapter = vectorMediasViewerAdapter;
            this.mViewPager.setAdapter(this.mAdapter);
            this.mViewPager.setPageTransformer(true, new DepthPageTransformer());
            this.mAdapter.autoPlayItemAt(min);
            this.mViewPager.setCurrentItem(min);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle((CharSequence) ((SlidableMediaInfo) this.mMediasList.get(min)).mFileName);
            }
            this.mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
                public void onPageScrollStateChanged(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                }

                public void onPageSelected(int i) {
                    if (VectorMediasViewerActivity.this.getSupportActionBar() != null) {
                        VectorMediasViewerActivity.this.getSupportActionBar().setTitle((CharSequence) ((SlidableMediaInfo) VectorMediasViewerActivity.this.mMediasList.get(i)).mFileName);
                    }
                    if (VectorMediasViewerActivity.this.mShareMenuItem != null) {
                        VectorMediasViewerActivity.this.mShareMenuItem.setVisible(((SlidableMediaInfo) VectorMediasViewerActivity.this.mMediasList.get(i)).mEncryptedFileInfo == null);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mAdapter.stopPlayingVideo();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean z = false;
        if (CommonActivityUtils.shouldRestartApp(this)) {
            return false;
        }
        getMenuInflater().inflate(C1299R.C1302menu.vector_medias_viewer, menu);
        CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(this, C1299R.attr.icon_tint_on_dark_action_bar_color));
        this.mShareMenuItem = menu.findItem(C1299R.C1301id.ic_action_share);
        if (this.mShareMenuItem != null) {
            MenuItem menuItem = this.mShareMenuItem;
            if (((SlidableMediaInfo) this.mMediasList.get(this.mViewPager.getCurrentItem())).mEncryptedFileInfo == null) {
                z = true;
            }
            menuItem.setVisible(z);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void onAction(final int i, final int i2) {
        MXMediasCache mediasCache = Matrix.getInstance(this).getMediasCache();
        final SlidableMediaInfo slidableMediaInfo = (SlidableMediaInfo) this.mMediasList.get(i);
        if (mediasCache.isMediaCached(slidableMediaInfo.mMediaUrl, slidableMediaInfo.mMimeType)) {
            mediasCache.createTmpMediaFile(slidableMediaInfo.mMediaUrl, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo, new SimpleApiCallback<File>() {
                public void onSuccess(File file) {
                    Parcelable parcelable;
                    if (file != null) {
                        if (i2 == C1299R.C1301id.ic_action_download) {
                            CommonActivityUtils.saveMediaIntoDownloads(VectorMediasViewerActivity.this, file, slidableMediaInfo.mFileName, slidableMediaInfo.mMimeType, new SimpleApiCallback<String>() {
                                public void onSuccess(String str) {
                                    Toast.makeText(VectorApp.getInstance(), VectorMediasViewerActivity.this.getText(C1299R.string.media_slider_saved), 1).show();
                                }
                            });
                        } else {
                            if (slidableMediaInfo.mFileName != null) {
                                File file2 = new File(file.getParent(), slidableMediaInfo.mFileName);
                                if (file2.exists()) {
                                    file2.delete();
                                }
                                file.renameTo(file2);
                                file = file2;
                            }
                            try {
                                parcelable = VectorContentProvider.absolutePathToUri(VectorMediasViewerActivity.this, file.getAbsolutePath());
                            } catch (Exception e) {
                                String access$200 = VectorMediasViewerActivity.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("onMediaAction onAction.absolutePathToUri: ");
                                sb.append(e.getMessage());
                                Log.m211e(access$200, sb.toString());
                                parcelable = null;
                            }
                            if (parcelable != null) {
                                try {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.SEND");
                                    intent.setType(slidableMediaInfo.mMimeType);
                                    intent.putExtra("android.intent.extra.STREAM", parcelable);
                                    VectorMediasViewerActivity.this.startActivity(intent);
                                } catch (Exception e2) {
                                    String access$2002 = VectorMediasViewerActivity.LOG_TAG;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("## onAction : cannot display the media ");
                                    sb2.append(parcelable);
                                    sb2.append(" mimeType ");
                                    sb2.append(slidableMediaInfo.mMimeType);
                                    Log.m211e(access$2002, sb2.toString());
                                    CommonActivityUtils.displayToast(VectorMediasViewerActivity.this, e2.getLocalizedMessage());
                                }
                            }
                        }
                    }
                }
            });
            return;
        }
        final String downloadMedia = mediasCache.downloadMedia(this, this.mSession.getHomeServerConfig(), slidableMediaInfo.mMediaUrl, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo);
        if (downloadMedia != null) {
            mediasCache.addDownloadListener(downloadMedia, new MXMediaDownloadListener() {
                public void onDownloadError(String str, JsonElement jsonElement) {
                    MatrixError matrixError = JsonUtils.toMatrixError(jsonElement);
                    if (matrixError != null && matrixError.isSupportedErrorCode()) {
                        Toast.makeText(VectorMediasViewerActivity.this, matrixError.getLocalizedMessage(), 1).show();
                    }
                }

                public void onDownloadComplete(String str) {
                    if (str.equals(downloadMedia)) {
                        VectorMediasViewerActivity.this.onAction(i, i2);
                    }
                }
            });
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        int itemId2 = menuItem.getItemId();
        if (itemId2 == 16908332) {
            finish();
            return true;
        } else if (itemId2 != C1299R.C1301id.ic_action_download && itemId2 != C1299R.C1301id.ic_action_share) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            onAction(this.mViewPager.getCurrentItem(), itemId);
            return true;
        }
    }
}
