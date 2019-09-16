package com.opengarden.firechat.matrixsdk.rest.model.pid;

import java.io.Serializable;
import java.util.Map;

public class ThirdPartyProtocolInstance implements Serializable {
    public String botUserId;
    public String desc;
    public Map<String, Object> fields;
    public String icon;
    public String instanceId;
    public String networkId;
}
