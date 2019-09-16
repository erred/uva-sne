package com.opengarden.firechat.matrixsdk.rest.model.group;

import java.io.Serializable;
import java.util.Map;

public class GroupsSyncResponse implements Serializable {
    public Map<String, InvitedGroupSync> invite;
    public Map<String, Object> join;
    public Map<String, Object> leave;
}
