package com.facebook.react.modules.camera;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64OutputStream;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@ReactModule(name = "ImageStoreManager")
public class ImageStoreManager extends ReactContextBaseJavaModule {
    private static final int BUFFER_SIZE = 8192;

    private class GetBase64Task extends GuardedAsyncTask<Void, Void> {
        private final Callback mError;
        private final Callback mSuccess;
        private final String mUri;

        private GetBase64Task(ReactContext reactContext, String str, Callback callback, Callback callback2) {
            super(reactContext);
            this.mUri = str;
            this.mSuccess = callback;
            this.mError = callback2;
        }

        /* access modifiers changed from: protected */
        public void doInBackgroundGuarded(Void... voidArr) {
            InputStream openInputStream;
            Base64OutputStream base64OutputStream;
            try {
                openInputStream = ImageStoreManager.this.getReactApplicationContext().getContentResolver().openInputStream(Uri.parse(this.mUri));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                base64OutputStream = new Base64OutputStream(byteArrayOutputStream, 0);
                byte[] bArr = new byte[8192];
                while (true) {
                    try {
                        int read = openInputStream.read(bArr);
                        if (read <= -1) {
                            break;
                        }
                        base64OutputStream.write(bArr, 0, read);
                    } catch (IOException e) {
                        this.mError.invoke(e.getMessage());
                        ImageStoreManager.closeQuietly(openInputStream);
                    }
                }
                this.mSuccess.invoke(byteArrayOutputStream.toString());
                ImageStoreManager.closeQuietly(openInputStream);
                ImageStoreManager.closeQuietly(base64OutputStream);
            } catch (FileNotFoundException e2) {
                this.mError.invoke(e2.getMessage());
            } catch (Throwable th) {
                ImageStoreManager.closeQuietly(openInputStream);
                ImageStoreManager.closeQuietly(base64OutputStream);
                throw th;
            }
        }
    }

    public String getName() {
        return "ImageStoreManager";
    }

    public ImageStoreManager(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
    }

    @ReactMethod
    public void getBase64ForTag(String str, Callback callback, Callback callback2) {
        GetBase64Task getBase64Task = new GetBase64Task(getReactApplicationContext(), str, callback, callback2);
        getBase64Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* access modifiers changed from: private */
    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException unused) {
        }
    }
}
