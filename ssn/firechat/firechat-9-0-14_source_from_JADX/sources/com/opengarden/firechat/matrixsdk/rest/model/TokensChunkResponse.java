package com.opengarden.firechat.matrixsdk.rest.model;

import java.util.List;

public class TokensChunkResponse<T> {
    public List<T> chunk;
    public String end;
    public String start;
}
