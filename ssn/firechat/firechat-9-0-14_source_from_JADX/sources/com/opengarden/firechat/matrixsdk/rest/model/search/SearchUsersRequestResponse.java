package com.opengarden.firechat.matrixsdk.rest.model.search;

import java.util.List;

public class SearchUsersRequestResponse {
    public Boolean limited;
    public List<User> results;

    public class User {
        public String avatar_url;
        public String display_name;
        public String user_id;

        public User() {
        }
    }
}
