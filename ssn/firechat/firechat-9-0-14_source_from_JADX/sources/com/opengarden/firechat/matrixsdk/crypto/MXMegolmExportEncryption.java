package com.opengarden.firechat.matrixsdk.crypto;

import android.text.TextUtils;
import android.util.Base64;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import kotlin.jvm.internal.ByteCompanionObject;
import org.altbeacon.bluetooth.Pdu;
import org.apache.commons.lang3.StringUtils;

public class MXMegolmExportEncryption {
    public static final int DEFAULT_ITERATION_COUNT = 500000;
    private static final String HEADER_LINE = "-----BEGIN MEGOLM SESSION DATA-----";
    private static final int LINE_LENGTH = 96;
    private static final String LOG_TAG = "MXMegolmExportEncryption";
    private static final String TRAILER_LINE = "-----END MEGOLM SESSION DATA-----";

    private static int byteToInt(byte b) {
        return b & Pdu.MANUFACTURER_DATA_PDU_TYPE;
    }

    private static byte[] getAesKey(byte[] bArr) {
        return Arrays.copyOfRange(bArr, 0, 32);
    }

    private static byte[] getHmacKey(byte[] bArr) {
        return Arrays.copyOfRange(bArr, 32, bArr.length);
    }

    public static String decryptMegolmKeyFile(byte[] bArr, String str) throws Exception {
        byte[] unpackMegolmKeyFile = unpackMegolmKeyFile(bArr);
        if (unpackMegolmKeyFile == null || unpackMegolmKeyFile.length == 0) {
            Log.m211e(LOG_TAG, "## decryptMegolmKeyFile() : Invalid file: too short");
            throw new Exception("Invalid file: too short");
        } else if (unpackMegolmKeyFile[0] != 1) {
            Log.m211e(LOG_TAG, "## decryptMegolmKeyFile() : Invalid file: too short");
            throw new Exception("Unsupported version");
        } else {
            int length = unpackMegolmKeyFile.length - 69;
            if (length < 0) {
                throw new Exception("Invalid file: too short");
            } else if (TextUtils.isEmpty(str)) {
                throw new Exception("Empty password is not supported");
            } else {
                byte[] copyOfRange = Arrays.copyOfRange(unpackMegolmKeyFile, 1, 17);
                byte[] copyOfRange2 = Arrays.copyOfRange(unpackMegolmKeyFile, 17, 33);
                int byteToInt = (byteToInt(unpackMegolmKeyFile[33]) << 24) | (byteToInt(unpackMegolmKeyFile[34]) << 16) | (byteToInt(unpackMegolmKeyFile[35]) << 8) | byteToInt(unpackMegolmKeyFile[36]);
                byte[] copyOfRange3 = Arrays.copyOfRange(unpackMegolmKeyFile, 37, length + 37);
                byte[] copyOfRange4 = Arrays.copyOfRange(unpackMegolmKeyFile, unpackMegolmKeyFile.length - 32, unpackMegolmKeyFile.length);
                byte[] deriveKeys = deriveKeys(copyOfRange, byteToInt, str);
                byte[] copyOfRange5 = Arrays.copyOfRange(unpackMegolmKeyFile, 0, unpackMegolmKeyFile.length - 32);
                SecretKeySpec secretKeySpec = new SecretKeySpec(getHmacKey(deriveKeys), "HmacSHA256");
                Mac instance = Mac.getInstance("HmacSHA256");
                instance.init(secretKeySpec);
                if (!Arrays.equals(copyOfRange4, instance.doFinal(copyOfRange5))) {
                    Log.m211e(LOG_TAG, "## decryptMegolmKeyFile() : Authentication check failed: incorrect password?");
                    throw new Exception("Authentication check failed: incorrect password?");
                }
                Cipher instance2 = Cipher.getInstance("AES/CTR/NoPadding");
                instance2.init(2, new SecretKeySpec(getAesKey(deriveKeys), "AES"), new IvParameterSpec(copyOfRange2));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(instance2.update(copyOfRange3));
                byteArrayOutputStream.write(instance2.doFinal());
                String str2 = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
                byteArrayOutputStream.close();
                return str2;
            }
        }
    }

    public static byte[] encryptMegolmKeyFile(String str, String str2) throws Exception {
        return encryptMegolmKeyFile(str, str2, DEFAULT_ITERATION_COUNT);
    }

    public static byte[] encryptMegolmKeyFile(String str, String str2, int i) throws Exception {
        if (TextUtils.isEmpty(str2)) {
            throw new Exception("Empty password is not supported");
        }
        SecureRandom secureRandom = new SecureRandom();
        byte[] bArr = new byte[16];
        secureRandom.nextBytes(bArr);
        byte[] bArr2 = new byte[16];
        secureRandom.nextBytes(bArr2);
        bArr2[9] = (byte) (bArr2[9] & ByteCompanionObject.MAX_VALUE);
        byte[] deriveKeys = deriveKeys(bArr, i, str2);
        Cipher instance = Cipher.getInstance("AES/CTR/NoPadding");
        instance.init(1, new SecretKeySpec(getAesKey(deriveKeys), "AES"), new IvParameterSpec(bArr2));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(instance.update(str.getBytes("UTF-8")));
        byteArrayOutputStream.write(instance.doFinal());
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byte[] bArr3 = new byte[(bArr.length + 1 + bArr2.length + 4 + byteArray.length + 32)];
        bArr3[0] = 1;
        System.arraycopy(bArr, 0, bArr3, 1, bArr.length);
        int length = 1 + bArr.length;
        System.arraycopy(bArr2, 0, bArr3, length, bArr2.length);
        int length2 = length + bArr2.length;
        int i2 = length2 + 1;
        bArr3[length2] = (byte) ((i >> 24) & 255);
        int i3 = i2 + 1;
        bArr3[i2] = (byte) ((i >> 16) & 255);
        int i4 = i3 + 1;
        bArr3[i3] = (byte) ((i >> 8) & 255);
        int i5 = i4 + 1;
        bArr3[i4] = (byte) (i & 255);
        System.arraycopy(byteArray, 0, bArr3, i5, byteArray.length);
        int length3 = i5 + byteArray.length;
        byte[] copyOfRange = Arrays.copyOfRange(bArr3, 0, length3);
        SecretKeySpec secretKeySpec = new SecretKeySpec(getHmacKey(deriveKeys), "HmacSHA256");
        Mac instance2 = Mac.getInstance("HmacSHA256");
        instance2.init(secretKeySpec);
        byte[] doFinal = instance2.doFinal(copyOfRange);
        System.arraycopy(doFinal, 0, bArr3, length3, doFinal.length);
        return packMegolmKeyFile(bArr3);
    }

    private static byte[] unpackMegolmKeyFile(byte[] bArr) throws Exception {
        String str;
        String str2 = new String(bArr, "UTF-8");
        int i = 0;
        while (true) {
            int indexOf = str2.indexOf(10, i);
            if (indexOf < 0) {
                Log.m211e(LOG_TAG, "## unpackMegolmKeyFile() : Header line not found");
                throw new Exception("Header line not found");
            }
            int i2 = indexOf + 1;
            if (TextUtils.equals(str2.substring(i, indexOf).trim(), HEADER_LINE)) {
                int i3 = i2;
                while (true) {
                    int indexOf2 = str2.indexOf(10, i3);
                    if (indexOf2 < 0) {
                        str = str2.substring(i3).trim();
                    } else {
                        str = str2.substring(i3, indexOf2).trim();
                    }
                    if (TextUtils.equals(str, TRAILER_LINE)) {
                        return Base64.decode(str2.substring(i2, i3), 0);
                    }
                    if (indexOf2 < 0) {
                        Log.m211e(LOG_TAG, "## unpackMegolmKeyFile() : Trailer line not found");
                        throw new Exception("Trailer line not found");
                    }
                    i3 = indexOf2 + 1;
                }
            } else {
                i = i2;
            }
        }
    }

    private static byte[] packMegolmKeyFile(byte[] bArr) throws Exception {
        int length = ((bArr.length + 96) - 1) / 96;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(HEADER_LINE.getBytes());
        int i = 0;
        for (int i2 = 1; i2 <= length; i2++) {
            byteArrayOutputStream.write(StringUtils.f158LF.getBytes());
            byteArrayOutputStream.write(Base64.encode(bArr, i, Math.min(96, bArr.length - i), 0));
            i += 96;
        }
        byteArrayOutputStream.write(StringUtils.f158LF.getBytes());
        byteArrayOutputStream.write(TRAILER_LINE.getBytes());
        byteArrayOutputStream.write(StringUtils.f158LF.getBytes());
        return byteArrayOutputStream.toByteArray();
    }

    private static byte[] deriveKeys(byte[] bArr, int i, String str) throws Exception {
        Long valueOf = Long.valueOf(System.currentTimeMillis());
        Mac instance = Mac.getInstance("HmacSHA512");
        instance.init(new SecretKeySpec(str.getBytes("UTF-8"), "HmacSHA512"));
        byte[] bArr2 = new byte[64];
        byte[] bArr3 = new byte[64];
        instance.update(bArr);
        byte[] bArr4 = new byte[4];
        Arrays.fill(bArr4, 0);
        bArr4[3] = 1;
        instance.update(bArr4);
        instance.doFinal(bArr3, 0);
        System.arraycopy(bArr3, 0, bArr2, 0, bArr3.length);
        for (int i2 = 2; i2 <= i; i2++) {
            instance.update(bArr3);
            instance.doFinal(bArr3, 0);
            for (int i3 = 0; i3 < bArr3.length; i3++) {
                bArr2[i3] = (byte) (bArr2[i3] ^ bArr3[i3]);
            }
        }
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## deriveKeys() : ");
        sb.append(i);
        sb.append(" in ");
        sb.append(System.currentTimeMillis() - valueOf.longValue());
        sb.append(" ms");
        Log.m209d(str2, sb.toString());
        return bArr2;
    }
}
