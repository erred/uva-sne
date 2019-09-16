package com.opengarden.firechat.matrixsdk.rest.model.pid;

import java.util.List;
import java.util.Map;

public class ThirdPartyProtocol {
    public Map<String, Map<String, String>> fieldTypes;
    public List<ThirdPartyProtocolInstance> instances;
    public List<String> locationFields;
    public List<String> userFields;
}
