package com.opengarden.firechat.matrixsdk.rest.model.login;

import java.util.Map;

public class RegistrationParams {
    public Map<String, Object> auth;
    public Boolean bind_email;
    public Boolean bind_msisdn;
    public String initial_device_display_name;
    public String password;
    public String username;
    public Boolean x_show_msisdn;
}
