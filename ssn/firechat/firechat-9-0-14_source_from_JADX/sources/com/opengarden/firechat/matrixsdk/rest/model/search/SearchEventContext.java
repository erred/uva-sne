package com.opengarden.firechat.matrixsdk.rest.model.search;

import com.opengarden.firechat.matrixsdk.rest.model.Event;
import java.util.HashMap;
import java.util.List;

public class SearchEventContext {
    public String end;
    public List<Event> eventsAfter;
    public List<Event> eventsBefore;
    public HashMap<String, SearchUserProfile> profileInfo;
    public String start;
}
