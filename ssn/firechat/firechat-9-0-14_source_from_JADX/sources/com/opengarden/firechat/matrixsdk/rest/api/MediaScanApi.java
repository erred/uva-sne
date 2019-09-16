package com.opengarden.firechat.matrixsdk.rest.api;

import com.opengarden.firechat.matrixsdk.rest.model.EncryptedMediaScanBody;
import com.opengarden.firechat.matrixsdk.rest.model.EncryptedMediaScanEncryptedBody;
import com.opengarden.firechat.matrixsdk.rest.model.MediaScanPublicKeyResult;
import com.opengarden.firechat.matrixsdk.rest.model.MediaScanResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MediaScanApi {
    @GET("public_key")
    Call<MediaScanPublicKeyResult> getServerPublicKey();

    @POST("scan_encrypted")
    Call<MediaScanResult> scanEncrypted(@Body EncryptedMediaScanBody encryptedMediaScanBody);

    @POST("scan_encrypted")
    Call<MediaScanResult> scanEncrypted(@Body EncryptedMediaScanEncryptedBody encryptedMediaScanEncryptedBody);

    @GET("scan/{domain}/{mediaId}")
    Call<MediaScanResult> scanUnencrypted(@Path("domain") String str, @Path("mediaId") String str2);
}
