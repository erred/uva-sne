package p006pl.droidsonroids.gif;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import p006pl.droidsonroids.gif.GifDrawableInit;
import p006pl.droidsonroids.gif.InputSource.AssetFileDescriptorSource;
import p006pl.droidsonroids.gif.InputSource.AssetSource;
import p006pl.droidsonroids.gif.InputSource.ByteArraySource;
import p006pl.droidsonroids.gif.InputSource.DirectByteBufferSource;
import p006pl.droidsonroids.gif.InputSource.FileDescriptorSource;
import p006pl.droidsonroids.gif.InputSource.FileSource;
import p006pl.droidsonroids.gif.InputSource.InputStreamSource;
import p006pl.droidsonroids.gif.InputSource.ResourcesSource;
import p006pl.droidsonroids.gif.InputSource.UriSource;
import p006pl.droidsonroids.gif.annotations.Beta;

/* renamed from: pl.droidsonroids.gif.GifDrawableInit */
public abstract class GifDrawableInit<T extends GifDrawableInit<T>> {
    private ScheduledThreadPoolExecutor mExecutor;
    private InputSource mInputSource;
    private boolean mIsRenderingTriggeredOnDraw = true;
    private GifDrawable mOldDrawable;
    private GifOptions mOptions = new GifOptions();

    /* access modifiers changed from: protected */
    public abstract T self();

    public T sampleSize(@IntRange(from = 1, mo107to = 65535) int i) {
        this.mOptions.setInSampleSize(i);
        return self();
    }

    public GifDrawable build() throws IOException {
        if (this.mInputSource != null) {
            return this.mInputSource.build(this.mOldDrawable, this.mExecutor, this.mIsRenderingTriggeredOnDraw, this.mOptions);
        }
        throw new NullPointerException("Source is not set");
    }

    public T with(GifDrawable gifDrawable) {
        this.mOldDrawable = gifDrawable;
        return self();
    }

    public T threadPoolSize(int i) {
        this.mExecutor = new ScheduledThreadPoolExecutor(i);
        return self();
    }

    public T taskExecutor(ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        this.mExecutor = scheduledThreadPoolExecutor;
        return self();
    }

    public T renderingTriggeredOnDraw(boolean z) {
        this.mIsRenderingTriggeredOnDraw = z;
        return self();
    }

    public T setRenderingTriggeredOnDraw(boolean z) {
        return renderingTriggeredOnDraw(z);
    }

    @Beta
    public T options(@Nullable GifOptions gifOptions) {
        this.mOptions.setFrom(gifOptions);
        return self();
    }

    public T from(InputStream inputStream) {
        this.mInputSource = new InputStreamSource(inputStream);
        return self();
    }

    public T from(AssetFileDescriptor assetFileDescriptor) {
        this.mInputSource = new AssetFileDescriptorSource(assetFileDescriptor);
        return self();
    }

    public T from(FileDescriptor fileDescriptor) {
        this.mInputSource = new FileDescriptorSource(fileDescriptor);
        return self();
    }

    public T from(AssetManager assetManager, String str) {
        this.mInputSource = new AssetSource(assetManager, str);
        return self();
    }

    public T from(ContentResolver contentResolver, Uri uri) {
        this.mInputSource = new UriSource(contentResolver, uri);
        return self();
    }

    public T from(File file) {
        this.mInputSource = new FileSource(file);
        return self();
    }

    public T from(String str) {
        this.mInputSource = new FileSource(str);
        return self();
    }

    public T from(byte[] bArr) {
        this.mInputSource = new ByteArraySource(bArr);
        return self();
    }

    public T from(ByteBuffer byteBuffer) {
        this.mInputSource = new DirectByteBufferSource(byteBuffer);
        return self();
    }

    public T from(Resources resources, int i) {
        this.mInputSource = new ResourcesSource(resources, i);
        return self();
    }

    public InputSource getInputSource() {
        return this.mInputSource;
    }

    public GifDrawable getOldDrawable() {
        return this.mOldDrawable;
    }

    public ScheduledThreadPoolExecutor getExecutor() {
        return this.mExecutor;
    }

    public boolean isRenderingTriggeredOnDraw() {
        return this.mIsRenderingTriggeredOnDraw;
    }

    public GifOptions getOptions() {
        return this.mOptions;
    }
}
