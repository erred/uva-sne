package org.altbeacon.beacon.distance;

import android.os.Build;
import android.os.Build.VERSION;
import org.altbeacon.beacon.logging.LogManager;

public class AndroidModel {
    private static final String TAG = "AndroidModel";
    String mBuildNumber;
    String mManufacturer;
    String mModel;
    String mVersion;

    public AndroidModel(String str, String str2, String str3, String str4) {
        this.mVersion = str;
        this.mBuildNumber = str2;
        this.mModel = str3;
        this.mManufacturer = str4;
    }

    public static AndroidModel forThisDevice() {
        return new AndroidModel(VERSION.RELEASE, Build.ID, Build.MODEL, Build.MANUFACTURER);
    }

    public String getVersion() {
        return this.mVersion;
    }

    public void setVersion(String str) {
        this.mVersion = str;
    }

    public String getBuildNumber() {
        return this.mBuildNumber;
    }

    public String getModel() {
        return this.mModel;
    }

    public String getManufacturer() {
        return this.mManufacturer;
    }

    public void setBuildNumber(String str) {
        this.mBuildNumber = str;
    }

    public void setModel(String str) {
        this.mModel = str;
    }

    public void setManufacturer(String str) {
        this.mManufacturer = str;
    }

    public int matchScore(AndroidModel androidModel) {
        int equalsIgnoreCase = this.mManufacturer.equalsIgnoreCase(androidModel.mManufacturer);
        if (equalsIgnoreCase == 1 && this.mModel.equals(androidModel.mModel)) {
            equalsIgnoreCase = 2;
        }
        if (equalsIgnoreCase == 2 && this.mBuildNumber.equals(androidModel.mBuildNumber)) {
            equalsIgnoreCase = 3;
        }
        if (equalsIgnoreCase == 3 && this.mVersion.equals(androidModel.mVersion)) {
            equalsIgnoreCase = 4;
        }
        LogManager.m260d(TAG, "Score is %s for %s compared to %s", Integer.valueOf(equalsIgnoreCase), toString(), androidModel);
        return equalsIgnoreCase;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(this.mManufacturer);
        sb.append(";");
        sb.append(this.mModel);
        sb.append(";");
        sb.append(this.mBuildNumber);
        sb.append(";");
        sb.append(this.mVersion);
        return sb.toString();
    }
}
