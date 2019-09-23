package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class ReadableObjectId {
    protected Object _item;
    protected final IdKey _key;
    protected LinkedList<Referring> _referringProperties;
    protected ObjectIdResolver _resolver;

    public static abstract class Referring {
        private final Class<?> _beanType;
        private final UnresolvedForwardReference _reference;

        public abstract void handleResolvedForwardReference(Object obj, Object obj2) throws IOException;

        public Referring(UnresolvedForwardReference unresolvedForwardReference, Class<?> cls) {
            this._reference = unresolvedForwardReference;
            this._beanType = cls;
        }

        public Referring(UnresolvedForwardReference unresolvedForwardReference, JavaType javaType) {
            this._reference = unresolvedForwardReference;
            this._beanType = javaType.getRawClass();
        }

        public JsonLocation getLocation() {
            return this._reference.getLocation();
        }

        public Class<?> getBeanType() {
            return this._beanType;
        }

        public boolean hasId(Object obj) {
            return obj.equals(this._reference.getUnresolvedId());
        }
    }

    public boolean tryToResolveUnresolved(DeserializationContext deserializationContext) {
        return false;
    }

    public ReadableObjectId(IdKey idKey) {
        this._key = idKey;
    }

    public void setResolver(ObjectIdResolver objectIdResolver) {
        this._resolver = objectIdResolver;
    }

    public IdKey getKey() {
        return this._key;
    }

    public void appendReferring(Referring referring) {
        if (this._referringProperties == null) {
            this._referringProperties = new LinkedList<>();
        }
        this._referringProperties.add(referring);
    }

    public void bindItem(Object obj) throws IOException {
        this._resolver.bindItem(this._key, obj);
        this._item = obj;
        Object obj2 = this._key.key;
        if (this._referringProperties != null) {
            Iterator it = this._referringProperties.iterator();
            this._referringProperties = null;
            while (it.hasNext()) {
                ((Referring) it.next()).handleResolvedForwardReference(obj2, obj);
            }
        }
    }

    public Object resolve() {
        Object resolveId = this._resolver.resolveId(this._key);
        this._item = resolveId;
        return resolveId;
    }

    public boolean hasReferringProperties() {
        return this._referringProperties != null && !this._referringProperties.isEmpty();
    }

    public Iterator<Referring> referringProperties() {
        if (this._referringProperties == null) {
            return Collections.emptyList().iterator();
        }
        return this._referringProperties.iterator();
    }

    public ObjectIdResolver getResolver() {
        return this._resolver;
    }

    public String toString() {
        return String.valueOf(this._key);
    }
}
