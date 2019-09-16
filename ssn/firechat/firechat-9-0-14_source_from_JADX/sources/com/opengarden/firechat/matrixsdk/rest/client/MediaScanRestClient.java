package com.opengarden.firechat.matrixsdk.rest.client;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.RestClient.EndPointServer;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.rest.api.MediaScanApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.DefaultRetrofit2CallbackWrapper;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.EncryptedMediaScanBody;
import com.opengarden.firechat.matrixsdk.rest.model.EncryptedMediaScanEncryptedBody;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.MediaScanPublicKeyResult;
import com.opengarden.firechat.matrixsdk.rest.model.MediaScanResult;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedBodyFileInfo;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import org.matrix.olm.OlmException;
import org.matrix.olm.OlmPkEncryption;
import org.matrix.olm.OlmPkMessage;
import retrofit2.Call;

public class MediaScanRestClient extends RestClient<MediaScanApi> {
    /* access modifiers changed from: private */
    @Nullable
    public IMXStore mMxStore;

    public MediaScanRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, MediaScanApi.class, RestClient.URI_API_PREFIX_PATH_MEDIA_PROXY_UNSTABLE, false, EndPointServer.ANTIVIRUS_SERVER);
    }

    public void setMxStore(IMXStore iMXStore) {
        this.mMxStore = iMXStore;
    }

    public void getServerPublicKey(final ApiCallback<String> apiCallback) {
        if (this.mMxStore == null) {
            apiCallback.onUnexpectedError(new Exception("MxStore not configured"));
            return;
        }
        String antivirusServerPublicKey = this.mMxStore.getAntivirusServerPublicKey();
        if (antivirusServerPublicKey != null) {
            apiCallback.onSuccess(antivirusServerPublicKey);
        } else {
            ((MediaScanApi) this.mApi).getServerPublicKey().enqueue(new DefaultRetrofit2CallbackWrapper(new SimpleApiCallback<MediaScanPublicKeyResult>(apiCallback) {
                public void onSuccess(MediaScanPublicKeyResult mediaScanPublicKeyResult) {
                    MediaScanRestClient.this.mMxStore.setAntivirusServerPublicKey(mediaScanPublicKeyResult.mCurve25519PublicKey);
                    if (mediaScanPublicKeyResult.mCurve25519PublicKey != null) {
                        apiCallback.onSuccess(mediaScanPublicKeyResult.mCurve25519PublicKey);
                    } else {
                        apiCallback.onUnexpectedError(new Exception("Unable to get server public key from Json"));
                    }
                }

                public void onMatrixError(MatrixError matrixError) {
                    if (matrixError.mStatus.intValue() == 404) {
                        MediaScanRestClient.this.mMxStore.setAntivirusServerPublicKey("");
                        apiCallback.onSuccess("");
                        return;
                    }
                    super.onMatrixError(matrixError);
                }
            }));
        }
    }

    public void resetServerPublicKey() {
        if (this.mMxStore != null) {
            this.mMxStore.setAntivirusServerPublicKey(null);
        }
    }

    public void scanUnencryptedFile(String str, String str2, ApiCallback<MediaScanResult> apiCallback) {
        ((MediaScanApi) this.mApi).scanUnencrypted(str, str2).enqueue(new DefaultRetrofit2CallbackWrapper(apiCallback));
    }

    public void scanEncryptedFile(final EncryptedMediaScanBody encryptedMediaScanBody, final ApiCallback<MediaScanResult> apiCallback) {
        getServerPublicKey(new SimpleApiCallback<String>(apiCallback) {
            public void onSuccess(String str) {
                Call call;
                if (!TextUtils.isEmpty(str)) {
                    try {
                        OlmPkEncryption olmPkEncryption = new OlmPkEncryption();
                        olmPkEncryption.setRecipientKey(str);
                        OlmPkMessage encrypt = olmPkEncryption.encrypt(JsonUtils.getCanonicalizedJsonString(encryptedMediaScanBody));
                        EncryptedMediaScanEncryptedBody encryptedMediaScanEncryptedBody = new EncryptedMediaScanEncryptedBody();
                        encryptedMediaScanEncryptedBody.encryptedBodyFileInfo = new EncryptedBodyFileInfo(encrypt);
                        call = ((MediaScanApi) MediaScanRestClient.this.mApi).scanEncrypted(encryptedMediaScanEncryptedBody);
                    } catch (OlmException e) {
                        apiCallback.onUnexpectedError(e);
                        call = null;
                    }
                } else {
                    call = ((MediaScanApi) MediaScanRestClient.this.mApi).scanEncrypted(encryptedMediaScanBody);
                }
                ((MediaScanApi) MediaScanRestClient.this.mApi).scanEncrypted(encryptedMediaScanBody).enqueue(new DefaultRetrofit2CallbackWrapper(apiCallback));
                if (call != null) {
                    call.enqueue(new DefaultRetrofit2CallbackWrapper(apiCallback));
                }
            }
        });
    }
}
