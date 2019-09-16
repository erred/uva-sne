package com.opengarden.firechat.matrixsdk.rest.model.bingrules;

public class Condition {
    public static final String KIND_CONTAINS_DISPLAY_NAME = "contains_display_name";
    public static final String KIND_DEVICE = "device";
    public static final String KIND_EVENT_MATCH = "event_match";
    public static final String KIND_PROFILE_TAG = "profile_tag";
    public static final String KIND_ROOM_MEMBER_COUNT = "room_member_count";
    public static final String KIND_SENDER_NOTIFICATION_PERMISSION = "sender_notification_permission";
    public static final String KIND_UNKNOWN = "unknown_condition";
    public String kind;
}
