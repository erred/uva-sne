package com.opengarden.firechat.matrixsdk.rest.model.group;

import java.io.Serializable;

public class GroupSummary implements Serializable {
    public GroupProfile profile;
    public GroupSummaryRoomsSection roomsSection;
    public GroupSummaryUser user;
    public GroupSummaryUsersSection usersSection;
}
