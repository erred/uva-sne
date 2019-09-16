package com.opengarden.firechat.matrixsdk.rest.model.search;

import com.opengarden.firechat.matrixsdk.rest.model.User;
import java.util.List;

public class SearchUsersResponse {
    public Boolean limited;
    public List<User> results;
}
