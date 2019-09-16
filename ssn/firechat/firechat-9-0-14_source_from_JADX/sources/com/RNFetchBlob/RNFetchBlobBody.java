package com.RNFetchBlob;

import android.util.Base64;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.apache.commons.cli.HelpFormatter;

public class RNFetchBlobBody extends RequestBody {
    File bodyCache;
    Boolean chunkedEncoding = Boolean.valueOf(false);
    long contentLength = 0;
    ReadableArray form;
    String mTaskId;
    MediaType mime;
    String rawBody;
    int reported = 0;
    InputStream requestStream;
    RequestType requestType;

    private class FormField {
        public String data;
        public String filename;
        public String mime;
        public String name;

        public FormField(ReadableMap readableMap) {
            if (readableMap.hasKey("name")) {
                this.name = readableMap.getString("name");
            }
            if (readableMap.hasKey("filename")) {
                this.filename = readableMap.getString("filename");
            }
            if (readableMap.hasKey("type")) {
                this.mime = readableMap.getString("type");
            } else {
                this.mime = this.filename == null ? "text/plain" : "application/octet-stream";
            }
            if (readableMap.hasKey("data")) {
                this.data = readableMap.getString("data");
            }
        }
    }

    public RNFetchBlobBody(String str) {
        this.mTaskId = str;
    }

    /* access modifiers changed from: 0000 */
    public RNFetchBlobBody chunkedEncoding(boolean z) {
        this.chunkedEncoding = Boolean.valueOf(z);
        return this;
    }

    /* access modifiers changed from: 0000 */
    public RNFetchBlobBody setMIME(MediaType mediaType) {
        this.mime = mediaType;
        return this;
    }

    /* access modifiers changed from: 0000 */
    public RNFetchBlobBody setRequestType(RequestType requestType2) {
        this.requestType = requestType2;
        return this;
    }

    /* access modifiers changed from: 0000 */
    public RNFetchBlobBody setBody(String str) {
        this.rawBody = str;
        if (this.rawBody == null) {
            this.rawBody = "";
            this.requestType = RequestType.AsIs;
        }
        try {
            switch (this.requestType) {
                case SingleFile:
                    this.requestStream = getReuqestStream();
                    this.contentLength = (long) this.requestStream.available();
                    break;
                case AsIs:
                    this.contentLength = (long) this.rawBody.getBytes().length;
                    this.requestStream = new ByteArrayInputStream(this.rawBody.getBytes());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            StringBuilder sb = new StringBuilder();
            sb.append("RNFetchBlob failed to create single content request body :");
            sb.append(e.getLocalizedMessage());
            sb.append("\r\n");
            RNFetchBlobUtils.emitWarningEvent(sb.toString());
        }
        return this;
    }

    /* access modifiers changed from: 0000 */
    public RNFetchBlobBody setBody(ReadableArray readableArray) {
        this.form = readableArray;
        try {
            this.bodyCache = createMultipartBodyCache();
            this.requestStream = new FileInputStream(this.bodyCache);
            this.contentLength = this.bodyCache.length();
        } catch (Exception e) {
            e.printStackTrace();
            StringBuilder sb = new StringBuilder();
            sb.append("RNFetchBlob failed to create request multipart body :");
            sb.append(e.getLocalizedMessage());
            RNFetchBlobUtils.emitWarningEvent(sb.toString());
        }
        return this;
    }

    public long contentLength() {
        if (this.chunkedEncoding.booleanValue()) {
            return -1;
        }
        return this.contentLength;
    }

    public MediaType contentType() {
        return this.mime;
    }

    public void writeTo(BufferedSink bufferedSink) {
        try {
            pipeStreamToSink(this.requestStream, bufferedSink);
        } catch (Exception e) {
            RNFetchBlobUtils.emitWarningEvent(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean clearRequestBody() {
        try {
            if (this.bodyCache != null && this.bodyCache.exists()) {
                this.bodyCache.delete();
            }
            return true;
        } catch (Exception e) {
            RNFetchBlobUtils.emitWarningEvent(e.getLocalizedMessage());
            return false;
        }
    }

    private InputStream getReuqestStream() throws Exception {
        if (this.rawBody.startsWith(RNFetchBlobConst.FILE_PREFIX)) {
            String normalizePath = RNFetchBlobFS.normalizePath(this.rawBody.substring(RNFetchBlobConst.FILE_PREFIX.length()));
            if (RNFetchBlobFS.isAsset(normalizePath)) {
                try {
                    return C0491RNFetchBlob.RCTContext.getAssets().open(normalizePath.replace(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET, ""));
                } catch (Exception e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("error when getting request stream from asset : ");
                    sb.append(e.getLocalizedMessage());
                    throw new Exception(sb.toString());
                }
            } else {
                File file = new File(RNFetchBlobFS.normalizePath(normalizePath));
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    return new FileInputStream(file);
                } catch (Exception e2) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("error when getting request stream: ");
                    sb2.append(e2.getLocalizedMessage());
                    throw new Exception(sb2.toString());
                }
            }
        } else {
            try {
                return new ByteArrayInputStream(Base64.decode(this.rawBody, 0));
            } catch (Exception e3) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("error when getting request stream: ");
                sb3.append(e3.getLocalizedMessage());
                throw new Exception(sb3.toString());
            }
        }
    }

    private File createMultipartBodyCache() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("RNFetchBlob-");
        sb.append(this.mTaskId);
        String sb2 = sb.toString();
        File createTempFile = File.createTempFile("rnfb-form-tmp", "", C0491RNFetchBlob.RCTContext.getCacheDir());
        FileOutputStream fileOutputStream = new FileOutputStream(createTempFile);
        ArrayList countFormDataLength = countFormDataLength();
        ReactApplicationContext reactApplicationContext = C0491RNFetchBlob.RCTContext;
        for (int i = 0; i < countFormDataLength.size(); i++) {
            FormField formField = (FormField) countFormDataLength.get(i);
            String str = formField.data;
            String str2 = formField.name;
            if (!(str2 == null || str == null)) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(HelpFormatter.DEFAULT_LONG_OPT_PREFIX);
                sb3.append(sb2);
                sb3.append("\r\n");
                String sb4 = sb3.toString();
                if (formField.filename != null) {
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(sb4);
                    sb5.append("Content-Disposition: form-data; name=\"");
                    sb5.append(str2);
                    sb5.append("\"; filename=\"");
                    sb5.append(formField.filename);
                    sb5.append("\"\r\n");
                    String sb6 = sb5.toString();
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append(sb6);
                    sb7.append("Content-Type: ");
                    sb7.append(formField.mime);
                    sb7.append("\r\n\r\n");
                    fileOutputStream.write(sb7.toString().getBytes());
                    if (str.startsWith(RNFetchBlobConst.FILE_PREFIX)) {
                        String normalizePath = RNFetchBlobFS.normalizePath(str.substring(RNFetchBlobConst.FILE_PREFIX.length()));
                        if (RNFetchBlobFS.isAsset(normalizePath)) {
                            try {
                                pipeStreamToFileStream(reactApplicationContext.getAssets().open(normalizePath.replace(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET, "")), fileOutputStream);
                            } catch (IOException e) {
                                StringBuilder sb8 = new StringBuilder();
                                sb8.append("Failed to create form data asset :");
                                sb8.append(normalizePath);
                                sb8.append(", ");
                                sb8.append(e.getLocalizedMessage());
                                RNFetchBlobUtils.emitWarningEvent(sb8.toString());
                            }
                        } else {
                            File file = new File(RNFetchBlobFS.normalizePath(normalizePath));
                            if (file.exists()) {
                                pipeStreamToFileStream(new FileInputStream(file), fileOutputStream);
                            } else {
                                StringBuilder sb9 = new StringBuilder();
                                sb9.append("Failed to create form data from path :");
                                sb9.append(normalizePath);
                                sb9.append(", file not exists.");
                                RNFetchBlobUtils.emitWarningEvent(sb9.toString());
                            }
                        }
                    } else {
                        fileOutputStream.write(Base64.decode(str, 0));
                    }
                } else {
                    StringBuilder sb10 = new StringBuilder();
                    sb10.append(sb4);
                    sb10.append("Content-Disposition: form-data; name=\"");
                    sb10.append(str2);
                    sb10.append("\"\r\n");
                    String sb11 = sb10.toString();
                    StringBuilder sb12 = new StringBuilder();
                    sb12.append(sb11);
                    sb12.append("Content-Type: ");
                    sb12.append(formField.mime);
                    sb12.append("\r\n\r\n");
                    fileOutputStream.write(sb12.toString().getBytes());
                    fileOutputStream.write(formField.data.getBytes());
                }
                fileOutputStream.write("\r\n".getBytes());
            }
        }
        StringBuilder sb13 = new StringBuilder();
        sb13.append(HelpFormatter.DEFAULT_LONG_OPT_PREFIX);
        sb13.append(sb2);
        sb13.append("--\r\n");
        fileOutputStream.write(sb13.toString().getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
        return createTempFile;
    }

    private void pipeStreamToSink(InputStream inputStream, BufferedSink bufferedSink) throws Exception {
        byte[] bArr = new byte[10240];
        int i = 0;
        while (true) {
            int read = inputStream.read(bArr, 0, 10240);
            if (read <= 0) {
                inputStream.close();
                return;
            } else if (read > 0) {
                bufferedSink.write(bArr, 0, read);
                i += read;
                emitUploadProgress(i);
            }
        }
    }

    private void pipeStreamToFileStream(InputStream inputStream, FileOutputStream fileOutputStream) throws IOException {
        byte[] bArr = new byte[10240];
        while (true) {
            int read = inputStream.read(bArr);
            if (read > 0) {
                fileOutputStream.write(bArr, 0, read);
            } else {
                inputStream.close();
                return;
            }
        }
    }

    private ArrayList<FormField> countFormDataLength() {
        long length;
        ArrayList<FormField> arrayList = new ArrayList<>();
        ReactApplicationContext reactApplicationContext = C0491RNFetchBlob.RCTContext;
        long j = 0;
        for (int i = 0; i < this.form.size(); i++) {
            FormField formField = new FormField(this.form.getMap(i));
            arrayList.add(formField);
            String str = formField.data;
            if (str == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("RNFetchBlob multipart request builder has found a field without `data` property, the field `");
                sb.append(formField.name);
                sb.append("` will be removed implicitly.");
                RNFetchBlobUtils.emitWarningEvent(sb.toString());
            } else {
                if (formField.filename == null) {
                    length = j + (formField.data != null ? (long) formField.data.getBytes().length : 0);
                } else if (str.startsWith(RNFetchBlobConst.FILE_PREFIX)) {
                    String normalizePath = RNFetchBlobFS.normalizePath(str.substring(RNFetchBlobConst.FILE_PREFIX.length()));
                    if (RNFetchBlobFS.isAsset(normalizePath)) {
                        try {
                            length = j + ((long) reactApplicationContext.getAssets().open(normalizePath.replace(RNFetchBlobConst.FILE_PREFIX_BUNDLE_ASSET, "")).available());
                        } catch (IOException e) {
                            RNFetchBlobUtils.emitWarningEvent(e.getLocalizedMessage());
                            length = j;
                        }
                    } else {
                        length = j + new File(RNFetchBlobFS.normalizePath(normalizePath)).length();
                    }
                } else {
                    length = j + ((long) Base64.decode(str, 0).length);
                }
                j = length;
            }
        }
        this.contentLength = j;
        return arrayList;
    }

    private void emitUploadProgress(int i) {
        RNFetchBlobProgressConfig reportUploadProgress = RNFetchBlobReq.getReportUploadProgress(this.mTaskId);
        if (reportUploadProgress != null && this.contentLength != 0 && reportUploadProgress.shouldReport(((float) i) / ((float) this.contentLength))) {
            WritableMap createMap = Arguments.createMap();
            createMap.putString("taskId", this.mTaskId);
            createMap.putString("written", String.valueOf(i));
            createMap.putString("total", String.valueOf(this.contentLength));
            ((RCTDeviceEventEmitter) C0491RNFetchBlob.RCTContext.getJSModule(RCTDeviceEventEmitter.class)).emit(RNFetchBlobConst.EVENT_UPLOAD_PROGRESS, createMap);
        }
    }
}
