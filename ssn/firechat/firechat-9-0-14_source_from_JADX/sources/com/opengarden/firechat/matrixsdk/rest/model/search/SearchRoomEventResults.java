package com.opengarden.firechat.matrixsdk.rest.model.search;

import com.opengarden.firechat.matrixsdk.rest.model.Event;
import java.util.HashMap;
import java.util.List;

public class SearchRoomEventResults {
    public Integer count;
    public HashMap<String, SearchGroup> groups;
    public String nextBatch;
    public List<SearchResult> results;
    public HashMap<String, List<Event>> state;
}
