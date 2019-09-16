package com.opengarden.firechat.matrixsdk.crypto;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.crypto.algorithms.IMXDecrypting;
import com.opengarden.firechat.matrixsdk.crypto.algorithms.IMXEncrypting;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MXCryptoAlgorithms {
    private static final String LOG_TAG = "MXCryptoAlgorithms";
    public static final String MXCRYPTO_ALGORITHM_MEGOLM = "m.megolm.v1.aes-sha2";
    public static final String MXCRYPTO_ALGORITHM_OLM = "m.olm.v1.curve25519-aes-sha2";
    private static MXCryptoAlgorithms mSharedInstance;
    private final HashMap<String, Class<IMXDecrypting>> mDecryptors;
    private final HashMap<String, Class<IMXEncrypting>> mEncryptors = new HashMap<>();

    public static MXCryptoAlgorithms sharedAlgorithms() {
        if (mSharedInstance == null) {
            mSharedInstance = new MXCryptoAlgorithms();
        }
        return mSharedInstance;
    }

    private MXCryptoAlgorithms() {
        try {
            this.mEncryptors.put(MXCRYPTO_ALGORITHM_MEGOLM, Class.forName("com.opengarden.firechat.matrixsdk.crypto.algorithms.megolm.MXMegolmEncryption"));
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## MXCryptoAlgorithms() : fails to add MXCRYPTO_ALGORITHM_MEGOLM ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
        try {
            this.mEncryptors.put(MXCRYPTO_ALGORITHM_OLM, Class.forName("com.opengarden.firechat.matrixsdk.crypto.algorithms.olm.MXOlmEncryption"));
        } catch (Exception e2) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## MXCryptoAlgorithms() : fails to add MXCRYPTO_ALGORITHM_OLM ");
            sb2.append(e2.getMessage());
            Log.m211e(str2, sb2.toString());
        }
        this.mDecryptors = new HashMap<>();
        try {
            this.mDecryptors.put(MXCRYPTO_ALGORITHM_MEGOLM, Class.forName("com.opengarden.firechat.matrixsdk.crypto.algorithms.megolm.MXMegolmDecryption"));
        } catch (Exception e3) {
            String str3 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## MXCryptoAlgorithms() : fails to add MXCRYPTO_ALGORITHM_MEGOLM ");
            sb3.append(e3.getMessage());
            Log.m211e(str3, sb3.toString());
        }
        try {
            this.mDecryptors.put(MXCRYPTO_ALGORITHM_OLM, Class.forName("com.opengarden.firechat.matrixsdk.crypto.algorithms.olm.MXOlmDecryption"));
        } catch (Exception e4) {
            String str4 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("## MXCryptoAlgorithms() : fails to add MXCRYPTO_ALGORITHM_OLM ");
            sb4.append(e4.getMessage());
            Log.m211e(str4, sb4.toString());
        }
    }

    public Class<IMXEncrypting> encryptorClassForAlgorithm(String str) {
        if (!TextUtils.isEmpty(str)) {
            return (Class) this.mEncryptors.get(str);
        }
        return null;
    }

    public Class<IMXDecrypting> decryptorClassForAlgorithm(String str) {
        if (!TextUtils.isEmpty(str)) {
            return (Class) this.mDecryptors.get(str);
        }
        return null;
    }

    public List<String> supportedAlgorithms() {
        return new ArrayList(this.mEncryptors.keySet());
    }
}
