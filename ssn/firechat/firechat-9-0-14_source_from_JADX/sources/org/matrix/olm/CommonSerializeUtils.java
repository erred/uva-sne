package org.matrix.olm;

import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

abstract class CommonSerializeUtils {
    private static final String LOG_TAG = "CommonSerializeUtils";

    /* access modifiers changed from: protected */
    public abstract void deserialize(byte[] bArr, byte[] bArr2) throws Exception;

    /* access modifiers changed from: protected */
    public abstract byte[] serialize(byte[] bArr, StringBuffer stringBuffer);

    CommonSerializeUtils() {
    }

    /* access modifiers changed from: protected */
    public void serialize(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        byte[] randomKey = OlmUtility.getRandomKey();
        StringBuffer stringBuffer = new StringBuffer();
        byte[] serialize = serialize(randomKey, stringBuffer);
        if (serialize == null) {
            throw new OlmException(100, String.valueOf(stringBuffer));
        }
        objectOutputStream.writeObject(new String(randomKey, "UTF-8"));
        objectOutputStream.writeObject(new String(serialize, "UTF-8"));
    }

    /* access modifiers changed from: protected */
    public void deserialize(ObjectInputStream objectInputStream) throws Exception {
        objectInputStream.defaultReadObject();
        String str = (String) objectInputStream.readObject();
        String str2 = (String) objectInputStream.readObject();
        try {
            deserialize(str2.getBytes("UTF-8"), str.getBytes("UTF-8"));
            Log.d(LOG_TAG, "## deserializeObject(): success");
        } catch (Exception e) {
            throw new OlmException(101, e.getMessage());
        }
    }
}
