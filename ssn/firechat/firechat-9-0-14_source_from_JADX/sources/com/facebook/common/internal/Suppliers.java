package com.facebook.common.internal;

public class Suppliers {
    /* renamed from: of */
    public static <T> Supplier<T> m44of(final T t) {
        return new Supplier<T>() {
            public T get() {
                return t;
            }
        };
    }
}
