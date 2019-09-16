package com.opengarden.firechat.matrixsdk.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.X509TrustManager;

public class PinnedTrustManager implements X509TrustManager {
    private final X509TrustManager mDefaultTrustManager;
    private final List<Fingerprint> mFingerprints;

    public PinnedTrustManager(List<Fingerprint> list, X509TrustManager x509TrustManager) {
        this.mFingerprints = list;
        this.mDefaultTrustManager = x509TrustManager;
    }

    public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        try {
            if (this.mDefaultTrustManager != null) {
                this.mDefaultTrustManager.checkClientTrusted(x509CertificateArr, str);
                return;
            }
        } catch (CertificateException e) {
            if (this.mFingerprints == null || this.mFingerprints.size() == 0) {
                throw new UnrecognizedCertificateException(x509CertificateArr[0], Fingerprint.newSha256Fingerprint(x509CertificateArr[0]), e.getCause());
            }
        }
        checkTrusted("client", x509CertificateArr);
    }

    public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        try {
            if (this.mDefaultTrustManager != null) {
                this.mDefaultTrustManager.checkServerTrusted(x509CertificateArr, str);
                return;
            }
        } catch (CertificateException e) {
            if (this.mFingerprints == null || this.mFingerprints.size() == 0) {
                throw new UnrecognizedCertificateException(x509CertificateArr[0], Fingerprint.newSha256Fingerprint(x509CertificateArr[0]), e.getCause());
            }
        }
        checkTrusted("server", x509CertificateArr);
    }

    private void checkTrusted(String str, X509Certificate[] x509CertificateArr) throws CertificateException {
        boolean z = false;
        X509Certificate x509Certificate = x509CertificateArr[0];
        if (this.mFingerprints != null) {
            Iterator it = this.mFingerprints.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Fingerprint fingerprint = (Fingerprint) it.next();
                if (fingerprint != null && fingerprint.matchesCert(x509Certificate)) {
                    z = true;
                    break;
                }
            }
        }
        if (!z) {
            throw new UnrecognizedCertificateException(x509Certificate, Fingerprint.newSha256Fingerprint(x509Certificate), null);
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
