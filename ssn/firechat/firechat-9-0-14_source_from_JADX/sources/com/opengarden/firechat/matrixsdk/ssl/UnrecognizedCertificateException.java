package com.opengarden.firechat.matrixsdk.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class UnrecognizedCertificateException extends CertificateException {
    private final X509Certificate mCert;
    private final Fingerprint mFingerprint;

    public UnrecognizedCertificateException(X509Certificate x509Certificate, Fingerprint fingerprint, Throwable th) {
        StringBuilder sb = new StringBuilder();
        sb.append("Unrecognized certificate with unknown fingerprint: ");
        sb.append(x509Certificate.getSubjectDN());
        super(sb.toString(), th);
        this.mCert = x509Certificate;
        this.mFingerprint = fingerprint;
    }

    public X509Certificate getCertificate() {
        return this.mCert;
    }

    public Fingerprint getFingerprint() {
        return this.mFingerprint;
    }
}
