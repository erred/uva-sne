package com.RNFetchBlob;

public class RNFetchBlobProgressConfig {
    int count = -1;
    public boolean enable = false;
    public int interval = -1;
    long lastTick = 0;
    int tick = 0;
    public ReportType type = ReportType.Download;

    public enum ReportType {
        Upload,
        Download
    }

    RNFetchBlobProgressConfig(boolean z, int i, int i2, ReportType reportType) {
        this.enable = z;
        this.interval = i;
        this.type = reportType;
        this.count = i2;
    }

    public boolean shouldReport(float f) {
        boolean z = false;
        boolean z2 = this.count <= 0 || f <= 0.0f || Math.floor((double) (f * ((float) this.count))) > ((double) this.tick);
        if (System.currentTimeMillis() - this.lastTick > ((long) this.interval) && this.enable && z2) {
            z = true;
        }
        if (z) {
            this.tick++;
            this.lastTick = System.currentTimeMillis();
        }
        return z;
    }
}
