package org.altbeacon.bluetooth;

import android.annotation.TargetApi;

public class Pdu {
    public static final byte GATT_SERVICE_UUID_PDU_TYPE = 22;
    public static final byte MANUFACTURER_DATA_PDU_TYPE = -1;
    private static final String TAG = "Pdu";
    private byte[] mBytes;
    private int mDeclaredLength;
    private int mEndIndex;
    private int mStartIndex;
    private byte mType;

    @TargetApi(9)
    public static Pdu parse(byte[] bArr, int i) {
        if (bArr.length - i >= 2) {
            byte b = bArr[i];
            if (b > 0) {
                byte b2 = bArr[i + 1];
                int i2 = i + 2;
                if (i2 < bArr.length) {
                    Pdu pdu = new Pdu();
                    pdu.mEndIndex = i + b;
                    if (pdu.mEndIndex >= bArr.length) {
                        pdu.mEndIndex = bArr.length - 1;
                    }
                    pdu.mType = b2;
                    pdu.mDeclaredLength = b;
                    pdu.mStartIndex = i2;
                    pdu.mBytes = bArr;
                    return pdu;
                }
            }
        }
        return null;
    }

    public byte getType() {
        return this.mType;
    }

    public int getDeclaredLength() {
        return this.mDeclaredLength;
    }

    public int getActualLength() {
        return (this.mEndIndex - this.mStartIndex) + 1;
    }

    public int getStartIndex() {
        return this.mStartIndex;
    }

    public int getEndIndex() {
        return this.mEndIndex;
    }
}
