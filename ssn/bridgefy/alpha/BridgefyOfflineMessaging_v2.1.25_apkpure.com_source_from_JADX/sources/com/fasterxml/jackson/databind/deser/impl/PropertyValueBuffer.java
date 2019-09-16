package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import java.io.IOException;
import java.util.BitSet;

public class PropertyValueBuffer {
    protected PropertyValue _buffered;
    protected final DeserializationContext _context;
    protected final Object[] _creatorParameters;
    protected Object _idValue;
    protected final ObjectIdReader _objectIdReader;
    protected int _paramsNeeded;
    protected int _paramsSeen;
    protected final BitSet _paramsSeenBig;
    protected final JsonParser _parser;

    public PropertyValueBuffer(JsonParser jsonParser, DeserializationContext deserializationContext, int i, ObjectIdReader objectIdReader) {
        this._parser = jsonParser;
        this._context = deserializationContext;
        this._paramsNeeded = i;
        this._objectIdReader = objectIdReader;
        this._creatorParameters = new Object[i];
        if (i < 32) {
            this._paramsSeenBig = null;
        } else {
            this._paramsSeenBig = new BitSet();
        }
    }

    public final boolean hasParameter(SettableBeanProperty settableBeanProperty) {
        if (this._paramsSeenBig != null) {
            return this._paramsSeenBig.get(settableBeanProperty.getCreatorIndex());
        }
        boolean z = true;
        if (((this._paramsSeen >> settableBeanProperty.getCreatorIndex()) & 1) != 1) {
            z = false;
        }
        return z;
    }

    public Object getParameter(SettableBeanProperty settableBeanProperty) throws JsonMappingException {
        Object obj;
        if (hasParameter(settableBeanProperty)) {
            obj = this._creatorParameters[settableBeanProperty.getCreatorIndex()];
        } else {
            Object[] objArr = this._creatorParameters;
            int creatorIndex = settableBeanProperty.getCreatorIndex();
            Object _findMissing = _findMissing(settableBeanProperty);
            objArr[creatorIndex] = _findMissing;
            obj = _findMissing;
        }
        if (obj != null || !this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
            return obj;
        }
        throw this._context.mappingException("Null value for creator property '%s'; DeserializationFeature.FAIL_ON_NULL_FOR_CREATOR_PARAMETERS enabled", settableBeanProperty.getName(), Integer.valueOf(settableBeanProperty.getCreatorIndex()));
    }

    public Object[] getParameters(SettableBeanProperty[] settableBeanPropertyArr) throws JsonMappingException {
        if (this._paramsNeeded > 0) {
            if (this._paramsSeenBig != null) {
                int length = this._creatorParameters.length;
                int i = 0;
                while (true) {
                    int nextClearBit = this._paramsSeenBig.nextClearBit(i);
                    if (nextClearBit >= length) {
                        break;
                    }
                    this._creatorParameters[nextClearBit] = _findMissing(settableBeanPropertyArr[nextClearBit]);
                    i = nextClearBit + 1;
                }
            } else {
                int i2 = this._paramsSeen;
                int length2 = this._creatorParameters.length;
                int i3 = i2;
                int i4 = 0;
                while (i4 < length2) {
                    if ((i3 & 1) == 0) {
                        this._creatorParameters[i4] = _findMissing(settableBeanPropertyArr[i4]);
                    }
                    i4++;
                    i3 >>= 1;
                }
            }
        }
        if (this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
            for (int i5 = 0; i5 < settableBeanPropertyArr.length; i5++) {
                if (this._creatorParameters[i5] == null) {
                    this._context.reportMappingException("Null value for creator property '%s'; DeserializationFeature.FAIL_ON_NULL_FOR_CREATOR_PARAMETERS enabled", settableBeanPropertyArr[i5].getName(), Integer.valueOf(settableBeanPropertyArr[i5].getCreatorIndex()));
                }
            }
        }
        return this._creatorParameters;
    }

    /* access modifiers changed from: protected */
    public Object _findMissing(SettableBeanProperty settableBeanProperty) throws JsonMappingException {
        if (settableBeanProperty.getInjectableValueId() != null) {
            return this._context.findInjectableValue(settableBeanProperty.getInjectableValueId(), settableBeanProperty, null);
        }
        if (settableBeanProperty.isRequired()) {
            this._context.reportMappingException("Missing required creator property '%s' (index %d)", settableBeanProperty.getName(), Integer.valueOf(settableBeanProperty.getCreatorIndex()));
        }
        if (this._context.isEnabled(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)) {
            this._context.reportMappingException("Missing creator property '%s' (index %d); DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES enabled", settableBeanProperty.getName(), Integer.valueOf(settableBeanProperty.getCreatorIndex()));
        }
        return settableBeanProperty.getValueDeserializer().getNullValue(this._context);
    }

    public boolean readIdProperty(String str) throws IOException {
        if (this._objectIdReader == null || !str.equals(this._objectIdReader.propertyName.getSimpleName())) {
            return false;
        }
        this._idValue = this._objectIdReader.readObjectReference(this._parser, this._context);
        return true;
    }

    public Object handleIdValue(DeserializationContext deserializationContext, Object obj) throws IOException {
        if (this._objectIdReader != null) {
            if (this._idValue != null) {
                deserializationContext.findObjectId(this._idValue, this._objectIdReader.generator, this._objectIdReader.resolver).bindItem(obj);
                SettableBeanProperty settableBeanProperty = this._objectIdReader.idProperty;
                if (settableBeanProperty != null) {
                    return settableBeanProperty.setAndReturn(obj, this._idValue);
                }
            } else {
                deserializationContext.reportUnresolvedObjectId(this._objectIdReader, obj);
            }
        }
        return obj;
    }

    /* access modifiers changed from: protected */
    public PropertyValue buffered() {
        return this._buffered;
    }

    public boolean isComplete() {
        return this._paramsNeeded <= 0;
    }

    public boolean assignParameter(SettableBeanProperty settableBeanProperty, Object obj) {
        int creatorIndex = settableBeanProperty.getCreatorIndex();
        this._creatorParameters[creatorIndex] = obj;
        boolean z = false;
        if (this._paramsSeenBig == null) {
            int i = this._paramsSeen;
            int i2 = (1 << creatorIndex) | i;
            if (i != i2) {
                this._paramsSeen = i2;
                int i3 = this._paramsNeeded - 1;
                this._paramsNeeded = i3;
                if (i3 <= 0) {
                    if (this._objectIdReader == null || this._idValue != null) {
                        z = true;
                    }
                    return z;
                }
            }
        } else if (!this._paramsSeenBig.get(creatorIndex)) {
            this._paramsSeenBig.set(creatorIndex);
            this._paramsNeeded--;
        }
        return false;
    }

    public void bufferProperty(SettableBeanProperty settableBeanProperty, Object obj) {
        this._buffered = new Regular(this._buffered, obj, settableBeanProperty);
    }

    public void bufferAnyProperty(SettableAnyProperty settableAnyProperty, String str, Object obj) {
        this._buffered = new Any(this._buffered, obj, settableAnyProperty, str);
    }

    public void bufferMapProperty(Object obj, Object obj2) {
        this._buffered = new Map(this._buffered, obj2, obj);
    }
}
