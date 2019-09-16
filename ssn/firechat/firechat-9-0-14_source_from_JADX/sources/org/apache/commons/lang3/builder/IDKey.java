package org.apache.commons.lang3.builder;

final class IDKey {

    /* renamed from: id */
    private final int f159id;
    private final Object value;

    public IDKey(Object obj) {
        this.f159id = System.identityHashCode(obj);
        this.value = obj;
    }

    public int hashCode() {
        return this.f159id;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof IDKey)) {
            return false;
        }
        IDKey iDKey = (IDKey) obj;
        if (this.f159id != iDKey.f159id) {
            return false;
        }
        if (this.value == iDKey.value) {
            z = true;
        }
        return z;
    }
}
