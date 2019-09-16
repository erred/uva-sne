package com.opengarden.firechat.matrixsdk.rest.model;

public class HttpException extends Exception {
    private final HttpError httpError;

    public HttpException(HttpError httpError2) {
        this.httpError = httpError2;
    }

    public HttpError getHttpError() {
        return this.httpError;
    }
}
