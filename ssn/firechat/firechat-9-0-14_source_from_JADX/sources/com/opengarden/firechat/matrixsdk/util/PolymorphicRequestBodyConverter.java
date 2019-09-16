package com.opengarden.firechat.matrixsdk.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

public final class PolymorphicRequestBodyConverter<T> implements Converter<T, RequestBody> {
    public static final Factory FACTORY = new Factory() {
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] annotationArr, Annotation[] annotationArr2, Retrofit retrofit) {
            return new PolymorphicRequestBodyConverter(this, annotationArr, annotationArr2, retrofit);
        }
    };
    private final Map<Class<?>, Converter<T, RequestBody>> cache = new LinkedHashMap();
    private final Annotation[] methodsAnnotations;
    private final Annotation[] parameterAnnotations;
    private final Retrofit retrofit;
    private final Factory skipPast;

    PolymorphicRequestBodyConverter(Factory factory, Annotation[] annotationArr, Annotation[] annotationArr2, Retrofit retrofit3) {
        this.skipPast = factory;
        this.parameterAnnotations = annotationArr;
        this.methodsAnnotations = annotationArr2;
        this.retrofit = retrofit3;
    }

    public RequestBody convert(T t) throws IOException {
        Converter converter;
        Class cls = t.getClass();
        synchronized (this.cache) {
            converter = (Converter) this.cache.get(cls);
        }
        if (converter == null) {
            converter = this.retrofit.nextRequestBodyConverter(this.skipPast, cls, this.parameterAnnotations, this.methodsAnnotations);
            synchronized (this.cache) {
                this.cache.put(cls, converter);
            }
        }
        return (RequestBody) converter.convert(t);
    }
}
