package com.opengarden.firechat.matrixsdk.rest.model.login;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RegistrationFlowResponse implements Serializable {
    public List<String> completed;
    public List<LoginFlow> flows;
    public Map<String, Object> params;
    public String session;
}
