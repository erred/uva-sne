package com.facebook.react.modules.network;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class ReactCookieJarContainer implements CookieJarContainer {
    @Nullable
    private CookieJar cookieJar = null;

    public void setCookieJar(CookieJar cookieJar2) {
        this.cookieJar = cookieJar2;
    }

    public void removeCookieJar() {
        this.cookieJar = null;
    }

    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        if (this.cookieJar != null) {
            this.cookieJar.saveFromResponse(httpUrl, list);
        }
    }

    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        if (this.cookieJar != null) {
            return this.cookieJar.loadForRequest(httpUrl);
        }
        return Collections.emptyList();
    }
}
