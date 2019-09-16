package com.opengarden.firechat.matrixsdk.rest.model.login;

public class TokenLoginParams extends LoginParams {
    public String initial_device_display_name;
    public String token;
    public String txn_id;
    public String user;

    public TokenLoginParams() {
        this.type = "m.login.token";
    }
}
