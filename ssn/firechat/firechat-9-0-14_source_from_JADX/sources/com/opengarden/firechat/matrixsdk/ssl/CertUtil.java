package com.opengarden.firechat.matrixsdk.ssl;

import android.util.Pair;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.ConnectionSpec.Builder;
import okhttp3.TlsVersion;
import org.altbeacon.bluetooth.Pdu;

public class CertUtil {
    private static final String LOG_TAG = "CertUtil";
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static byte[] generateSha256Fingerprint(X509Certificate x509Certificate) throws CertificateException {
        return generateFingerprint(x509Certificate, "SHA-256");
    }

    public static byte[] generateSha1Fingerprint(X509Certificate x509Certificate) throws CertificateException {
        return generateFingerprint(x509Certificate, "SHA-1");
    }

    private static byte[] generateFingerprint(X509Certificate x509Certificate, String str) throws CertificateException {
        try {
            return MessageDigest.getInstance(str).digest(x509Certificate.getEncoded());
        } catch (Exception e) {
            throw new CertificateException(e);
        }
    }

    public static String fingerprintToHexString(byte[] bArr) {
        return fingerprintToHexString(bArr, ' ');
    }

    public static String fingerprintToHexString(byte[] bArr, char c) {
        char[] cArr = new char[(bArr.length * 3)];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i] & Pdu.MANUFACTURER_DATA_PDU_TYPE;
            int i2 = i * 3;
            cArr[i2] = hexArray[b >>> 4];
            cArr[i2 + 1] = hexArray[b & 15];
            cArr[i2 + 2] = c;
        }
        return new String(cArr, 0, cArr.length - 1);
    }

    public static UnrecognizedCertificateException getCertificateException(Throwable th) {
        int i = 0;
        while (th != null && i < 10) {
            if (th instanceof UnrecognizedCertificateException) {
                return (UnrecognizedCertificateException) th;
            }
            th = th.getCause();
            i++;
        }
        return null;
    }

    public static Pair<SSLSocketFactory, X509TrustManager> newPinnedSSLSocketFactory(HomeServerConnectionConfig homeServerConnectionConfig) {
        X509TrustManager x509TrustManager;
        TrustManagerFactory trustManagerFactory;
        try {
            if (!homeServerConnectionConfig.shouldPin()) {
                try {
                    trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## newPinnedSSLSocketFactory() : TrustManagerFactory.getInstance failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                    trustManagerFactory = null;
                }
                if (trustManagerFactory == null) {
                    try {
                        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    } catch (Exception e2) {
                        String str2 = LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("## addRule : onBingRuleUpdateFailure failed ");
                        sb2.append(e2.getMessage());
                        Log.m211e(str2, sb2.toString());
                    }
                }
                trustManagerFactory.init(null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                int i = 0;
                while (true) {
                    if (i >= trustManagers.length) {
                        break;
                    } else if (trustManagers[i] instanceof X509TrustManager) {
                        x509TrustManager = (X509TrustManager) trustManagers[i];
                        break;
                    } else {
                        i++;
                    }
                }
            }
            x509TrustManager = null;
            TrustManager[] trustManagerArr = {new PinnedTrustManager(homeServerConnectionConfig.getAllowedFingerprints(), x509TrustManager)};
            SSLContext instance = SSLContext.getInstance("TLS");
            instance.init(null, trustManagerArr, new SecureRandom());
            return new Pair<>(instance.getSocketFactory(), x509TrustManager);
        } catch (Exception e3) {
            throw new RuntimeException(e3);
        }
    }

    public static HostnameVerifier newHostnameVerifier(HomeServerConnectionConfig homeServerConnectionConfig) {
        final HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        final List allowedFingerprints = homeServerConnectionConfig.getAllowedFingerprints();
        return new HostnameVerifier() {
            public boolean verify(String str, SSLSession sSLSession) {
                Certificate[] peerCertificates;
                if (defaultHostnameVerifier.verify(str, sSLSession)) {
                    return true;
                }
                if (allowedFingerprints == null || allowedFingerprints.size() == 0) {
                    return false;
                }
                try {
                    for (Certificate certificate : sSLSession.getPeerCertificates()) {
                        for (Fingerprint fingerprint : allowedFingerprints) {
                            if (fingerprint != null && (certificate instanceof X509Certificate) && fingerprint.matchesCert((X509Certificate) certificate)) {
                                return true;
                            }
                        }
                    }
                    return false;
                } catch (SSLPeerUnverifiedException unused) {
                    return false;
                } catch (CertificateException unused2) {
                    return false;
                }
            }
        };
    }

    public static List<ConnectionSpec> newConnectionSpecs(HomeServerConnectionConfig homeServerConnectionConfig) {
        Builder builder = new Builder(ConnectionSpec.MODERN_TLS);
        List acceptedTlsVersions = homeServerConnectionConfig.getAcceptedTlsVersions();
        if (acceptedTlsVersions != null) {
            builder.tlsVersions((TlsVersion[]) acceptedTlsVersions.toArray(new TlsVersion[0]));
        }
        List acceptedTlsCipherSuites = homeServerConnectionConfig.getAcceptedTlsCipherSuites();
        if (acceptedTlsCipherSuites != null) {
            builder.cipherSuites((CipherSuite[]) acceptedTlsCipherSuites.toArray(new CipherSuite[0]));
        }
        builder.supportsTlsExtensions(homeServerConnectionConfig.shouldAcceptTlsExtensions());
        ArrayList arrayList = new ArrayList();
        arrayList.add(builder.build());
        if (homeServerConnectionConfig.isHttpConnectionAllowed()) {
            arrayList.add(ConnectionSpec.CLEARTEXT);
        }
        return arrayList;
    }
}
