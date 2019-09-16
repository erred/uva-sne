package com.opengarden.firechat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.support.annotation.NonNull;
import android.support.p000v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.VideoView;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.react.views.scroll.ReactScrollViewHelper;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.util.ImageUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import com.opengarden.firechat.view.RecentMediaLayout;
import com.opengarden.firechat.view.VideoRecordView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.apache.commons.lang3.StringUtils;

public class VectorMediasPickerActivity extends MXCActionBarActivity implements SurfaceTextureListener {
    private static final int AVATAR_COMPRESSION_LEVEL = 50;
    public static final String EXTRA_AVATAR_MODE = "EXTRA_AVATAR_MODE";
    public static final String EXTRA_VIDEO_RECORDING_MODE = "EXTRA_VIDEO_RECORDING_MODE";
    private static final int GALLERY_COLUMN_COUNT = 4;
    private static final int GALLERY_RAW_COUNT = 3;
    private static final int GALLERY_TABLE_ITEM_SIZE = 12;
    private static final int IMAGE_ORIGIN_CAMERA = 1;
    private static final int IMAGE_ORIGIN_GALLERY = 2;
    private static final int JPEG_QUALITY_MAX = 100;
    private static final String KEY_EXTRA_CAMERA_SIDE = "TAKEN_IMAGE_CAMERA_SIDE";
    private static final String KEY_EXTRA_IS_TAKEN_IMAGE_DISPLAYED = "IS_TAKEN_IMAGE_DISPLAYED";
    private static final String KEY_EXTRA_TAKEN_IMAGE_CAMERA_URL = "TAKEN_IMAGE_CAMERA_URL";
    private static final String KEY_EXTRA_TAKEN_IMAGE_GALLERY_URI = "TAKEN_IMAGE_GALLERY_URI";
    private static final String KEY_EXTRA_TAKEN_IMAGE_ORIGIN = "TAKEN_IMAGE_ORIGIN";
    private static final String KEY_EXTRA_TAKEN_VIDEO_URI = "KEY_EXTRA_TAKEN_VIDEO_URI";
    private static final String KEY_IS_AVATAR_MODE = "KEY_IS_AVATAR_MODE";
    private static final String KEY_PREFERENCE_CAMERA_IMAGE_NAME = "KEY_PREFERENCE_CAMERA_IMAGE_NAME";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorMediasPickerActivity";
    private static final String MIME_TYPE_IMAGE_GIF = "image/gif";
    private static final int REQUEST_MEDIAS = 54;
    private static final double SURFACE_VIEW_HEIGHT_RATIO = 0.95d;
    private static final boolean UI_SHOW_CAMERA_PREVIEW = false;
    private static final boolean UI_SHOW_TAKEN_IMAGE = true;
    private int mActivityOrientation;
    private CamcorderProfile mCamcorderProfile = null;
    /* access modifiers changed from: private */
    public Camera mCamera;
    private int mCameraId;
    private int mCameraOrientation = 0;
    private RelativeLayout mCameraPreviewLayout;
    private int mCameraPreviewLayoutHeight;
    /* access modifiers changed from: private */
    public ImageView mCameraTextureMaskView;
    /* access modifiers changed from: private */
    public TextureView mCameraTextureView;
    private Handler mFileHandler;
    private int mGalleryImageCount;
    private TableLayout mGalleryTableLayout;
    private HandlerThread mHandlerThread;
    private ImageView mImagePreviewAvatarModeMaskView;
    private ImageView mImagePreviewImageView;
    private View mImagePreviewLayout;
    /* access modifiers changed from: private */
    public boolean mIsAvatarMode;
    /* access modifiers changed from: private */
    public boolean mIsRecording = false;
    private boolean mIsTakenImageDisplayed;
    private boolean mIsVideoMode = false;
    /* access modifiers changed from: private */
    public boolean mIsVideoRecordingSupported;
    private MediaRecorder mMediaRecorder;
    /* access modifiers changed from: private */
    public final ArrayList<MediaStoreMedia> mMediaStoreMediasList = new ArrayList<>();
    private RelativeLayout mPreviewAndGalleryLayout;
    private View mPreviewLayout;
    private View mPreviewScrollView;
    private int mPreviewTextureWidth;
    private int mPreviewTextureheight;
    /* access modifiers changed from: private */
    public VideoRecordView mRecordAnimationView;
    private int mScreenHeight;
    private int mScreenWidth;
    private MediaStoreMedia mSelectedGalleryImage;
    /* access modifiers changed from: private */
    public String mShotPicturePath;
    private SurfaceTexture mSurfaceTexture;
    private View mSwitchCameraImageView;
    /* access modifiers changed from: private */
    public ImageView mTakeImageView;
    /* access modifiers changed from: private */
    public int mTakenImageOrigin;
    private ImageView mVideoButtonView;
    private View mVideoPreviewLayout;
    /* access modifiers changed from: private */
    public BitmapDrawable mVideoThumbnail;
    /* access modifiers changed from: private */
    public Uri mVideoUri = null;
    /* access modifiers changed from: private */
    public VideoView mVideoView;

    private class MediaStoreMedia {
        public long mCreationTime;
        public Uri mFileUri;
        public boolean mIsVideo;
        public String mMimeType;
        public Bitmap mThumbnail;

        private MediaStoreMedia() {
            this.mMimeType = "";
        }
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_medias_picker;
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public void initUiAndData() {
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m211e(LOG_TAG, "Restart the application.");
            CommonActivityUtils.restartApp(this);
        } else if (CommonActivityUtils.isGoingToSplash(this)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
        } else {
            Intent intent = getIntent();
            this.mIsAvatarMode = intent.getBooleanExtra(EXTRA_AVATAR_MODE, false);
            this.mIsVideoRecordingSupported = intent.getBooleanExtra(EXTRA_VIDEO_RECORDING_MODE, false);
            this.mCameraId = 0;
            this.mPreviewScrollView = findViewById(C1299R.C1301id.medias_picker_scrollView);
            this.mSwitchCameraImageView = findViewById(C1299R.C1301id.medias_picker_switch_camera);
            this.mCameraTextureView = (TextureView) findViewById(C1299R.C1301id.medias_picker_texture_view);
            this.mCameraTextureView.setSurfaceTextureListener(this);
            this.mCameraTextureMaskView = (ImageView) findViewById(C1299R.C1301id.medias_picker_texture_mask_view);
            this.mRecordAnimationView = (VideoRecordView) findViewById(C1299R.C1301id.medias_record_animation);
            this.mPreviewLayout = findViewById(C1299R.C1301id.medias_picker_preview_layout);
            this.mImagePreviewLayout = findViewById(C1299R.C1301id.medias_picker_preview_image_layout);
            this.mImagePreviewImageView = (ImageView) findViewById(C1299R.C1301id.medias_picker_preview_image_view);
            this.mImagePreviewAvatarModeMaskView = (ImageView) findViewById(C1299R.C1301id.medias_picker_preview_avatar_mode_mask);
            this.mVideoPreviewLayout = findViewById(C1299R.C1301id.medias_picker_preview_video_layout);
            this.mVideoView = (VideoView) findViewById(C1299R.C1301id.medias_picker_preview_video_view);
            this.mVideoButtonView = (ImageView) findViewById(C1299R.C1301id.medias_picker_preview_video_button);
            this.mTakeImageView = (ImageView) findViewById(C1299R.C1301id.medias_picker_camera_button);
            this.mGalleryTableLayout = (TableLayout) findViewById(C1299R.C1301id.gallery_table_layout);
            this.mSwitchCameraImageView.setVisibility(Camera.getNumberOfCameras() > 1 ? 0 : 8);
            this.mSwitchCameraImageView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorMediasPickerActivity.this.onSwitchCamera();
                }
            });
            this.mTakeImageView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorMediasPickerActivity.this.onClickTakeImage();
                }
            });
            this.mTakeImageView.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    if (VectorMediasPickerActivity.this.mIsVideoRecordingSupported && CommonActivityUtils.checkPermissions(5, (Activity) VectorMediasPickerActivity.this)) {
                        VectorMediasPickerActivity.this.mRecordAnimationView.startAnimation();
                        VectorMediasPickerActivity.this.startVideoRecord();
                    }
                    return VectorMediasPickerActivity.this.mIsVideoRecordingSupported;
                }
            });
            this.mTakeImageView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (!VectorMediasPickerActivity.this.mIsRecording || (motionEvent.getAction() != 1 && motionEvent.getAction() != 3)) {
                        return false;
                    }
                    VectorMediasPickerActivity.this.stopVideoRecord();
                    VectorMediasPickerActivity.this.startVideoPreviewVideo(null);
                    return true;
                }
            });
            findViewById(C1299R.C1301id.medias_picker_redo_text_view).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VectorMediasPickerActivity.this.mVideoUri != null) {
                        VectorMediasPickerActivity.this.stopVideoPreview();
                    } else {
                        VectorMediasPickerActivity.this.cancelTakeImage();
                    }
                }
            });
            findViewById(C1299R.C1301id.medias_picker_attach_text_view).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VectorMediasPickerActivity.this.mVideoUri != null) {
                        VectorMediasPickerActivity.this.sendVideoFile();
                    } else {
                        VectorMediasPickerActivity.this.attachImageFrom(VectorMediasPickerActivity.this.mTakenImageOrigin);
                    }
                }
            });
            initCameraLayout();
            this.mHandlerThread = new HandlerThread("VectorMediasPickerActivityThread");
            this.mHandlerThread.start();
            this.mFileHandler = new Handler(this.mHandlerThread.getLooper());
            if (isFirstCreation()) {
                updateUiConfiguration(false, 1);
            } else {
                restoreInstanceState(getSavedInstanceState());
            }
            setRequestedOrientation(4);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.mHandlerThread != null) {
            this.mHandlerThread.quit();
            this.mHandlerThread = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mCamera != null) {
            this.mCamera.stopPreview();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        computePreviewAndGalleryHeight();
        refreshRecentsMediasList();
        startCameraPreview();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(KEY_EXTRA_IS_TAKEN_IMAGE_DISPLAYED, this.mIsTakenImageDisplayed);
        bundle.putBoolean(KEY_IS_AVATAR_MODE, this.mIsAvatarMode);
        bundle.putInt(KEY_EXTRA_TAKEN_IMAGE_ORIGIN, this.mTakenImageOrigin);
        bundle.putInt(KEY_EXTRA_CAMERA_SIDE, this.mCameraId);
        bundle.putString(KEY_EXTRA_TAKEN_IMAGE_CAMERA_URL, this.mShotPicturePath);
        bundle.putParcelable(KEY_EXTRA_TAKEN_IMAGE_GALLERY_URI, (Uri) this.mImagePreviewImageView.getTag());
        if (this.mVideoUri != null) {
            bundle.putParcelable(KEY_EXTRA_TAKEN_VIDEO_URI, this.mVideoUri);
        }
    }

    private void restoreInstanceState(@NonNull Bundle bundle) {
        this.mIsAvatarMode = bundle.getBoolean(KEY_IS_AVATAR_MODE);
        this.mIsTakenImageDisplayed = bundle.getBoolean(KEY_EXTRA_IS_TAKEN_IMAGE_DISPLAYED);
        this.mShotPicturePath = bundle.getString(KEY_EXTRA_TAKEN_IMAGE_CAMERA_URL);
        this.mTakenImageOrigin = bundle.getInt(KEY_EXTRA_TAKEN_IMAGE_ORIGIN);
        Uri uri = (Uri) bundle.getParcelable(KEY_EXTRA_TAKEN_IMAGE_GALLERY_URI);
        this.mImagePreviewImageView.setTag(uri);
        this.mVideoUri = (Uri) bundle.getParcelable(KEY_EXTRA_TAKEN_VIDEO_URI);
        if (this.mIsTakenImageDisplayed) {
            Bitmap savedPickerImagePreview = VectorApp.getSavedPickerImagePreview();
            if (savedPickerImagePreview == null || this.mIsAvatarMode) {
                displayImagePreview(savedPickerImagePreview, this.mShotPicturePath, uri, this.mTakenImageOrigin);
            } else {
                this.mImagePreviewImageView.setImageBitmap(savedPickerImagePreview);
            }
        }
        updateUiConfiguration(this.mIsTakenImageDisplayed, this.mTakenImageOrigin);
        this.mCameraId = bundle.getInt(KEY_EXTRA_CAMERA_SIDE);
        if (this.mVideoUri != null) {
            startVideoPreviewVideo(null);
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"NewApi"})
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 54) {
            Intent intent2 = new Intent();
            intent2.setData(intent.getData());
            if (VERSION.SDK_INT >= 18) {
                intent2.setClipData(intent.getClipData());
            }
            VectorApp.setSavedCameraImagePreview(null);
            setResult(-1, intent2);
            finish();
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void openFileExplorer() {
        try {
            Intent intent = new Intent("android.intent.action.PICK");
            if (VERSION.SDK_INT >= 18) {
                intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", false);
            }
            if (this.mIsVideoRecordingSupported) {
                intent.setType(ResourceUtils.MIME_TYPE_ALL_CONTENT);
            } else {
                intent.setType(ResourceUtils.MIME_TYPE_IMAGE_ALL);
            }
            startActivityForResult(intent, 54);
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), 1).show();
        }
    }

    /* access modifiers changed from: private */
    public void onSwitchCamera() {
        if (Camera.getNumberOfCameras() >= 2) {
            if (this.mCamera != null) {
                if (this.mCameraTextureView != null) {
                    this.mCamera.stopPreview();
                }
                this.mCamera.release();
            }
            this.mCamera = null;
            if (this.mCameraId == 0) {
                this.mCameraId = 1;
            } else {
                this.mCameraId = 0;
            }
            try {
                this.mCamera = Camera.open(this.mCameraId);
                initCameraSettings();
                try {
                    this.mCamera.setPreviewTexture(this.mSurfaceTexture);
                } catch (IOException e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onSwitchCamera(): setPreviewTexture EXCEPTION Msg=");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
                this.mCamera.startPreview();
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## onSwitchCamera(): cannot init the other camera ");
                sb2.append(e2.getMessage());
                Log.m211e(str2, sb2.toString());
                this.mSwitchCameraImageView.setVisibility(8);
                onSwitchCamera();
            }
        }
    }

    private void initCameraSettings() {
        int i;
        int i2;
        int i3;
        int i4;
        try {
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(this.mCameraId, cameraInfo);
            int i5 = 0;
            switch (getWindowManager().getDefaultDisplay().getRotation()) {
                case 1:
                    i = 90;
                    break;
                case 2:
                    i = RotationOptions.ROTATE_180;
                    break;
                case 3:
                    i = RotationOptions.ROTATE_270;
                    break;
                default:
                    i = 0;
                    break;
            }
            if (cameraInfo.facing == 1) {
                int i6 = (cameraInfo.orientation + i) % 360;
                i2 = i6;
                i3 = (360 - i6) % 360;
            } else {
                i3 = ((cameraInfo.orientation - i) + 360) % 360;
                i2 = i3;
            }
            this.mCameraOrientation = i3;
            this.mCamera.setDisplayOrientation(i3);
            Parameters parameters = this.mCamera.getParameters();
            parameters.setRotation(i2);
            if (!this.mIsVideoMode) {
                List supportedPictureSizes = parameters.getSupportedPictureSizes();
                if (supportedPictureSizes.size() > 0) {
                    Size size = (Size) supportedPictureSizes.get(0);
                    long j = (long) (size.width * size.height);
                    for (int i7 = 1; i7 < supportedPictureSizes.size(); i7++) {
                        Size size2 = (Size) supportedPictureSizes.get(i7);
                        long j2 = (long) (size2.width * size2.height);
                        if (j2 > j) {
                            size = size2;
                            j = j2;
                        }
                    }
                    parameters.setPictureSize(size.width, size.height);
                }
                try {
                    this.mCamera.setParameters(parameters);
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## initCameraSettings(): set size fails EXCEPTION Msg=");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            if (supportedPreviewSizes.size() > 0) {
                if (this.mIsVideoMode) {
                    this.mCamcorderProfile = getCamcorderProfile(this.mCameraId);
                    i4 = (this.mCamcorderProfile.videoFrameWidth * 100) / this.mCamcorderProfile.videoFrameHeight;
                } else {
                    Size pictureSize = parameters.getPictureSize();
                    i4 = (pictureSize.width * 100) / pictureSize.height;
                }
                Size size3 = null;
                for (Size size4 : supportedPreviewSizes) {
                    if ((size4.width * 100) / size4.height == i4) {
                        int i8 = size4.height * size4.width;
                        if (i8 > i5) {
                            size3 = size4;
                            i5 = i8;
                        }
                    }
                }
                if (size3 != null) {
                    parameters.setPreviewSize(size3.width, size3.height);
                    try {
                        this.mCamera.setParameters(parameters);
                    } catch (Exception e2) {
                        String str2 = LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("## initCameraSettings(): set preview size fails EXCEPTION Msg=");
                        sb2.append(e2.getMessage());
                        Log.m211e(str2, sb2.toString());
                    }
                }
            }
            if (!this.mIsVideoMode) {
                try {
                    parameters.setFocusMode(ReactScrollViewHelper.AUTO);
                    this.mCamera.setParameters(parameters);
                } catch (Exception e3) {
                    String str3 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("## initCameraSettings(): set auto focus fails EXCEPTION Msg=");
                    sb3.append(e3.getMessage());
                    Log.m211e(str3, sb3.toString());
                }
                try {
                    parameters.setPictureFormat(256);
                    parameters.setJpegQuality(100);
                    this.mCamera.setParameters(parameters);
                } catch (Exception e4) {
                    String str4 = LOG_TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("## initCameraSettings(): set jpeg quality fails EXCEPTION Msg=");
                    sb4.append(e4.getMessage());
                    Log.m211e(str4, sb4.toString());
                }
            }
            resizeCameraPreviewTexture();
        } catch (Exception e5) {
            String str5 = LOG_TAG;
            StringBuilder sb5 = new StringBuilder();
            sb5.append("## ## initCameraSettings(): failed ");
            sb5.append(e5.getMessage());
            Log.m211e(str5, sb5.toString());
        }
    }

    private void resizeCameraPreviewTexture() {
        Size previewSize = this.mCamera.getParameters().getPreviewSize();
        if (this.mCameraOrientation == 90 || this.mCameraOrientation == 270) {
            int i = previewSize.width;
            previewSize.width = previewSize.height;
            previewSize.height = i;
        }
        if ((previewSize.height * 100) / previewSize.width != (this.mPreviewTextureheight * 100) / this.mPreviewTextureWidth) {
            final int i2 = this.mIsVideoMode ? this.mCameraPreviewLayoutHeight : this.mPreviewTextureheight;
            final int i3 = (int) ((((float) i2) * ((float) previewSize.width)) / ((float) previewSize.height));
            if (i3 > this.mPreviewTextureWidth) {
                i3 = this.mPreviewTextureWidth;
                i2 = (int) ((((float) i3) * ((float) previewSize.height)) / ((float) previewSize.width));
                if (i2 > ((int) (((double) this.mScreenHeight) * SURFACE_VIEW_HEIGHT_RATIO))) {
                    i2 = (int) (((double) this.mScreenHeight) * SURFACE_VIEW_HEIGHT_RATIO);
                    i3 = (int) ((((float) i2) * ((float) previewSize.width)) / ((float) previewSize.height));
                }
            }
            LayoutParams layoutParams = this.mCameraTextureView.getLayoutParams();
            layoutParams.width = i3;
            layoutParams.height = i2;
            this.mCameraTextureView.setLayoutParams(layoutParams);
            if (this.mIsAvatarMode) {
                this.mCameraTextureMaskView.setVisibility(0);
                this.mCameraTextureMaskView.post(new Runnable() {
                    public void run() {
                        VectorMediasPickerActivity.this.drawCircleMask(VectorMediasPickerActivity.this.mCameraTextureMaskView, i3, i2);
                    }
                });
            } else {
                this.mCameraTextureMaskView.setVisibility(8);
            }
            if (layoutParams.height != this.mCameraPreviewLayoutHeight && !this.mIsVideoMode) {
                this.mCameraPreviewLayoutHeight = layoutParams.height;
                if (this.mCameraPreviewLayout != null) {
                    RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.mCameraPreviewLayout.getLayoutParams();
                    layoutParams2.height = this.mCameraPreviewLayoutHeight;
                    this.mCameraPreviewLayout.setLayoutParams(layoutParams2);
                }
                computePreviewAndGalleryHeight();
            }
        }
    }

    private void initCameraLayout() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.mScreenHeight = displayMetrics.heightPixels;
        this.mScreenWidth = displayMetrics.widthPixels;
        this.mCameraPreviewLayoutHeight = (int) (((double) this.mScreenHeight) * SURFACE_VIEW_HEIGHT_RATIO);
        this.mCameraPreviewLayout = (RelativeLayout) findViewById(C1299R.C1301id.medias_picker_camera_preview_layout);
        LayoutParams layoutParams = this.mCameraPreviewLayout.getLayoutParams();
        layoutParams.height = this.mCameraPreviewLayoutHeight;
        this.mCameraPreviewLayout.setLayoutParams(layoutParams);
        this.mPreviewAndGalleryLayout = (RelativeLayout) findViewById(C1299R.C1301id.medias_picker_preview_gallery_layout);
        computePreviewAndGalleryHeight();
    }

    private void computePreviewAndGalleryHeight() {
        int galleryRowsCount = getGalleryRowsCount();
        if (this.mPreviewAndGalleryLayout != null) {
            LayoutParams layoutParams = this.mPreviewAndGalleryLayout.getLayoutParams();
            layoutParams.height = this.mCameraPreviewLayoutHeight + ((galleryRowsCount * this.mScreenWidth) / 4);
            this.mPreviewAndGalleryLayout.setLayoutParams(layoutParams);
            return;
        }
        Log.m217w(LOG_TAG, "## computePreviewAndGalleryHeight(): GalleryTable height not set");
    }

    public void onExitButton(View view) {
        finish();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0099  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void displayImagePreview(android.graphics.Bitmap r9, java.lang.String r10, android.net.Uri r11, int r12) {
        /*
            r8 = this;
            r0 = 2131296745(0x7f0901e9, float:1.8211415E38)
            android.view.View r0 = r8.findViewById(r0)
            r8.setWaitingView(r0)
            r8.showWaitingView()
            android.widget.ImageView r0 = r8.mTakeImageView
            r1 = 0
            r0.setEnabled(r1)
            r0 = 1
            r2 = 0
            if (r9 != 0) goto L_0x0038
            if (r0 != r12) goto L_0x0035
            int r3 = r8.mCameraId
            if (r3 != 0) goto L_0x0024
            android.view.TextureView r3 = r8.mCameraTextureView
            android.graphics.Bitmap r3 = r3.getBitmap()
            goto L_0x0025
        L_0x0024:
            r3 = r2
        L_0x0025:
            if (r3 != 0) goto L_0x002b
            android.graphics.Bitmap r3 = r8.createPhotoThumbnail(r10)
        L_0x002b:
            java.io.File r4 = new java.io.File
            r4.<init>(r10)
            android.net.Uri r4 = android.net.Uri.fromFile(r4)
            goto L_0x003a
        L_0x0035:
            r3 = r9
            r4 = r11
            goto L_0x003a
        L_0x0038:
            r3 = r9
            r4 = r2
        L_0x003a:
            com.opengarden.firechat.VectorApp.setSavedCameraImagePreview(r3)
            android.widget.ImageView r5 = r8.mImagePreviewAvatarModeMaskView
            r6 = 8
            r5.setVisibility(r6)
            boolean r5 = r8.mIsAvatarMode
            if (r5 != 0) goto L_0x005a
            if (r3 == 0) goto L_0x0051
            android.widget.ImageView r9 = r8.mImagePreviewImageView
            r9.setImageBitmap(r3)
            goto L_0x00ee
        L_0x0051:
            if (r4 == 0) goto L_0x00ee
            android.widget.ImageView r9 = r8.mImagePreviewImageView
            r9.setImageURI(r4)
            goto L_0x00ee
        L_0x005a:
            if (r3 != 0) goto L_0x0097
            if (r4 == 0) goto L_0x0097
            com.opengarden.firechat.matrixsdk.util.ResourceUtils$Resource r2 = com.opengarden.firechat.matrixsdk.util.ResourceUtils.openResource(r8, r4, r2)     // Catch:{ Exception -> 0x007c }
            if (r2 == 0) goto L_0x0097
            java.io.InputStream r5 = r2.mContentStream     // Catch:{ Exception -> 0x007c }
            if (r5 == 0) goto L_0x0097
            int r4 = com.opengarden.firechat.matrixsdk.util.ImageUtils.getRotationAngleForBitmap(r8, r4)     // Catch:{ Exception -> 0x007c }
            java.io.InputStream r5 = r2.mContentStream     // Catch:{ Exception -> 0x007c }
            android.graphics.Bitmap r4 = r8.createPhotoThumbnail(r5, r4)     // Catch:{ Exception -> 0x007c }
            java.io.InputStream r2 = r2.mContentStream     // Catch:{ Exception -> 0x0079 }
            r2.close()     // Catch:{ Exception -> 0x0079 }
            r3 = r4
            goto L_0x0097
        L_0x0079:
            r2 = move-exception
            r3 = r4
            goto L_0x007d
        L_0x007c:
            r2 = move-exception
        L_0x007d:
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "fails to retrieve the bitmap from uri "
            r5.append(r6)
            java.lang.String r2 = r2.getMessage()
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r2)
        L_0x0097:
            if (r3 == 0) goto L_0x00ee
            android.widget.ImageView r2 = r8.mImagePreviewImageView
            r2.setImageBitmap(r3)
            int r2 = r3.getWidth()
            int r3 = r3.getHeight()
            android.view.Window r4 = r8.getWindow()
            android.view.View r4 = r4.getDecorView()
            int r4 = r4.getHeight()
            android.view.Window r5 = r8.getWindow()
            android.view.View r5 = r5.getDecorView()
            int r5 = r5.getWidth()
            if (r4 == 0) goto L_0x00dd
            if (r5 != 0) goto L_0x00c3
            goto L_0x00dd
        L_0x00c3:
            float r9 = (float) r4
            float r10 = (float) r2
            float r9 = r9 * r10
            float r11 = (float) r3
            float r9 = r9 / r11
            int r9 = (int) r9
            if (r9 <= r5) goto L_0x00d2
            float r9 = (float) r5
            float r9 = r9 * r11
            float r9 = r9 / r10
            int r4 = (int) r9
            r9 = r5
        L_0x00d2:
            android.widget.ImageView r10 = r8.mImagePreviewAvatarModeMaskView
            r10.setVisibility(r1)
            android.widget.ImageView r10 = r8.mImagePreviewAvatarModeMaskView
            r8.drawCircleMask(r10, r9, r4)
            goto L_0x00ee
        L_0x00dd:
            android.widget.ImageView r0 = r8.mImagePreviewImageView
            com.opengarden.firechat.activity.VectorMediasPickerActivity$8 r7 = new com.opengarden.firechat.activity.VectorMediasPickerActivity$8
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r10
            r5 = r11
            r6 = r12
            r1.<init>(r3, r4, r5, r6)
            r0.post(r7)
            return
        L_0x00ee:
            android.widget.ImageView r9 = r8.mTakeImageView
            r9.setEnabled(r0)
            r8.updateUiConfiguration(r0, r12)
            r8.hideWaitingView()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorMediasPickerActivity.displayImagePreview(android.graphics.Bitmap, java.lang.String, android.net.Uri, int):void");
    }

    private void updateUiConfiguration(boolean z, int i) {
        this.mIsTakenImageDisplayed = z;
        this.mTakenImageOrigin = i;
        if (!z) {
            this.mSelectedGalleryImage = null;
        }
        if (z) {
            this.mPreviewLayout.setVisibility(0);
            this.mPreviewScrollView.setVisibility(8);
            return;
        }
        this.mPreviewScrollView.setVisibility(0);
        this.mPreviewLayout.setVisibility(8);
    }

    private void startCameraPreview() {
        try {
            if (this.mCamera != null) {
                this.mCamera.startPreview();
            }
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## startCameraPreview(): Exception Msg=");
            sb.append(e.getMessage());
            Log.m217w(str, sb.toString());
        }
    }

    /* access modifiers changed from: private */
    public void takePhoto() {
        Log.m209d(LOG_TAG, "## takePhoto");
        try {
            this.mCamera.takePicture(null, null, new PictureCallback() {
                /* JADX WARNING: Removed duplicated region for block: B:37:0x00f7 A[Catch:{ Exception -> 0x00fb }] */
                /* JADX WARNING: Removed duplicated region for block: B:44:0x011e A[Catch:{ Exception -> 0x0122 }] */
                /* JADX WARNING: Removed duplicated region for block: B:51:? A[RETURN, SYNTHETIC] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onPictureTaken(byte[] r7, android.hardware.Camera r8) {
                    /*
                        r6 = this;
                        java.lang.String r8 = com.opengarden.firechat.activity.VectorMediasPickerActivity.LOG_TAG
                        java.lang.String r0 = "## onPictureTaken(): success"
                        com.opengarden.firechat.matrixsdk.util.Log.m209d(r8, r0)
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r8 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this
                        boolean r8 = r8.mIsAvatarMode
                        if (r8 == 0) goto L_0x0037
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r8 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this
                        android.view.TextureView r8 = r8.mCameraTextureView
                        android.graphics.Bitmap r8 = r8.getBitmap()
                        if (r8 == 0) goto L_0x0037
                        java.io.ByteArrayOutputStream r7 = new java.io.ByteArrayOutputStream
                        r7.<init>()
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r8 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this
                        android.view.TextureView r8 = r8.mCameraTextureView
                        android.graphics.Bitmap r8 = r8.getBitmap()
                        android.graphics.Bitmap$CompressFormat r0 = android.graphics.Bitmap.CompressFormat.JPEG
                        r1 = 50
                        r8.compress(r0, r1, r7)
                        byte[] r7 = r7.toByteArray()
                    L_0x0037:
                        java.io.ByteArrayInputStream r8 = new java.io.ByteArrayInputStream
                        r8.<init>(r7)
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r7 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r0 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this
                        java.lang.String r7 = r7.getSavedImageName(r0)
                        boolean r0 = android.text.TextUtils.isEmpty(r7)
                        if (r0 != 0) goto L_0x0062
                        java.io.File r0 = new java.io.File
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r1 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this
                        java.io.File r1 = r1.getCacheDir()
                        java.lang.String r1 = r1.getAbsolutePath()
                        r0.<init>(r1, r7)
                        boolean r7 = r0.exists()
                        if (r7 == 0) goto L_0x0062
                        r0.delete()
                    L_0x0062:
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r7 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this
                        java.lang.String r7 = com.opengarden.firechat.activity.VectorMediasPickerActivity.buildNewImageName(r7)
                        java.io.File r0 = new java.io.File
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r1 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this
                        java.io.File r1 = r1.getCacheDir()
                        java.lang.String r1 = r1.getAbsolutePath()
                        r0.<init>(r1, r7)
                        r7 = 0
                        r1 = 0
                        r0.createNewFile()     // Catch:{ Exception -> 0x00d3 }
                        java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00d3 }
                        r2.<init>(r0)     // Catch:{ Exception -> 0x00d3 }
                        r3 = 10240(0x2800, float:1.4349E-41)
                        byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                    L_0x0085:
                        int r4 = r8.read(r3)     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        r5 = -1
                        if (r4 == r5) goto L_0x0090
                        r2.write(r3, r7, r4)     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        goto L_0x0085
                    L_0x0090:
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r3 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        r3.mShotPicturePath = r0     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r0 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r3 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        java.lang.String r3 = r3.mShotPicturePath     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        r4 = 1
                        r0.displayImagePreview(r1, r3, r1, r4)     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r0 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        android.hardware.Camera r0 = r0.mCamera     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        r0.stopPreview()     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        java.lang.String r0 = com.opengarden.firechat.activity.VectorMediasPickerActivity.LOG_TAG     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        java.lang.String r1 = "onPictureTaken processed"
                        com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)     // Catch:{ Exception -> 0x00cd, all -> 0x00cb }
                        r8.close()     // Catch:{ Exception -> 0x00c0 }
                        if (r2 == 0) goto L_0x0118
                        r2.close()     // Catch:{ Exception -> 0x00c0 }
                        goto L_0x0118
                    L_0x00c0:
                        r7 = move-exception
                        java.lang.String r8 = com.opengarden.firechat.activity.VectorMediasPickerActivity.LOG_TAG
                        java.lang.StringBuilder r0 = new java.lang.StringBuilder
                        r0.<init>()
                        goto L_0x0105
                    L_0x00cb:
                        r7 = move-exception
                        goto L_0x0119
                    L_0x00cd:
                        r0 = move-exception
                        r1 = r2
                        goto L_0x00d4
                    L_0x00d0:
                        r7 = move-exception
                        r2 = r1
                        goto L_0x0119
                    L_0x00d3:
                        r0 = move-exception
                    L_0x00d4:
                        com.opengarden.firechat.activity.VectorMediasPickerActivity r2 = com.opengarden.firechat.activity.VectorMediasPickerActivity.this     // Catch:{ all -> 0x00d0 }
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d0 }
                        r3.<init>()     // Catch:{ all -> 0x00d0 }
                        java.lang.String r4 = "Exception onPictureTaken(): "
                        r3.append(r4)     // Catch:{ all -> 0x00d0 }
                        java.lang.String r0 = r0.getLocalizedMessage()     // Catch:{ all -> 0x00d0 }
                        r3.append(r0)     // Catch:{ all -> 0x00d0 }
                        java.lang.String r0 = r3.toString()     // Catch:{ all -> 0x00d0 }
                        android.widget.Toast r7 = android.widget.Toast.makeText(r2, r0, r7)     // Catch:{ all -> 0x00d0 }
                        r7.show()     // Catch:{ all -> 0x00d0 }
                        r8.close()     // Catch:{ Exception -> 0x00fb }
                        if (r1 == 0) goto L_0x0118
                        r1.close()     // Catch:{ Exception -> 0x00fb }
                        goto L_0x0118
                    L_0x00fb:
                        r7 = move-exception
                        java.lang.String r8 = com.opengarden.firechat.activity.VectorMediasPickerActivity.LOG_TAG
                        java.lang.StringBuilder r0 = new java.lang.StringBuilder
                        r0.<init>()
                    L_0x0105:
                        java.lang.String r1 = "## onPictureTaken(): EXCEPTION Msg="
                        r0.append(r1)
                        java.lang.String r7 = r7.getMessage()
                        r0.append(r7)
                        java.lang.String r7 = r0.toString()
                        com.opengarden.firechat.matrixsdk.util.Log.m211e(r8, r7)
                    L_0x0118:
                        return
                    L_0x0119:
                        r8.close()     // Catch:{ Exception -> 0x0122 }
                        if (r2 == 0) goto L_0x013f
                        r2.close()     // Catch:{ Exception -> 0x0122 }
                        goto L_0x013f
                    L_0x0122:
                        r8 = move-exception
                        java.lang.String r0 = com.opengarden.firechat.activity.VectorMediasPickerActivity.LOG_TAG
                        java.lang.StringBuilder r1 = new java.lang.StringBuilder
                        r1.<init>()
                        java.lang.String r2 = "## onPictureTaken(): EXCEPTION Msg="
                        r1.append(r2)
                        java.lang.String r8 = r8.getMessage()
                        r1.append(r8)
                        java.lang.String r8 = r1.toString()
                        com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r8)
                    L_0x013f:
                        throw r7
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorMediasPickerActivity.C15529.onPictureTaken(byte[], android.hardware.Camera):void");
                }
            });
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## takePicture(): EXCEPTION Msg=");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    /* access modifiers changed from: private */
    public void onClickTakeImage() {
        Log.m209d(LOG_TAG, "onClickTakeImage");
        if (this.mCamera != null) {
            List list = null;
            try {
                if (this.mCamera.getParameters() != null) {
                    list = this.mCamera.getParameters().getSupportedFocusModes();
                }
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onClickTakeImage : supported focus modes ");
                sb.append(list);
                Log.m209d(str, sb.toString());
                if (list == null || list.indexOf(ReactScrollViewHelper.AUTO) < 0) {
                    Log.m209d(LOG_TAG, "onClickTakeImage : no autofocus : take photo");
                    playShutterSound();
                    takePhoto();
                    return;
                }
                Log.m209d(LOG_TAG, "onClickTakeImage : autofocus starts");
                this.mCamera.autoFocus(new AutoFocusCallback() {
                    public void onAutoFocus(boolean z, Camera camera) {
                        if (!z) {
                            Log.m211e(VectorMediasPickerActivity.LOG_TAG, "## autoFocus(): fails");
                        } else {
                            Log.m209d(VectorMediasPickerActivity.LOG_TAG, "## autoFocus(): succeeds");
                        }
                        VectorMediasPickerActivity.this.playShutterSound();
                        VectorMediasPickerActivity.this.takePhoto();
                    }
                });
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## autoFocus(): EXCEPTION Msg=");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
                playShutterSound();
                takePhoto();
            }
        }
    }

    /* access modifiers changed from: private */
    public static String buildNewImageName(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("VectorImage_");
        sb.append(new SimpleDateFormat("yyyy-MM-dd_hhmmss").format(new Date()));
        sb.append(".jpg");
        String sb2 = sb.toString();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_PREFERENCE_CAMERA_IMAGE_NAME, sb2).apply();
        return sb2;
    }

    /* access modifiers changed from: private */
    public String getSavedImageName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_PREFERENCE_CAMERA_IMAGE_NAME, null);
    }

    private Bitmap createPhotoThumbnail(InputStream inputStream, int i) {
        Options options = new Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.outWidth = -1;
        options.outHeight = -1;
        Bitmap bitmap = null;
        try {
            InputStream resizeImage = ImageUtils.resizeImage(inputStream, 1024, 0, 100);
            inputStream.close();
            Bitmap decodeStream = BitmapFactory.decodeStream(resizeImage, null, options);
            if (i != 0) {
                try {
                    Matrix matrix = new Matrix();
                    matrix.postRotate((float) i);
                    bitmap = Bitmap.createBitmap(decodeStream, 0, 0, decodeStream.getWidth(), decodeStream.getHeight(), matrix, false);
                } catch (OutOfMemoryError unused) {
                    bitmap = decodeStream;
                    Log.m211e(LOG_TAG, "## createPhotoThumbnail : out of memory");
                    return bitmap;
                } catch (Exception e) {
                    e = e;
                    bitmap = decodeStream;
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## createPhotoThumbnail() Exception Msg=");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                    return bitmap;
                }
            } else {
                bitmap = decodeStream;
            }
            System.gc();
        } catch (OutOfMemoryError unused2) {
        } catch (Exception e2) {
            e = e2;
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## createPhotoThumbnail() Exception Msg=");
            sb2.append(e.getMessage());
            Log.m211e(str2, sb2.toString());
            return bitmap;
        }
        return bitmap;
    }

    private Bitmap createPhotoThumbnail(String str) {
        Bitmap bitmap = null;
        if (str == null) {
            return null;
        }
        Uri fromFile = Uri.fromFile(new File(str));
        int rotationAngleForBitmap = ImageUtils.getRotationAngleForBitmap(this, fromFile);
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(fromFile.getPath()));
            Bitmap createPhotoThumbnail = createPhotoThumbnail(fileInputStream, rotationAngleForBitmap);
            try {
                fileInputStream.close();
                System.gc();
                return createPhotoThumbnail;
            } catch (OutOfMemoryError unused) {
                bitmap = createPhotoThumbnail;
                Log.m211e(LOG_TAG, "## createPhotoThumbnail : out of memory");
                return bitmap;
            } catch (Exception e) {
                Exception exc = e;
                bitmap = createPhotoThumbnail;
                e = exc;
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## createPhotoThumbnail() Exception Msg=");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
                return bitmap;
            }
        } catch (OutOfMemoryError unused2) {
            Log.m211e(LOG_TAG, "## createPhotoThumbnail : out of memory");
            return bitmap;
        } catch (Exception e2) {
            e = e2;
            String str22 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## createPhotoThumbnail() Exception Msg=");
            sb2.append(e.getMessage());
            Log.m211e(str22, sb2.toString());
            return bitmap;
        }
    }

    /* access modifiers changed from: private */
    public void cancelTakeImage() {
        this.mShotPicturePath = null;
        this.mSelectedGalleryImage = null;
        VectorApp.setSavedCameraImagePreview(null);
        startCameraPreview();
        updateUiConfiguration(false, 1);
    }

    /* access modifiers changed from: private */
    public void attachImageFrom(int i) {
        if (1 == i) {
            attachImageFromCamera();
        } else if (2 == i) {
            attachImageFromGallery();
        } else {
            Log.m217w(LOG_TAG, "## attachImageFrom(): unknown image origin");
        }
    }

    private static String getThumbnailPath(String str) {
        if (TextUtils.isEmpty(str) || !str.endsWith(".jpg")) {
            return null;
        }
        return str.replace(".jpg", "_thumb.jpg");
    }

    private void attachImageFromCamera() {
        try {
            if (this.mShotPicturePath != null) {
                Uri fromFile = Uri.fromFile(new File(this.mShotPicturePath));
                try {
                    Bitmap savedPickerImagePreview = VectorApp.getSavedPickerImagePreview();
                    String thumbnailPath = getThumbnailPath(this.mShotPicturePath);
                    int rotationAngleForBitmap = ImageUtils.getRotationAngleForBitmap(this, fromFile);
                    if (rotationAngleForBitmap != 0) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate((float) (360 - rotationAngleForBitmap));
                        savedPickerImagePreview = Bitmap.createBitmap(savedPickerImagePreview, 0, 0, savedPickerImagePreview.getWidth(), savedPickerImagePreview.getHeight(), matrix, false);
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(thumbnailPath));
                    savedPickerImagePreview.compress(CompressFormat.JPEG, 50, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("attachImageFromCamera fails to create thumbnail file ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                intent.setData(fromFile);
                intent.putExtras(bundle);
                setResult(-1, intent);
            }
        } catch (Exception unused) {
            setResult(0, null);
        } catch (Throwable th) {
            VectorApp.setSavedCameraImagePreview(null);
            finish();
            throw th;
        }
        VectorApp.setSavedCameraImagePreview(null);
        finish();
    }

    /* access modifiers changed from: private */
    public void playShutterSound() {
        new MediaActionSound().play(0);
    }

    /* access modifiers changed from: private */
    public void drawCircleMask(ImageView imageView, int i, int i2) {
        imageView.setBackgroundResource(0);
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(getResources().getColor(17170444));
        Paint paint = new Paint(1);
        paint.setStyle(Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
        paint.setColor(0);
        int i3 = i / 2;
        int i4 = i2 / 2;
        canvas.drawCircle((float) i3, (float) i4, (float) Math.min(i3, i4), paint);
        canvas.drawBitmap(createBitmap, 0.0f, 0.0f, null);
        imageView.setImageBitmap(createBitmap);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        try {
            this.mCamera = Camera.open(this.mCameraId);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot open the camera ");
            sb.append(this.mCameraId);
            sb.append(StringUtils.SPACE);
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
        if (this.mCamera == null) {
            this.mSwitchCameraImageView.setVisibility(8);
            try {
                this.mCamera = Camera.open(this.mCameraId == 0 ? 1 : 0);
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Cannot open the camera ");
                sb2.append(this.mCameraId);
                sb2.append(StringUtils.SPACE);
                sb2.append(e2.getMessage());
                Log.m211e(str2, sb2.toString());
            }
        }
        if (this.mCamera == null) {
            Log.m217w(LOG_TAG, "## onSurfaceTextureAvailable() camera creation failed");
            return;
        }
        try {
            this.mSurfaceTexture = surfaceTexture;
            this.mCamera.setPreviewTexture(surfaceTexture);
            this.mPreviewTextureWidth = i;
            this.mPreviewTextureheight = i2;
            initCameraSettings();
            this.mCamera.startPreview();
        } catch (Exception unused) {
            if (this.mCamera != null) {
                try {
                    this.mCamera.stopPreview();
                    this.mCamera.release();
                } catch (Exception e3) {
                    String str3 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("## onSurfaceTextureAvailable() : ");
                    sb3.append(e3.getMessage());
                    Log.m211e(str3, sb3.toString());
                }
                this.mCamera = null;
            }
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onSurfaceTextureSizeChanged(): width=");
        sb.append(i);
        sb.append(" height=");
        sb.append(i2);
        Log.m209d(str, sb.toString());
        if (surfaceTexture != null) {
            try {
                EGL10 egl10 = (EGL10) EGLContext.getEGL();
                EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                egl10.eglInitialize(eglGetDisplay, null);
                EGLConfig[] eGLConfigArr = new EGLConfig[1];
                EGL10 egl102 = egl10;
                EGLDisplay eGLDisplay = eglGetDisplay;
                EGLConfig[] eGLConfigArr2 = eGLConfigArr;
                egl102.eglChooseConfig(eGLDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12344, 0, 12344}, eGLConfigArr2, eGLConfigArr.length, new int[1]);
                EGLConfig eGLConfig = eGLConfigArr[0];
                EGLContext eglCreateContext = egl10.eglCreateContext(eglGetDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                EGLSurface eglCreateWindowSurface = egl10.eglCreateWindowSurface(eglGetDisplay, eGLConfig, surfaceTexture, new int[]{12344});
                egl10.eglMakeCurrent(eglGetDisplay, eglCreateWindowSurface, eglCreateWindowSurface, eglCreateContext);
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GLES20.glClear(16384);
                egl10.eglSwapBuffers(eglGetDisplay, eglCreateWindowSurface);
                egl10.eglDestroySurface(eglGetDisplay, eglCreateWindowSurface);
                egl10.eglMakeCurrent(eglGetDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                egl10.eglDestroyContext(eglGetDisplay, eglCreateContext);
                egl10.eglTerminate(eglGetDisplay);
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## onSurfaceTextureSizeChanged() failed ");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
            }
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (this.mCamera != null) {
            this.mCamera.stopPreview();
            this.mCamera.release();
        }
        this.mSurfaceTexture = null;
        this.mCamera = null;
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0054  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.media.CamcorderProfile getCamcorderProfile(int r5) {
        /*
            r0 = 4
            boolean r1 = android.media.CamcorderProfile.hasProfile(r0)
            if (r1 == 0) goto L_0x0027
            android.media.CamcorderProfile r0 = android.media.CamcorderProfile.get(r0)     // Catch:{ Exception -> 0x000c }
            goto L_0x0028
        L_0x000c:
            r0 = move-exception
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "## getCamcorderProfile() : "
            r2.append(r3)
            java.lang.String r0 = r0.getMessage()
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r0)
        L_0x0027:
            r0 = 0
        L_0x0028:
            if (r0 != 0) goto L_0x0052
            r1 = 5
            boolean r2 = android.media.CamcorderProfile.hasProfile(r1)
            if (r2 == 0) goto L_0x0052
            android.media.CamcorderProfile r1 = android.media.CamcorderProfile.get(r1)     // Catch:{ Exception -> 0x0037 }
            r0 = r1
            goto L_0x0052
        L_0x0037:
            r1 = move-exception
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "## getCamcorderProfile() : "
            r3.append(r4)
            java.lang.String r1 = r1.getMessage()
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r1)
        L_0x0052:
            if (r0 != 0) goto L_0x0059
            r0 = 1
            android.media.CamcorderProfile r0 = android.media.CamcorderProfile.get(r0)
        L_0x0059:
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "getCamcorderProfile for camera "
            r2.append(r3)
            r2.append(r5)
            java.lang.String r5 = " width "
            r2.append(r5)
            int r5 = r0.videoFrameWidth
            r2.append(r5)
            java.lang.String r5 = " height "
            r2.append(r5)
            int r5 = r0.videoFrameWidth
            r2.append(r5)
            java.lang.String r5 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r5)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorMediasPickerActivity.getCamcorderProfile(int):android.media.CamcorderProfile");
    }

    private static String buildNewVideoName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_hhmmss");
        StringBuilder sb = new StringBuilder();
        sb.append("VectorVideo_");
        sb.append(simpleDateFormat.format(new Date()));
        sb.append(".mp4");
        return sb.toString();
    }

    private int getVideoRotation() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(this.mCameraId, cameraInfo);
        int i = 0;
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case 1:
                i = 90;
                break;
            case 2:
                i = RotationOptions.ROTATE_180;
                break;
            case 3:
                i = RotationOptions.ROTATE_270;
                break;
        }
        if (cameraInfo.facing == 1) {
            return (cameraInfo.orientation + i) % 360;
        }
        return ((cameraInfo.orientation - i) + 360) % 360;
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void startVideoRecord() {
        if (this.mCamera != null) {
            this.mActivityOrientation = getRequestedOrientation();
            setRequestedOrientation(VERSION.SDK_INT < 18 ? 14 : 5);
            this.mTakeImageView.setAlpha(0.0f);
            this.mRecordAnimationView.setVisibility(0);
            this.mRecordAnimationView.startAnimation();
            this.mIsVideoMode = true;
            initCameraSettings();
            this.mMediaRecorder = new MediaRecorder();
            this.mCamera.unlock();
            this.mMediaRecorder.setCamera(this.mCamera);
            try {
                this.mMediaRecorder.setAudioSource(0);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## startVideoRecord() : setAudioSource fails ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
            try {
                this.mMediaRecorder.setVideoSource(1);
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## startVideoRecord() : setVideoSource fails ");
                sb2.append(e2.getMessage());
                Log.m211e(str2, sb2.toString());
            }
            try {
                this.mMediaRecorder.setProfile(this.mCamcorderProfile);
            } catch (Exception e3) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## startVideoRecord() : setProfile fails ");
                sb3.append(e3.getMessage());
                Log.m211e(str3, sb3.toString());
            }
            File file = new File(getCacheDir().getAbsolutePath(), buildNewVideoName());
            this.mVideoUri = Uri.fromFile(file);
            this.mMediaRecorder.setOutputFile(file.getPath());
            this.mMediaRecorder.setOrientationHint(getVideoRotation());
            try {
                this.mMediaRecorder.prepare();
                try {
                    this.mMediaRecorder.start();
                    this.mIsRecording = true;
                } catch (Exception e4) {
                    String str4 = LOG_TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("## startVideoRecord() : cannot start the media recorder ");
                    sb4.append(e4.getMessage());
                    Log.m211e(str4, sb4.toString());
                    Toast.makeText(this, getString(C1299R.string.media_picker_cannot_record_video), 0).show();
                    stopVideoRecord();
                }
            } catch (Exception e5) {
                String str5 = LOG_TAG;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("## startVideoRecord() : cannot prepare the media recorder ");
                sb5.append(e5.getMessage());
                Log.m211e(str5, sb5.toString());
                Toast.makeText(this, getString(C1299R.string.media_picker_cannot_record_video), 0).show();
                stopVideoRecord();
            }
        }
    }

    private void releaseMediaRecorder() {
        if (this.mMediaRecorder != null) {
            try {
                this.mMediaRecorder.stop();
                this.mMediaRecorder.release();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## releaseMediaRecorder() : mMediaRecorder release failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
            this.mMediaRecorder = null;
        }
        try {
            this.mCamera.reconnect();
        } catch (Exception e2) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## releaseMediaRecorder() : mCamera reconnect failed ");
            sb2.append(e2.getMessage());
            Log.m211e(str2, sb2.toString());
        }
    }

    /* access modifiers changed from: private */
    public void stopVideoRecord() {
        if (this.mIsRecording) {
            this.mIsVideoMode = false;
            this.mIsRecording = false;
            this.mTakeImageView.setAlpha(1.0f);
            this.mRecordAnimationView.setVisibility(8);
            this.mRecordAnimationView.startAnimation();
            releaseMediaRecorder();
            setRequestedOrientation(this.mActivityOrientation);
            initCameraSettings();
        }
    }

    /* access modifiers changed from: private */
    public void sendVideoFile() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        intent.setData(this.mVideoUri);
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
    }

    /* access modifiers changed from: private */
    public void stopVideoPreview() {
        if (this.mVideoView.isPlaying()) {
            this.mVideoView.stopPlayback();
            this.mVideoView.setVideoURI(null);
        }
        this.mVideoView.setVisibility(8);
        this.mPreviewLayout.setVisibility(8);
        this.mImagePreviewLayout.setVisibility(0);
        this.mVideoPreviewLayout.setVisibility(8);
        this.mVideoUri = null;
        refreshPlayVideoButton();
    }

    /* access modifiers changed from: private */
    public void startVideoPreviewVideo(Bitmap bitmap) {
        this.mPreviewLayout.setVisibility(0);
        this.mImagePreviewLayout.setVisibility(8);
        this.mVideoPreviewLayout.setVisibility(0);
        if (bitmap == null) {
            ThumbnailUtils.createVideoThumbnail(this.mVideoUri.getPath(), 2);
        }
        if (bitmap == null) {
            bitmap = ThumbnailUtils.createVideoThumbnail(this.mVideoUri.getPath(), 1);
        }
        this.mVideoThumbnail = bitmap != null ? new BitmapDrawable(bitmap) : null;
        this.mVideoView.setVisibility(0);
        this.mVideoView.setBackground(this.mVideoThumbnail);
        this.mVideoView.setVideoURI(this.mVideoUri);
        refreshPlayVideoButton();
        this.mVideoButtonView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VectorMediasPickerActivity.this.mVideoView.isPlaying()) {
                    VectorMediasPickerActivity.this.mVideoView.stopPlayback();
                    VectorMediasPickerActivity.this.mVideoView.post(new Runnable() {
                        public void run() {
                            VectorMediasPickerActivity.this.mVideoView.setBackground(VectorMediasPickerActivity.this.mVideoThumbnail);
                            VectorMediasPickerActivity.this.refreshPlayVideoButton();
                        }
                    });
                    return;
                }
                VectorMediasPickerActivity.this.mVideoView.setBackground(null);
                VectorMediasPickerActivity.this.mVideoView.start();
                VectorMediasPickerActivity.this.mVideoView.post(new Runnable() {
                    public void run() {
                        VectorMediasPickerActivity.this.refreshPlayVideoButton();
                    }
                });
            }
        });
        this.mVideoView.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                VectorMediasPickerActivity.this.mVideoView.setBackground(VectorMediasPickerActivity.this.mVideoThumbnail);
                VectorMediasPickerActivity.this.refreshPlayVideoButton();
            }
        });
        this.mVideoView.setOnErrorListener(new OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                VectorMediasPickerActivity.this.mVideoView.setBackground(VectorMediasPickerActivity.this.mVideoThumbnail);
                VectorMediasPickerActivity.this.refreshPlayVideoButton();
                return false;
            }
        });
    }

    /* access modifiers changed from: private */
    public void refreshPlayVideoButton() {
        this.mVideoButtonView.setImageResource((this.mVideoView == null || !this.mVideoView.isPlaying()) ? C1299R.C1300drawable.camera_play : C1299R.C1300drawable.camera_stop);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0107 A[LOOP:0: B:8:0x0060->B:22:0x0107, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x010a A[EDGE_INSN: B:48:0x010a->B:23:0x010a ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.opengarden.firechat.activity.VectorMediasPickerActivity.MediaStoreMedia> listLatestMedias() {
        /*
            r24 = this;
            r1 = r24
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r3 = 3
            java.lang.String[] r6 = new java.lang.String[r3]
            java.lang.String r4 = "_id"
            r10 = 0
            r6[r10] = r4
            java.lang.String r4 = "datetaken"
            r11 = 1
            r6[r11] = r4
            java.lang.String r4 = "mime_type"
            r12 = 2
            r6[r12] = r4
            r13 = 0
            android.content.ContentResolver r4 = r24.getContentResolver()     // Catch:{ Exception -> 0x0029 }
            android.net.Uri r5 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x0029 }
            r7 = 0
            r8 = 0
            java.lang.String r9 = "datetaken DESC LIMIT 12"
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0029 }
            goto L_0x0046
        L_0x0029:
            r0 = move-exception
            r4 = r0
            java.lang.String r5 = LOG_TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "## listLatestMedias() : "
            r6.append(r7)
            java.lang.String r4 = r4.getMessage()
            r6.append(r4)
            java.lang.String r4 = r6.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r4)
            r4 = r13
        L_0x0046:
            if (r4 == 0) goto L_0x010d
            java.lang.String r5 = "datetaken"
            int r5 = r4.getColumnIndex(r5)
            java.lang.String r6 = "_id"
            int r6 = r4.getColumnIndex(r6)
            java.lang.String r7 = "mime_type"
            int r7 = r4.getColumnIndex(r7)
            boolean r8 = r4.moveToFirst()
            if (r8 == 0) goto L_0x010a
        L_0x0060:
            com.opengarden.firechat.activity.VectorMediasPickerActivity$MediaStoreMedia r8 = new com.opengarden.firechat.activity.VectorMediasPickerActivity$MediaStoreMedia     // Catch:{ Exception -> 0x00e0 }
            r8.<init>()     // Catch:{ Exception -> 0x00e0 }
            r8.mIsVideo = r10     // Catch:{ Exception -> 0x00e0 }
            java.lang.String r9 = r4.getString(r6)     // Catch:{ Exception -> 0x00e0 }
            java.lang.String r14 = r4.getString(r5)     // Catch:{ Exception -> 0x00e0 }
            java.lang.String r15 = r4.getString(r7)     // Catch:{ Exception -> 0x00e0 }
            r8.mMimeType = r15     // Catch:{ Exception -> 0x00e0 }
            long r14 = java.lang.Long.parseLong(r14)     // Catch:{ Exception -> 0x00e0 }
            r8.mCreationTime = r14     // Catch:{ Exception -> 0x00e0 }
            android.content.ContentResolver r14 = r24.getContentResolver()     // Catch:{ Exception -> 0x00e0 }
            r16 = r4
            long r3 = java.lang.Long.parseLong(r9)     // Catch:{ Exception -> 0x00de }
            android.graphics.Bitmap r3 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r14, r3, r11, r13)     // Catch:{ Exception -> 0x00de }
            r8.mThumbnail = r3     // Catch:{ Exception -> 0x00de }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00de }
            r3.<init>()     // Catch:{ Exception -> 0x00de }
            android.net.Uri r4 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00de }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x00de }
            r3.append(r4)     // Catch:{ Exception -> 0x00de }
            java.lang.String r4 = "/"
            r3.append(r4)     // Catch:{ Exception -> 0x00de }
            r3.append(r9)     // Catch:{ Exception -> 0x00de }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x00de }
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x00de }
            r8.mFileUri = r3     // Catch:{ Exception -> 0x00de }
            android.net.Uri r3 = r8.mFileUri     // Catch:{ Exception -> 0x00de }
            int r3 = com.opengarden.firechat.matrixsdk.util.ImageUtils.getRotationAngleForBitmap(r1, r3)     // Catch:{ Exception -> 0x00de }
            if (r3 == 0) goto L_0x00da
            android.graphics.Matrix r4 = new android.graphics.Matrix     // Catch:{ Exception -> 0x00de }
            r4.<init>()     // Catch:{ Exception -> 0x00de }
            float r3 = (float) r3     // Catch:{ Exception -> 0x00de }
            r4.postRotate(r3)     // Catch:{ Exception -> 0x00de }
            android.graphics.Bitmap r3 = r8.mThumbnail     // Catch:{ Exception -> 0x00de }
            r18 = 0
            r19 = 0
            android.graphics.Bitmap r9 = r8.mThumbnail     // Catch:{ Exception -> 0x00de }
            int r20 = r9.getWidth()     // Catch:{ Exception -> 0x00de }
            android.graphics.Bitmap r9 = r8.mThumbnail     // Catch:{ Exception -> 0x00de }
            int r21 = r9.getHeight()     // Catch:{ Exception -> 0x00de }
            r23 = 0
            r17 = r3
            r22 = r4
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r17, r18, r19, r20, r21, r22, r23)     // Catch:{ Exception -> 0x00de }
            r8.mThumbnail = r3     // Catch:{ Exception -> 0x00de }
        L_0x00da:
            r2.add(r8)     // Catch:{ Exception -> 0x00de }
            goto L_0x00fe
        L_0x00de:
            r0 = move-exception
            goto L_0x00e3
        L_0x00e0:
            r0 = move-exception
            r16 = r4
        L_0x00e3:
            r3 = r0
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "## listLatestMedias(): Msg="
            r8.append(r9)
            java.lang.String r3 = r3.getMessage()
            r8.append(r3)
            java.lang.String r3 = r8.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r3)
        L_0x00fe:
            r4 = r16
            boolean r3 = r4.moveToNext()
            if (r3 != 0) goto L_0x0107
            goto L_0x010a
        L_0x0107:
            r3 = 3
            goto L_0x0060
        L_0x010a:
            r4.close()
        L_0x010d:
            boolean r3 = r1.mIsVideoRecordingSupported
            if (r3 == 0) goto L_0x01e0
            r3 = 3
            java.lang.String[] r6 = new java.lang.String[r3]
            java.lang.String r3 = "_id"
            r6[r10] = r3
            java.lang.String r3 = "datetaken"
            r6[r11] = r3
            java.lang.String r3 = "mime_type"
            r6[r12] = r3
            android.content.ContentResolver r4 = r24.getContentResolver()     // Catch:{ Exception -> 0x012f }
            android.net.Uri r5 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x012f }
            r7 = 0
            r8 = 0
            java.lang.String r9 = "datetaken DESC LIMIT 12"
            android.database.Cursor r3 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x012f }
            goto L_0x014c
        L_0x012f:
            r0 = move-exception
            r3 = r0
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "## listLatestMedias(): "
            r5.append(r6)
            java.lang.String r3 = r3.getMessage()
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r3)
            r3 = r13
        L_0x014c:
            if (r3 == 0) goto L_0x01d8
            java.lang.String r4 = "datetaken"
            int r4 = r3.getColumnIndex(r4)
            java.lang.String r5 = "_id"
            int r5 = r3.getColumnIndex(r5)
            java.lang.String r6 = "mime_type"
            int r6 = r3.getColumnIndex(r6)
            boolean r7 = r3.moveToFirst()
            if (r7 == 0) goto L_0x01d5
        L_0x0166:
            com.opengarden.firechat.activity.VectorMediasPickerActivity$MediaStoreMedia r7 = new com.opengarden.firechat.activity.VectorMediasPickerActivity$MediaStoreMedia     // Catch:{ Exception -> 0x01b3 }
            r7.<init>()     // Catch:{ Exception -> 0x01b3 }
            r7.mIsVideo = r11     // Catch:{ Exception -> 0x01b3 }
            java.lang.String r8 = r3.getString(r5)     // Catch:{ Exception -> 0x01b3 }
            java.lang.String r9 = r3.getString(r4)     // Catch:{ Exception -> 0x01b3 }
            java.lang.String r12 = r3.getString(r6)     // Catch:{ Exception -> 0x01b3 }
            r7.mMimeType = r12     // Catch:{ Exception -> 0x01b3 }
            long r14 = java.lang.Long.parseLong(r9)     // Catch:{ Exception -> 0x01b3 }
            r7.mCreationTime = r14     // Catch:{ Exception -> 0x01b3 }
            android.content.ContentResolver r9 = r24.getContentResolver()     // Catch:{ Exception -> 0x01b3 }
            long r14 = java.lang.Long.parseLong(r8)     // Catch:{ Exception -> 0x01b3 }
            android.graphics.Bitmap r9 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r9, r14, r11, r13)     // Catch:{ Exception -> 0x01b3 }
            r7.mThumbnail = r9     // Catch:{ Exception -> 0x01b3 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01b3 }
            r9.<init>()     // Catch:{ Exception -> 0x01b3 }
            android.net.Uri r12 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x01b3 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x01b3 }
            r9.append(r12)     // Catch:{ Exception -> 0x01b3 }
            java.lang.String r12 = "/"
            r9.append(r12)     // Catch:{ Exception -> 0x01b3 }
            r9.append(r8)     // Catch:{ Exception -> 0x01b3 }
            java.lang.String r8 = r9.toString()     // Catch:{ Exception -> 0x01b3 }
            android.net.Uri r8 = android.net.Uri.parse(r8)     // Catch:{ Exception -> 0x01b3 }
            r7.mFileUri = r8     // Catch:{ Exception -> 0x01b3 }
            r2.add(r7)     // Catch:{ Exception -> 0x01b3 }
            goto L_0x01cf
        L_0x01b3:
            r0 = move-exception
            r7 = r0
            java.lang.String r8 = LOG_TAG
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r12 = "## listLatestMedias(): Msg="
            r9.append(r12)
            java.lang.String r7 = r7.getMessage()
            r9.append(r7)
            java.lang.String r7 = r9.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r8, r7)
        L_0x01cf:
            boolean r7 = r3.moveToNext()
            if (r7 != 0) goto L_0x0166
        L_0x01d5:
            r3.close()
        L_0x01d8:
            com.opengarden.firechat.activity.VectorMediasPickerActivity$14 r3 = new com.opengarden.firechat.activity.VectorMediasPickerActivity$14
            r3.<init>()
            java.util.Collections.sort(r2, r3)
        L_0x01e0:
            int r3 = r2.size()
            r4 = 12
            if (r3 <= r4) goto L_0x01f4
            java.lang.String r3 = LOG_TAG
            java.lang.String r5 = "## listLatestMedias(): Added count=12"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r3, r5)
            java.util.List r2 = r2.subList(r10, r4)
            return r2
        L_0x01f4:
            java.lang.String r3 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "## listLatestMedias(): Added count="
            r4.append(r5)
            int r5 = r2.size()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r3, r4)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorMediasPickerActivity.listLatestMedias():java.util.List");
    }

    private int getMediaStoreMediasCount() {
        Cursor cursor;
        Cursor cursor2 = null;
        try {
            cursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getMediaStoreImageCount() Exception Msg=");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            cursor = null;
        }
        int i = 0;
        if (cursor != null) {
            i = 0 + cursor.getCount();
            cursor.close();
        }
        if (this.mIsVideoRecordingSupported) {
            try {
                cursor2 = getContentResolver().query(Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## getMediaStoreImageCount() Exception Msg=");
                sb2.append(e2.getMessage());
                Log.m211e(str2, sb2.toString());
            }
            if (cursor2 != null) {
                i += cursor2.getCount();
                cursor2.close();
            }
        }
        return Math.min(i, 12);
    }

    private int getGalleryRowsCount() {
        this.mGalleryImageCount = getMediaStoreMediasCount();
        if (this.mGalleryImageCount == 0 || this.mGalleryImageCount % 4 != 0) {
            return (this.mGalleryImageCount / 4) + 1;
        }
        int i = this.mGalleryImageCount / 4;
        this.mGalleryImageCount--;
        return i;
    }

    private void refreshRecentsMediasList() {
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(C1299R.C1301id.medias_preview_progress_bar_layout);
        relativeLayout.setVisibility(0);
        this.mTakeImageView.setEnabled(false);
        this.mTakeImageView.setAlpha(0.5f);
        this.mMediaStoreMediasList.clear();
        this.mFileHandler.post(new Runnable() {
            public void run() {
                final List access$3000 = VectorMediasPickerActivity.this.listLatestMedias();
                VectorMediasPickerActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        VectorMediasPickerActivity.this.mMediaStoreMediasList.addAll(access$3000);
                        VectorMediasPickerActivity.this.buildGalleryTableLayout();
                        relativeLayout.setVisibility(8);
                        VectorMediasPickerActivity.this.mTakeImageView.setEnabled(true);
                        VectorMediasPickerActivity.this.mTakeImageView.setAlpha(1.0f);
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public void buildGalleryTableLayout() {
        TableRow.LayoutParams layoutParams;
        ScaleType scaleType;
        final MediaStoreMedia mediaStoreMedia;
        TableLayout.LayoutParams layoutParams2 = new TableLayout.LayoutParams();
        if (this.mGalleryTableLayout != null) {
            this.mGalleryTableLayout.removeAllViews();
            this.mGalleryTableLayout.setBackgroundColor(-1);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int i = displayMetrics.widthPixels;
            int i2 = (i - 8) / 4;
            if (i == 0) {
                scaleType = ScaleType.FIT_XY;
                layoutParams = new TableRow.LayoutParams(-1, -1);
            } else {
                scaleType = ScaleType.FIT_CENTER;
                layoutParams = new TableRow.LayoutParams(i2, i2);
            }
            int i3 = 0;
            layoutParams.setMargins(2, 0, 2, 0);
            layoutParams2.setMargins(2, 2, 2, 2);
            TableRow tableRow = null;
            while (i3 < this.mGalleryImageCount) {
                try {
                    mediaStoreMedia = (MediaStoreMedia) this.mMediaStoreMediasList.get(i3);
                } catch (IndexOutOfBoundsException unused) {
                    mediaStoreMedia = null;
                }
                if (i3 % 4 == 0) {
                    if (tableRow != null) {
                        this.mGalleryTableLayout.addView(tableRow, layoutParams2);
                    }
                    tableRow = new TableRow(this);
                }
                if (mediaStoreMedia != null) {
                    RecentMediaLayout recentMediaLayout = new RecentMediaLayout(this);
                    if (mediaStoreMedia.mThumbnail != null) {
                        recentMediaLayout.setThumbnail(mediaStoreMedia.mThumbnail);
                    } else {
                        recentMediaLayout.setThumbnailByUri(mediaStoreMedia.mFileUri);
                    }
                    recentMediaLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
                    recentMediaLayout.setThumbnailScaleType(scaleType);
                    recentMediaLayout.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            if (!mediaStoreMedia.mIsVideo) {
                                VectorMediasPickerActivity.this.onClickGalleryImage(mediaStoreMedia);
                                return;
                            }
                            VectorMediasPickerActivity.this.mVideoUri = mediaStoreMedia.mFileUri;
                            VectorMediasPickerActivity.this.startVideoPreviewVideo(mediaStoreMedia.mThumbnail);
                        }
                    });
                    recentMediaLayout.setIsVideo(mediaStoreMedia.mIsVideo);
                    if (!mediaStoreMedia.mIsVideo) {
                        recentMediaLayout.enableGifLogoImage(MIME_TYPE_IMAGE_GIF.equals(mediaStoreMedia.mMimeType));
                        recentMediaLayout.enableMediaTypeLogoImage(Boolean.valueOf(!MIME_TYPE_IMAGE_GIF.equals(mediaStoreMedia.mMimeType)));
                    }
                    if (tableRow != null) {
                        tableRow.addView(recentMediaLayout, layoutParams);
                    }
                }
                i3++;
            }
            RecentMediaLayout recentMediaLayout2 = new RecentMediaLayout(this);
            recentMediaLayout2.setThumbnailScaleType(scaleType);
            recentMediaLayout2.setThumbnailByResource(C1299R.C1300drawable.ic_material_folder_green_vector);
            recentMediaLayout2.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorMediasPickerActivity.this.openFileExplorer();
                }
            });
            if (i3 == 0) {
                tableRow = new TableRow(this);
            }
            if (tableRow != null) {
                tableRow.addView(recentMediaLayout2, layoutParams);
            }
            if (tableRow != null) {
                this.mGalleryTableLayout.addView(tableRow, layoutParams2);
                return;
            }
            return;
        }
        Log.m217w(LOG_TAG, "## buildGalleryImageTableLayout(): failure - TableLayout widget missing");
    }

    /* access modifiers changed from: private */
    public void onClickGalleryImage(MediaStoreMedia mediaStoreMedia) {
        if (this.mCamera != null) {
            this.mCamera.stopPreview();
        }
        this.mSelectedGalleryImage = mediaStoreMedia;
        if (mediaStoreMedia.mThumbnail != null && !this.mIsAvatarMode) {
            updateUiConfiguration(true, 2);
            this.mImagePreviewImageView.setImageBitmap(mediaStoreMedia.mThumbnail);
            VectorApp.setSavedCameraImagePreview(mediaStoreMedia.mThumbnail);
        } else if (mediaStoreMedia.mFileUri != null) {
            displayImagePreview(null, null, mediaStoreMedia.mFileUri, 2);
        } else {
            Log.m211e(LOG_TAG, "## onClickGalleryImage(): no image to display");
        }
        this.mImagePreviewImageView.setTag(mediaStoreMedia.mFileUri);
    }

    @SuppressLint({"NewApi"})
    private void attachImageFromGallery() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        if (this.mSelectedGalleryImage != null) {
            intent.setData(this.mSelectedGalleryImage.mFileUri);
        } else {
            Uri uri = (Uri) this.mImagePreviewImageView.getTag();
            if (uri != null) {
                intent.setData(uri);
            }
        }
        intent.putExtras(bundle);
        setResult(-1, intent);
        VectorApp.setSavedCameraImagePreview(null);
        finish();
    }
}
