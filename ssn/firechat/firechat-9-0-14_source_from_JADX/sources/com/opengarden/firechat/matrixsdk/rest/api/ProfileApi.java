package com.opengarden.firechat.matrixsdk.rest.api;

import com.opengarden.firechat.matrixsdk.rest.model.ChangePasswordParams;
import com.opengarden.firechat.matrixsdk.rest.model.DeactivateAccountParams;
import com.opengarden.firechat.matrixsdk.rest.model.ForgetPasswordParams;
import com.opengarden.firechat.matrixsdk.rest.model.ForgetPasswordResponse;
import com.opengarden.firechat.matrixsdk.rest.model.RequestEmailValidationParams;
import com.opengarden.firechat.matrixsdk.rest.model.RequestEmailValidationResponse;
import com.opengarden.firechat.matrixsdk.rest.model.RequestPhoneNumberValidationParams;
import com.opengarden.firechat.matrixsdk.rest.model.RequestPhoneNumberValidationResponse;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.login.TokenRefreshParams;
import com.opengarden.firechat.matrixsdk.rest.model.login.TokenRefreshResponse;
import com.opengarden.firechat.matrixsdk.rest.model.pid.AccountThreePidsResponse;
import com.opengarden.firechat.matrixsdk.rest.model.pid.AddThreePidsParams;
import com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteThreePidParams;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProfileApi {
    @POST("_matrix/client/r0/account/3pid")
    Call<Void> add3PID(@Body AddThreePidsParams addThreePidsParams);

    @GET("_matrix/client/r0/profile/{userId}/avatar_url")
    Call<User> avatarUrl(@Path("userId") String str);

    @PUT("_matrix/client/r0/profile/{userId}/avatar_url")
    Call<Void> avatarUrl(@Path("userId") String str, @Body User user);

    @POST("_matrix/client/r0/account/deactivate")
    Call<Void> deactivate(@Body DeactivateAccountParams deactivateAccountParams);

    @POST("_matrix/client/unstable/account/3pid/delete")
    Call<Void> delete3PID(@Body DeleteThreePidParams deleteThreePidParams);

    @GET("_matrix/client/r0/profile/{userId}/displayname")
    Call<User> displayname(@Path("userId") String str);

    @PUT("_matrix/client/r0/profile/{userId}/displayname")
    Call<Void> displayname(@Path("userId") String str, @Body User user);

    @POST("_matrix/client/r0/account/password/email/requestToken")
    Call<ForgetPasswordResponse> forgetPassword(@Body ForgetPasswordParams forgetPasswordParams);

    @POST("_matrix/client/r0/account/3pid/email/requestToken")
    Call<RequestEmailValidationResponse> requestEmailValidation(@Body RequestEmailValidationParams requestEmailValidationParams);

    @POST("_matrix/client/r0/register/email/requestToken")
    Call<RequestEmailValidationResponse> requestEmailValidationForRegistration(@Body RequestEmailValidationParams requestEmailValidationParams);

    @POST("_matrix/client/r0/account/3pid/msisdn/requestToken")
    Call<RequestPhoneNumberValidationResponse> requestPhoneNumberValidation(@Body RequestPhoneNumberValidationParams requestPhoneNumberValidationParams);

    @POST("_matrix/client/r0/register/msisdn/requestToken")
    Call<RequestPhoneNumberValidationResponse> requestPhoneNumberValidationForRegistration(@Body RequestPhoneNumberValidationParams requestPhoneNumberValidationParams);

    @GET("_matrix/client/r0/account/3pid")
    Call<AccountThreePidsResponse> threePIDs();

    @POST("_matrix/client/r0/tokenrefresh")
    Call<TokenRefreshResponse> tokenrefresh(@Body TokenRefreshParams tokenRefreshParams);

    @POST("_matrix/client/r0/account/password")
    Call<Void> updatePassword(@Body ChangePasswordParams changePasswordParams);
}
