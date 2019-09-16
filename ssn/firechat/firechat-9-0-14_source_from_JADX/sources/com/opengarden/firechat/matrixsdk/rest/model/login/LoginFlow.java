package com.opengarden.firechat.matrixsdk.rest.model.login;

import java.io.Serializable;
import java.util.List;

public class LoginFlow implements Serializable {
    public List<String> stages;
    public String type;
}
