package com.opengarden.firechat.matrixsdk.listeners;

import com.google.gson.JsonElement;

public interface IMXMediaDownloadListener {

    public static class DownloadStats {
        public int mBitRate;
        public String mDownloadId;
        public int mDownloadedSize;
        public int mElapsedTime;
        public int mEstimatedRemainingTime;
        public int mFileSize;
        public int mProgress;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append("mProgress : ");
            sb.append(this.mProgress);
            sb.append("%\n");
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(sb2);
            sb3.append("mDownloadedSize : ");
            sb3.append(this.mDownloadedSize);
            sb3.append(" bytes\n");
            String sb4 = sb3.toString();
            StringBuilder sb5 = new StringBuilder();
            sb5.append(sb4);
            sb5.append("mFileSize : ");
            sb5.append(this.mFileSize);
            sb5.append("bytes\n");
            String sb6 = sb5.toString();
            StringBuilder sb7 = new StringBuilder();
            sb7.append(sb6);
            sb7.append("mElapsedTime : ");
            sb7.append(this.mProgress);
            sb7.append(" seconds\n");
            String sb8 = sb7.toString();
            StringBuilder sb9 = new StringBuilder();
            sb9.append(sb8);
            sb9.append("mEstimatedRemainingTime : ");
            sb9.append(this.mEstimatedRemainingTime);
            sb9.append(" seconds\n");
            String sb10 = sb9.toString();
            StringBuilder sb11 = new StringBuilder();
            sb11.append(sb10);
            sb11.append("mBitRate : ");
            sb11.append(this.mBitRate);
            sb11.append(" KB/s\n");
            return sb11.toString();
        }
    }

    void onDownloadCancel(String str);

    void onDownloadComplete(String str);

    void onDownloadError(String str, JsonElement jsonElement);

    void onDownloadProgress(String str, DownloadStats downloadStats);

    void onDownloadStart(String str);
}
