package com.RNFetchBlob;

import android.content.res.AssetFileDescriptor;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.support.p000v4.app.NotificationCompat;
import android.util.Base64;
import com.RNFetchBlob.Utils.PathResolver;
import com.facebook.common.util.UriUtil;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.CharEncoding;

public class RNFetchBlobFS {
    static HashMap<String, RNFetchBlobFS> fileStreams = new HashMap<>();
    boolean append = false;
    RCTDeviceEventEmitter emitter;
    String encoding = RNFetchBlobConst.RNFB_RESPONSE_BASE64;
    ReactApplicationContext mCtx;
    OutputStream writeStreamInstance = null;

    RNFetchBlobFS(ReactApplicationContext reactApplicationContext) {
        this.mCtx = reactApplicationContext;
        this.emitter = (RCTDeviceEventEmitter) reactApplicationContext.getJSModule(RCTDeviceEventEmitter.class);
    }

    static String getExternalFilePath(ReactApplicationContext reactApplicationContext, String str, RNFetchBlobConfig rNFetchBlobConfig) {
        if (rNFetchBlobConfig.path != null) {
            return rNFetchBlobConfig.path;
        }
        if (!rNFetchBlobConfig.fileCache.booleanValue() || rNFetchBlobConfig.appendExt == null) {
            return getTmpPath(reactApplicationContext, str);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getTmpPath(reactApplicationContext, str));
        sb.append(".");
        sb.append(rNFetchBlobConfig.appendExt);
        return sb.toString();
    }

    public static void writeFile(String str, String str2, String str3, boolean z, Promise promise) {
        int i;
        try {
            File file = new File(str);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, z);
            if (str2.equalsIgnoreCase(RNFetchBlobConst.DATA_ENCODE_URI)) {
                String normalizePath = normalizePath(str3);
                File file2 = new File(normalizePath);
                if (!file2.exists()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("source file : ");
                    sb.append(normalizePath);
                    sb.append("not exists");
                    promise.reject("RNfetchBlob writeFileError", sb.toString());
                    fileOutputStream.close();
                    return;
                }
                FileInputStream fileInputStream = new FileInputStream(file2);
                byte[] bArr = new byte[10240];
                i = 0;
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read <= 0) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                    i += read;
                }
                fileInputStream.close();
            } else {
                byte[] stringToBytes = stringToBytes(str3, str2);
                fileOutputStream.write(stringToBytes);
                i = stringToBytes.length;
            }
            fileOutputStream.close();
            promise.resolve(Integer.valueOf(i));
        } catch (Exception e) {
            promise.reject("RNFetchBlob writeFileError", e.getLocalizedMessage());
        }
    }

    public static void writeFile(String str, ReadableArray readableArray, boolean z, Promise promise) {
        try {
            File file = new File(str);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, z);
            byte[] bArr = new byte[readableArray.size()];
            for (int i = 0; i < readableArray.size(); i++) {
                bArr[i] = (byte) readableArray.getInt(i);
            }
            fileOutputStream.write(bArr);
            fileOutputStream.close();
            promise.resolve(Integer.valueOf(readableArray.size()));
        } catch (Exception e) {
            promise.reject("RNFetchBlob writeFileError", e.getLocalizedMessage());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0082 A[Catch:{ Exception -> 0x003c }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a1 A[Catch:{ Exception -> 0x003c }] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00af A[Catch:{ Exception -> 0x003c }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b2 A[Catch:{ Exception -> 0x003c }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00bb A[Catch:{ Exception -> 0x003c }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00ce A[Catch:{ Exception -> 0x003c }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void readFile(java.lang.String r5, java.lang.String r6, com.facebook.react.bridge.Promise r7) {
        /*
            java.lang.String r0 = normalizePath(r5)
            if (r0 == 0) goto L_0x0007
            r5 = r0
        L_0x0007:
            r1 = 0
            if (r0 == 0) goto L_0x003f
            java.lang.String r2 = "bundle-assets://"
            boolean r2 = r0.startsWith(r2)     // Catch:{ Exception -> 0x003c }
            if (r2 == 0) goto L_0x003f
            java.lang.String r0 = "bundle-assets://"
            java.lang.String r2 = ""
            java.lang.String r5 = r5.replace(r0, r2)     // Catch:{ Exception -> 0x003c }
            com.facebook.react.bridge.ReactApplicationContext r0 = com.RNFetchBlob.C0491RNFetchBlob.RCTContext     // Catch:{ Exception -> 0x003c }
            android.content.res.AssetManager r0 = r0.getAssets()     // Catch:{ Exception -> 0x003c }
            android.content.res.AssetFileDescriptor r0 = r0.openFd(r5)     // Catch:{ Exception -> 0x003c }
            long r2 = r0.getLength()     // Catch:{ Exception -> 0x003c }
            int r0 = (int) r2     // Catch:{ Exception -> 0x003c }
            byte[] r2 = new byte[r0]     // Catch:{ Exception -> 0x003c }
            com.facebook.react.bridge.ReactApplicationContext r3 = com.RNFetchBlob.C0491RNFetchBlob.RCTContext     // Catch:{ Exception -> 0x003c }
            android.content.res.AssetManager r3 = r3.getAssets()     // Catch:{ Exception -> 0x003c }
            java.io.InputStream r5 = r3.open(r5)     // Catch:{ Exception -> 0x003c }
            r5.read(r2, r1, r0)     // Catch:{ Exception -> 0x003c }
            r5.close()     // Catch:{ Exception -> 0x003c }
            goto L_0x0073
        L_0x003c:
            r5 = move-exception
            goto L_0x00dd
        L_0x003f:
            if (r0 != 0) goto L_0x005c
            com.facebook.react.bridge.ReactApplicationContext r0 = com.RNFetchBlob.C0491RNFetchBlob.RCTContext     // Catch:{ Exception -> 0x003c }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ Exception -> 0x003c }
            android.net.Uri r5 = android.net.Uri.parse(r5)     // Catch:{ Exception -> 0x003c }
            java.io.InputStream r5 = r0.openInputStream(r5)     // Catch:{ Exception -> 0x003c }
            int r0 = r5.available()     // Catch:{ Exception -> 0x003c }
            byte[] r2 = new byte[r0]     // Catch:{ Exception -> 0x003c }
            r5.read(r2)     // Catch:{ Exception -> 0x003c }
            r5.close()     // Catch:{ Exception -> 0x003c }
            goto L_0x0073
        L_0x005c:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x003c }
            r0.<init>(r5)     // Catch:{ Exception -> 0x003c }
            long r2 = r0.length()     // Catch:{ Exception -> 0x003c }
            int r5 = (int) r2     // Catch:{ Exception -> 0x003c }
            byte[] r2 = new byte[r5]     // Catch:{ Exception -> 0x003c }
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ Exception -> 0x003c }
            r5.<init>(r0)     // Catch:{ Exception -> 0x003c }
            r5.read(r2)     // Catch:{ Exception -> 0x003c }
            r5.close()     // Catch:{ Exception -> 0x003c }
        L_0x0073:
            java.lang.String r5 = r6.toLowerCase()     // Catch:{ Exception -> 0x003c }
            r6 = -1
            int r0 = r5.hashCode()     // Catch:{ Exception -> 0x003c }
            r3 = -1396204209(0xffffffffacc79d4f, float:-5.673385E-12)
            r4 = 2
            if (r0 == r3) goto L_0x00a1
            r3 = 3600241(0x36ef71, float:5.045012E-39)
            if (r0 == r3) goto L_0x0097
            r3 = 93106001(0x58caf51, float:1.3229938E-35)
            if (r0 == r3) goto L_0x008d
            goto L_0x00ab
        L_0x008d:
            java.lang.String r0 = "ascii"
            boolean r5 = r5.equals(r0)     // Catch:{ Exception -> 0x003c }
            if (r5 == 0) goto L_0x00ab
            r5 = 1
            goto L_0x00ac
        L_0x0097:
            java.lang.String r0 = "utf8"
            boolean r5 = r5.equals(r0)     // Catch:{ Exception -> 0x003c }
            if (r5 == 0) goto L_0x00ab
            r5 = 2
            goto L_0x00ac
        L_0x00a1:
            java.lang.String r0 = "base64"
            boolean r5 = r5.equals(r0)     // Catch:{ Exception -> 0x003c }
            if (r5 == 0) goto L_0x00ab
            r5 = 0
            goto L_0x00ac
        L_0x00ab:
            r5 = -1
        L_0x00ac:
            switch(r5) {
                case 0: goto L_0x00ce;
                case 1: goto L_0x00bb;
                case 2: goto L_0x00b2;
                default: goto L_0x00af;
            }     // Catch:{ Exception -> 0x003c }
        L_0x00af:
            java.lang.String r5 = new java.lang.String     // Catch:{ Exception -> 0x003c }
            goto L_0x00d6
        L_0x00b2:
            java.lang.String r5 = new java.lang.String     // Catch:{ Exception -> 0x003c }
            r5.<init>(r2)     // Catch:{ Exception -> 0x003c }
            r7.resolve(r5)     // Catch:{ Exception -> 0x003c }
            goto L_0x00e6
        L_0x00bb:
            com.facebook.react.bridge.WritableArray r5 = com.facebook.react.bridge.Arguments.createArray()     // Catch:{ Exception -> 0x003c }
            int r6 = r2.length     // Catch:{ Exception -> 0x003c }
        L_0x00c0:
            if (r1 >= r6) goto L_0x00ca
            byte r0 = r2[r1]     // Catch:{ Exception -> 0x003c }
            r5.pushInt(r0)     // Catch:{ Exception -> 0x003c }
            int r1 = r1 + 1
            goto L_0x00c0
        L_0x00ca:
            r7.resolve(r5)     // Catch:{ Exception -> 0x003c }
            goto L_0x00e6
        L_0x00ce:
            java.lang.String r5 = android.util.Base64.encodeToString(r2, r4)     // Catch:{ Exception -> 0x003c }
            r7.resolve(r5)     // Catch:{ Exception -> 0x003c }
            goto L_0x00e6
        L_0x00d6:
            r5.<init>(r2)     // Catch:{ Exception -> 0x003c }
            r7.resolve(r5)     // Catch:{ Exception -> 0x003c }
            goto L_0x00e6
        L_0x00dd:
            java.lang.String r6 = "ReadFile Error"
            java.lang.String r5 = r5.getLocalizedMessage()
            r7.reject(r6, r5)
        L_0x00e6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.RNFetchBlob.RNFetchBlobFS.readFile(java.lang.String, java.lang.String, com.facebook.react.bridge.Promise):void");
    }

    public static Map<String, Object> getSystemfolders(ReactApplicationContext reactApplicationContext) {
        HashMap hashMap = new HashMap();
        hashMap.put("DocumentDir", reactApplicationContext.getFilesDir().getAbsolutePath());
        hashMap.put("CacheDir", reactApplicationContext.getCacheDir().getAbsolutePath());
        hashMap.put("DCIMDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        hashMap.put("PictureDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        hashMap.put("MusicDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        hashMap.put("DownloadDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        hashMap.put("MovieDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath());
        hashMap.put("RingtoneDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).getAbsolutePath());
        if (Environment.getExternalStorageState().equals("mounted")) {
            hashMap.put("SDCardDir", Environment.getExternalStorageDirectory().getAbsolutePath());
            hashMap.put("SDCardApplicationDir", reactApplicationContext.getExternalFilesDir(null).getParentFile().getAbsolutePath());
        }
        hashMap.put("MainBundleDir", reactApplicationContext.getApplicationInfo().dataDir);
        return hashMap;
    }

    public static String getTmpPath(ReactApplicationContext reactApplicationContext, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(C0491RNFetchBlob.RCTContext.getFilesDir());
        sb.append("/RNFetchBlobTmp_");
        sb.append(str);
        return sb.toString();
    }

    public void readStream(String str, String str2, int i, int i2, String str3) {
        InputStream inputStream;
        String normalizePath = normalizePath(str);
        if (normalizePath != null) {
            str = normalizePath;
        }
        try {
            int i3 = str2.equalsIgnoreCase(RNFetchBlobConst.RNFB_RESPONSE_BASE64) ? 4095 : 4096;
            if (i <= 0) {
                i = i3;
            }
            if (normalizePath != null && str.startsWith(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET)) {
                inputStream = C0491RNFetchBlob.RCTContext.getAssets().open(str.replace(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET, ""));
            } else if (normalizePath == null) {
                inputStream = C0491RNFetchBlob.RCTContext.getContentResolver().openInputStream(Uri.parse(str));
            } else {
                inputStream = new FileInputStream(new File(str));
            }
            byte[] bArr = new byte[i];
            boolean z = false;
            if (str2.equalsIgnoreCase(RNFetchBlobConst.RNFB_RESPONSE_UTF8)) {
                CharsetEncoder newEncoder = Charset.forName("UTF-8").newEncoder();
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    newEncoder.encode(ByteBuffer.wrap(bArr).asCharBuffer());
                    emitStreamEvent(str3, "data", new String(bArr, 0, read));
                    if (i2 > 0) {
                        SystemClock.sleep((long) i2);
                    }
                }
            } else if (str2.equalsIgnoreCase("ascii")) {
                while (true) {
                    int read2 = inputStream.read(bArr);
                    if (read2 == -1) {
                        break;
                    }
                    WritableArray createArray = Arguments.createArray();
                    for (int i4 = 0; i4 < read2; i4++) {
                        createArray.pushInt(bArr[i4]);
                    }
                    emitStreamEvent(str3, "data", createArray);
                    if (i2 > 0) {
                        SystemClock.sleep((long) i2);
                    }
                }
            } else if (str2.equalsIgnoreCase(RNFetchBlobConst.RNFB_RESPONSE_BASE64)) {
                while (true) {
                    int read3 = inputStream.read(bArr);
                    if (read3 == -1) {
                        break;
                    }
                    if (read3 < i) {
                        byte[] bArr2 = new byte[read3];
                        for (int i5 = 0; i5 < read3; i5++) {
                            bArr2[i5] = bArr[i5];
                        }
                        emitStreamEvent(str3, "data", Base64.encodeToString(bArr2, 2));
                    } else {
                        emitStreamEvent(str3, "data", Base64.encodeToString(bArr, 2));
                    }
                    if (i2 > 0) {
                        SystemClock.sleep((long) i2);
                    }
                }
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("unrecognized encoding `");
                sb.append(str2);
                sb.append("`");
                emitStreamEvent(str3, "error", sb.toString());
                z = true;
            }
            if (!z) {
                emitStreamEvent(str3, "end", "");
            }
            inputStream.close();
        } catch (Exception e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Failed to convert data to ");
            sb2.append(str2);
            sb2.append(" encoded string, this might due to the source data is not able to convert using this encoding.");
            emitStreamEvent(str3, "warn", sb2.toString());
            e.printStackTrace();
        }
    }

    public void writeStream(String str, String str2, boolean z, Callback callback) {
        File file = new File(str);
        if (!file.exists() || file.isDirectory()) {
            StringBuilder sb = new StringBuilder();
            sb.append("write stream error: target path `");
            sb.append(str);
            sb.append("` may not exists or it's a folder");
            callback.invoke(sb.toString());
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str, z);
            this.encoding = str2;
            this.append = z;
            String uuid = UUID.randomUUID().toString();
            fileStreams.put(uuid, this);
            this.writeStreamInstance = fileOutputStream;
            callback.invoke(null, uuid);
        } catch (Exception e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("write stream error: failed to create write stream at path `");
            sb2.append(str);
            sb2.append("` ");
            sb2.append(e.getLocalizedMessage());
            callback.invoke(sb2.toString());
        }
    }

    static void writeChunk(String str, String str2, Callback callback) {
        RNFetchBlobFS rNFetchBlobFS = (RNFetchBlobFS) fileStreams.get(str);
        try {
            rNFetchBlobFS.writeStreamInstance.write(stringToBytes(str2, rNFetchBlobFS.encoding));
            callback.invoke(new Object[0]);
        } catch (Exception e) {
            callback.invoke(e.getLocalizedMessage());
        }
    }

    static void writeArrayChunk(String str, ReadableArray readableArray, Callback callback) {
        try {
            OutputStream outputStream = ((RNFetchBlobFS) fileStreams.get(str)).writeStreamInstance;
            byte[] bArr = new byte[readableArray.size()];
            for (int i = 0; i < readableArray.size(); i++) {
                bArr[i] = (byte) readableArray.getInt(i);
            }
            outputStream.write(bArr);
            callback.invoke(new Object[0]);
        } catch (Exception e) {
            callback.invoke(e.getLocalizedMessage());
        }
    }

    static void closeStream(String str, Callback callback) {
        try {
            OutputStream outputStream = ((RNFetchBlobFS) fileStreams.get(str)).writeStreamInstance;
            fileStreams.remove(str);
            outputStream.close();
            callback.invoke(new Object[0]);
        } catch (Exception e) {
            callback.invoke(e.getLocalizedMessage());
        }
    }

    static void unlink(String str, Callback callback) {
        try {
            deleteRecursive(new File(str));
            callback.invoke(null, Boolean.valueOf(true));
        } catch (Exception e) {
            if (e != null) {
                callback.invoke(e.getLocalizedMessage(), Boolean.valueOf(false));
            }
        }
    }

    static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File deleteRecursive : file.listFiles()) {
                deleteRecursive(deleteRecursive);
            }
        }
        file.delete();
    }

    static void mkdir(String str, Callback callback) {
        File file = new File(str);
        if (file.exists()) {
            StringBuilder sb = new StringBuilder();
            sb.append("mkdir error: failed to create folder at `");
            sb.append(str);
            sb.append("` folder already exists");
            callback.invoke(sb.toString());
            return;
        }
        file.mkdirs();
        callback.invoke(new Object[0]);
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x00a3 A[SYNTHETIC, Splitter:B:47:0x00a3] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00ab A[Catch:{ Exception -> 0x00a7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00c3 A[SYNTHETIC, Splitter:B:59:0x00c3] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00cb A[Catch:{ Exception -> 0x00c7 }] */
    /* renamed from: cp */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void m19cp(java.lang.String r5, java.lang.String r6, com.facebook.react.bridge.Callback r7) {
        /*
            java.lang.String r5 = normalizePath(r5)
            r0 = 0
            r1 = 1
            r2 = 0
            boolean r3 = isPathExists(r5)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            if (r3 != 0) goto L_0x003d
            java.lang.Object[] r6 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            r3.<init>()     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            java.lang.String r4 = "cp error: source file at path`"
            r3.append(r4)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            r3.append(r5)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            java.lang.String r5 = "` not exists"
            r3.append(r5)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            java.lang.String r5 = r3.toString()     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            r6[r2] = r5     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            r7.invoke(r6)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x0030 }
            r7.invoke(r5)     // Catch:{ Exception -> 0x0030 }
            goto L_0x003c
        L_0x0030:
            r5 = move-exception
            java.lang.Object[] r6 = new java.lang.Object[r1]
            java.lang.String r5 = r5.getLocalizedMessage()
            r6[r2] = r5
            r7.invoke(r6)
        L_0x003c:
            return
        L_0x003d:
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            r3.<init>(r6)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            boolean r3 = r3.exists()     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            if (r3 != 0) goto L_0x0050
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            r3.<init>(r6)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            r3.createNewFile()     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
        L_0x0050:
            java.io.InputStream r5 = inputStreamFromPath(r5)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x008c, all -> 0x0087 }
            r3.<init>(r6)     // Catch:{ Exception -> 0x008c, all -> 0x0087 }
            r6 = 10240(0x2800, float:1.4349E-41)
            byte[] r6 = new byte[r6]     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
        L_0x005d:
            int r0 = r5.read(r6)     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
            if (r0 <= 0) goto L_0x0067
            r3.write(r6, r2, r0)     // Catch:{ Exception -> 0x0085, all -> 0x0083 }
            goto L_0x005d
        L_0x0067:
            if (r5 == 0) goto L_0x006f
            r5.close()     // Catch:{ Exception -> 0x006d }
            goto L_0x006f
        L_0x006d:
            r5 = move-exception
            goto L_0x007a
        L_0x006f:
            if (r3 == 0) goto L_0x0074
            r3.close()     // Catch:{ Exception -> 0x006d }
        L_0x0074:
            java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x006d }
            r7.invoke(r5)     // Catch:{ Exception -> 0x006d }
            goto L_0x00bf
        L_0x007a:
            java.lang.Object[] r6 = new java.lang.Object[r1]
            java.lang.String r5 = r5.getLocalizedMessage()
            r6[r2] = r5
            goto L_0x00bc
        L_0x0083:
            r6 = move-exception
            goto L_0x0089
        L_0x0085:
            r6 = move-exception
            goto L_0x008e
        L_0x0087:
            r6 = move-exception
            r3 = r0
        L_0x0089:
            r0 = r5
            r5 = r6
            goto L_0x00c1
        L_0x008c:
            r6 = move-exception
            r3 = r0
        L_0x008e:
            r0 = r5
            r5 = r6
            goto L_0x0096
        L_0x0091:
            r5 = move-exception
            r3 = r0
            goto L_0x00c1
        L_0x0094:
            r5 = move-exception
            r3 = r0
        L_0x0096:
            java.lang.Object[] r6 = new java.lang.Object[r1]     // Catch:{ all -> 0x00c0 }
            java.lang.String r5 = r5.getLocalizedMessage()     // Catch:{ all -> 0x00c0 }
            r6[r2] = r5     // Catch:{ all -> 0x00c0 }
            r7.invoke(r6)     // Catch:{ all -> 0x00c0 }
            if (r0 == 0) goto L_0x00a9
            r0.close()     // Catch:{ Exception -> 0x00a7 }
            goto L_0x00a9
        L_0x00a7:
            r5 = move-exception
            goto L_0x00b4
        L_0x00a9:
            if (r3 == 0) goto L_0x00ae
            r3.close()     // Catch:{ Exception -> 0x00a7 }
        L_0x00ae:
            java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x00a7 }
            r7.invoke(r5)     // Catch:{ Exception -> 0x00a7 }
            goto L_0x00bf
        L_0x00b4:
            java.lang.Object[] r6 = new java.lang.Object[r1]
            java.lang.String r5 = r5.getLocalizedMessage()
            r6[r2] = r5
        L_0x00bc:
            r7.invoke(r6)
        L_0x00bf:
            return
        L_0x00c0:
            r5 = move-exception
        L_0x00c1:
            if (r0 == 0) goto L_0x00c9
            r0.close()     // Catch:{ Exception -> 0x00c7 }
            goto L_0x00c9
        L_0x00c7:
            r6 = move-exception
            goto L_0x00d4
        L_0x00c9:
            if (r3 == 0) goto L_0x00ce
            r3.close()     // Catch:{ Exception -> 0x00c7 }
        L_0x00ce:
            java.lang.Object[] r6 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x00c7 }
            r7.invoke(r6)     // Catch:{ Exception -> 0x00c7 }
            goto L_0x00df
        L_0x00d4:
            java.lang.Object[] r0 = new java.lang.Object[r1]
            java.lang.String r6 = r6.getLocalizedMessage()
            r0[r2] = r6
            r7.invoke(r0)
        L_0x00df:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.RNFetchBlob.RNFetchBlobFS.m19cp(java.lang.String, java.lang.String, com.facebook.react.bridge.Callback):void");
    }

    /* renamed from: mv */
    static void m22mv(String str, String str2, Callback callback) {
        File file = new File(str);
        if (!file.exists()) {
            StringBuilder sb = new StringBuilder();
            sb.append("mv error: source file at path `");
            sb.append(str);
            sb.append("` does not exists");
            callback.invoke(sb.toString());
            return;
        }
        file.renameTo(new File(str2));
        callback.invoke(new Object[0]);
    }

    static void exists(String str, Callback callback) {
        if (isAsset(str)) {
            try {
                C0491RNFetchBlob.RCTContext.getAssets().openFd(str.replace(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET, ""));
                callback.invoke(Boolean.valueOf(true), Boolean.valueOf(false));
            } catch (IOException unused) {
                callback.invoke(Boolean.valueOf(false), Boolean.valueOf(false));
            }
        } else {
            String normalizePath = normalizePath(str);
            callback.invoke(Boolean.valueOf(new File(normalizePath).exists()), Boolean.valueOf(new File(normalizePath).isDirectory()));
        }
    }

    /* renamed from: ls */
    static void m21ls(String str, Callback callback) {
        String normalizePath = normalizePath(str);
        File file = new File(normalizePath);
        if (!file.exists() || !file.isDirectory()) {
            StringBuilder sb = new StringBuilder();
            sb.append("ls error: failed to list path `");
            sb.append(normalizePath);
            sb.append("` for it is not exist or it is not a folder");
            callback.invoke(sb.toString());
            return;
        }
        String[] list = new File(normalizePath).list();
        WritableArray createArray = Arguments.createArray();
        for (String pushString : list) {
            createArray.pushString(pushString);
        }
        callback.invoke(null, createArray);
    }

    public static void slice(String str, String str2, int i, int i2, String str3, Promise promise) {
        String str4 = str2;
        Promise promise2 = promise;
        try {
            String normalizePath = normalizePath(str);
            File file = new File(normalizePath);
            if (!file.exists()) {
                StringBuilder sb = new StringBuilder();
                sb.append("source file : ");
                sb.append(normalizePath);
                sb.append(" not exists");
                promise2.reject("RNFetchBlob.slice error", sb.toString());
                return;
            }
            long j = (long) i;
            long min = Math.min(file.length(), (long) i2) - j;
            FileInputStream fileInputStream = new FileInputStream(new File(normalizePath));
            FileOutputStream fileOutputStream = new FileOutputStream(new File(str4));
            fileInputStream.skip(j);
            int i3 = 10240;
            byte[] bArr = new byte[10240];
            long j2 = 0;
            while (true) {
                if (j2 >= min) {
                    break;
                }
                long read = (long) fileInputStream.read(bArr, 0, i3);
                byte[] bArr2 = bArr;
                long j3 = min - j2;
                if (read <= 0) {
                    break;
                }
                bArr = bArr2;
                fileOutputStream.write(bArr, 0, (int) Math.min(j3, read));
                j2 += read;
                i3 = 10240;
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            promise2.resolve(str4);
        } catch (Exception e) {
            Exception exc = e;
            exc.printStackTrace();
            promise2.reject(exc.getLocalizedMessage());
        }
    }

    static void lstat(String str, final Callback callback) {
        String normalizePath = normalizePath(str);
        new AsyncTask<String, Integer, Integer>() {
            /* access modifiers changed from: protected */
            public Integer doInBackground(String... strArr) {
                String[] list;
                WritableArray createArray = Arguments.createArray();
                if (strArr[0] == null) {
                    callback.invoke("lstat error: the path specified for lstat is either `null` or `undefined`.");
                    return Integer.valueOf(0);
                }
                File file = new File(strArr[0]);
                if (!file.exists()) {
                    Callback callback = callback;
                    StringBuilder sb = new StringBuilder();
                    sb.append("lstat error: failed to list path `");
                    sb.append(strArr[0]);
                    sb.append("` for it is not exist or it is not a folder");
                    callback.invoke(sb.toString());
                    return Integer.valueOf(0);
                }
                if (file.isDirectory()) {
                    for (String str : file.list()) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(file.getPath());
                        sb2.append("/");
                        sb2.append(str);
                        createArray.pushMap(RNFetchBlobFS.statFile(sb2.toString()));
                    }
                } else {
                    createArray.pushMap(RNFetchBlobFS.statFile(file.getAbsolutePath()));
                }
                callback.invoke(null, createArray);
                return Integer.valueOf(0);
            }
        }.execute(new String[]{normalizePath});
    }

    static void stat(String str, Callback callback) {
        try {
            String normalizePath = normalizePath(str);
            WritableMap statFile = statFile(normalizePath);
            if (statFile == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("stat error: failed to list path `");
                sb.append(normalizePath);
                sb.append("` for it is not exist or it is not a folder");
                callback.invoke(sb.toString(), null);
                return;
            }
            callback.invoke(null, statFile);
        } catch (Exception e) {
            callback.invoke(e.getLocalizedMessage());
        }
    }

    static WritableMap statFile(String str) {
        try {
            String normalizePath = normalizePath(str);
            WritableMap createMap = Arguments.createMap();
            if (isAsset(normalizePath)) {
                String replace = normalizePath.replace(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET, "");
                AssetFileDescriptor openFd = C0491RNFetchBlob.RCTContext.getAssets().openFd(replace);
                createMap.putString("filename", replace);
                createMap.putString(RNFetchBlobConst.RNFB_RESPONSE_PATH, normalizePath);
                createMap.putString("type", UriUtil.LOCAL_ASSET_SCHEME);
                createMap.putString("size", String.valueOf(openFd.getLength()));
                createMap.putInt("lastModified", 0);
            } else {
                File file = new File(normalizePath);
                if (!file.exists()) {
                    return null;
                }
                createMap.putString("filename", file.getName());
                createMap.putString(RNFetchBlobConst.RNFB_RESPONSE_PATH, file.getPath());
                createMap.putString("type", file.isDirectory() ? "directory" : UriUtil.LOCAL_FILE_SCHEME);
                createMap.putString("size", String.valueOf(file.length()));
                createMap.putString("lastModified", String.valueOf(file.lastModified()));
            }
            return createMap;
        } catch (Exception unused) {
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void scanFile(String[] strArr, String[] strArr2, final Callback callback) {
        try {
            MediaScannerConnection.scanFile(this.mCtx, strArr, strArr2, new OnScanCompletedListener() {
                public void onScanCompleted(String str, Uri uri) {
                    callback.invoke(null, Boolean.valueOf(true));
                }
            });
        } catch (Exception e) {
            callback.invoke(e.getLocalizedMessage(), null);
        }
    }

    static void createFile(String str, String str2, String str3, Callback callback) {
        try {
            File file = new File(str);
            boolean createNewFile = file.createNewFile();
            if (str3.equals(RNFetchBlobConst.DATA_ENCODE_URI)) {
                File file2 = new File(str2.replace(RNFetchBlobConst.FILE_PREFIX, ""));
                if (!file2.exists()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("source file : ");
                    sb.append(str2);
                    sb.append("not exists");
                    callback.invoke("RNfetchBlob writeFileError", sb.toString());
                    return;
                }
                FileInputStream fileInputStream = new FileInputStream(file2);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[10240];
                for (int read = fileInputStream.read(bArr); read > 0; read = fileInputStream.read(bArr)) {
                    fileOutputStream.write(bArr, 0, read);
                }
                fileInputStream.close();
                fileOutputStream.close();
            } else if (!createNewFile) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("create file error: failed to create file at path `");
                sb2.append(str);
                sb2.append("` for its parent path may not exists, or the file already exists. If you intended to overwrite the existing file use fs.writeFile instead.");
                callback.invoke(sb2.toString());
                return;
            } else {
                new FileOutputStream(file).write(stringToBytes(str2, str3));
            }
            callback.invoke(null, str);
        } catch (Exception e) {
            callback.invoke(e.getLocalizedMessage());
        }
    }

    static void createFileASCII(String str, ReadableArray readableArray, Callback callback) {
        try {
            File file = new File(str);
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                sb.append("create file error: failed to create file at path `");
                sb.append(str);
                sb.append("`, file already exists.");
                callback.invoke(sb.toString());
            } else if (!file.createNewFile()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("create file error: failed to create file at path `");
                sb2.append(str);
                sb2.append("` for its parent path may not exists");
                callback.invoke(sb2.toString());
            } else {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[readableArray.size()];
                for (int i = 0; i < readableArray.size(); i++) {
                    bArr[i] = (byte) readableArray.getInt(i);
                }
                fileOutputStream.write(bArr);
                callback.invoke(null, str);
            }
        } catch (Exception e) {
            callback.invoke(e.getLocalizedMessage());
        }
    }

    /* renamed from: df */
    static void m20df(Callback callback) {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        WritableMap createMap = Arguments.createMap();
        if (VERSION.SDK_INT >= 18) {
            createMap.putString("internal_free", String.valueOf(statFs.getFreeBytes()));
            createMap.putString("internal_total", String.valueOf(statFs.getTotalBytes()));
            StatFs statFs2 = new StatFs(Environment.getExternalStorageDirectory().getPath());
            createMap.putString("external_free", String.valueOf(statFs2.getFreeBytes()));
            createMap.putString("external_total", String.valueOf(statFs2.getTotalBytes()));
        }
        callback.invoke(null, createMap);
    }

    static void removeSession(ReadableArray readableArray, final Callback callback) {
        new AsyncTask<ReadableArray, Integer, Integer>() {
            /* access modifiers changed from: protected */
            public Integer doInBackground(ReadableArray... readableArrayArr) {
                int i = 0;
                while (i < readableArrayArr[0].size()) {
                    try {
                        File file = new File(readableArrayArr[0].getString(i));
                        if (file.exists()) {
                            file.delete();
                        }
                        i++;
                    } catch (Exception e) {
                        callback.invoke(e.getLocalizedMessage());
                    }
                }
                callback.invoke(null, Boolean.valueOf(true));
                return Integer.valueOf(readableArrayArr[0].size());
            }
        }.execute(new ReadableArray[]{readableArray});
    }

    private static byte[] stringToBytes(String str, String str2) {
        if (str2.equalsIgnoreCase("ascii")) {
            return str.getBytes(Charset.forName(CharEncoding.US_ASCII));
        }
        if (str2.toLowerCase().contains(RNFetchBlobConst.RNFB_RESPONSE_BASE64)) {
            return Base64.decode(str, 2);
        }
        if (str2.equalsIgnoreCase(RNFetchBlobConst.RNFB_RESPONSE_UTF8)) {
            return str.getBytes(Charset.forName("UTF-8"));
        }
        return str.getBytes(Charset.forName(CharEncoding.US_ASCII));
    }

    private void emitStreamEvent(String str, String str2, String str3) {
        WritableMap createMap = Arguments.createMap();
        createMap.putString(NotificationCompat.CATEGORY_EVENT, str2);
        createMap.putString("detail", str3);
        this.emitter.emit(str, createMap);
    }

    private void emitStreamEvent(String str, String str2, WritableArray writableArray) {
        WritableMap createMap = Arguments.createMap();
        createMap.putString(NotificationCompat.CATEGORY_EVENT, str2);
        createMap.putArray("detail", writableArray);
        this.emitter.emit(str, createMap);
    }

    /* access modifiers changed from: 0000 */
    public void emitFSData(String str, String str2, String str3) {
        WritableMap createMap = Arguments.createMap();
        createMap.putString(NotificationCompat.CATEGORY_EVENT, str2);
        createMap.putString("detail", str3);
        RCTDeviceEventEmitter rCTDeviceEventEmitter = this.emitter;
        StringBuilder sb = new StringBuilder();
        sb.append("RNFetchBlobStream");
        sb.append(str);
        rCTDeviceEventEmitter.emit(sb.toString(), createMap);
    }

    static InputStream inputStreamFromPath(String str) throws IOException {
        if (str.startsWith(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET)) {
            return C0491RNFetchBlob.RCTContext.getAssets().open(str.replace(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET, ""));
        }
        return new FileInputStream(new File(str));
    }

    static boolean isPathExists(String str) {
        if (!str.startsWith(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET)) {
            return new File(str).exists();
        }
        try {
            C0491RNFetchBlob.RCTContext.getAssets().open(str.replace(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET, ""));
            return true;
        } catch (IOException unused) {
            return false;
        }
    }

    static boolean isAsset(String str) {
        if (str != null) {
            return str.startsWith(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET);
        }
        return false;
    }

    static String normalizePath(String str) {
        if (str == null) {
            return null;
        }
        if (!str.matches("\\w+\\:.*")) {
            return str;
        }
        if (str.startsWith("file://")) {
            return str.replace("file://", "");
        }
        Uri parse = Uri.parse(str);
        if (str.startsWith(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET)) {
            return str;
        }
        return PathResolver.getRealPathFromURI(C0491RNFetchBlob.RCTContext, parse);
    }
}
