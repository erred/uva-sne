package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

@GwtCompatible
@Beta
public final class MapConstraints {

    static class ConstrainedAsMapEntries<K, V> extends ForwardingSet<Entry<K, Collection<V>>> {
        /* access modifiers changed from: private */
        public final MapConstraint<? super K, ? super V> constraint;
        private final Set<Entry<K, Collection<V>>> entries;

        ConstrainedAsMapEntries(Set<Entry<K, Collection<V>>> set, MapConstraint<? super K, ? super V> mapConstraint) {
            this.entries = set;
            this.constraint = mapConstraint;
        }

        /* access modifiers changed from: protected */
        public Set<Entry<K, Collection<V>>> delegate() {
            return this.entries;
        }

        public Iterator<Entry<K, Collection<V>>> iterator() {
            final Iterator it = this.entries.iterator();
            return new ForwardingIterator<Entry<K, Collection<V>>>() {
                public Entry<K, Collection<V>> next() {
                    return MapConstraints.constrainedAsMapEntry((Entry) it.next(), ConstrainedAsMapEntries.this.constraint);
                }

                /* access modifiers changed from: protected */
                public Iterator<Entry<K, Collection<V>>> delegate() {
                    return it;
                }
            };
        }

        public Object[] toArray() {
            return standardToArray();
        }

        public <T> T[] toArray(T[] tArr) {
            return standardToArray(tArr);
        }

        public boolean contains(Object obj) {
            return Maps.containsEntryImpl(delegate(), obj);
        }

        public boolean containsAll(Collection<?> collection) {
            return standardContainsAll(collection);
        }

        public boolean equals(Object obj) {
            return standardEquals(obj);
        }

        public int hashCode() {
            return standardHashCode();
        }

        public boolean remove(Object obj) {
            return Maps.removeEntryImpl(delegate(), obj);
        }

        public boolean removeAll(Collection<?> collection) {
            return standardRemoveAll(collection);
        }

        public boolean retainAll(Collection<?> collection) {
            return standardRetainAll(collection);
        }
    }

    private static class ConstrainedAsMapValues<K, V> extends ForwardingCollection<Collection<V>> {
        final Collection<Collection<V>> delegate;
        final Set<Entry<K, Collection<V>>> entrySet;

        ConstrainedAsMapValues(Collection<Collection<V>> collection, Set<Entry<K, Collection<V>>> set) {
            this.delegate = collection;
            this.entrySet = set;
        }

        /* access modifiers changed from: protected */
        public Collection<Collection<V>> delegate() {
            return this.delegate;
        }

        public Iterator<Collection<V>> iterator() {
            final Iterator it = this.entrySet.iterator();
            return new Iterator<Collection<V>>() {
                public boolean hasNext() {
                    return it.hasNext();
                }

                public Collection<V> next() {
                    return (Collection) ((Entry) it.next()).getValue();
                }

                public void remove() {
                    it.remove();
                }
            };
        }

        public Object[] toArray() {
            return standardToArray();
        }

        public <T> T[] toArray(T[] tArr) {
            return standardToArray(tArr);
        }

        public boolean contains(Object obj) {
            return standardContains(obj);
        }

        public boolean containsAll(Collection<?> collection) {
            return standardContainsAll(collection);
        }

        public boolean remove(Object obj) {
            return standardRemove(obj);
        }

        public boolean removeAll(Collection<?> collection) {
            return standardRemoveAll(collection);
        }

        public boolean retainAll(Collection<?> collection) {
            return standardRetainAll(collection);
        }
    }

    private static class ConstrainedBiMap<K, V> extends ConstrainedMap<K, V> implements BiMap<K, V> {
        volatile BiMap<V, K> inverse;

        ConstrainedBiMap(BiMap<K, V> biMap, BiMap<V, K> biMap2, MapConstraint<? super K, ? super V> mapConstraint) {
            super(biMap, mapConstraint);
            this.inverse = biMap2;
        }

        /* access modifiers changed from: protected */
        public BiMap<K, V> delegate() {
            return (BiMap) super.delegate();
        }

        public V forcePut(K k, V v) {
            this.constraint.checkKeyValue(k, v);
            return delegate().forcePut(k, v);
        }

        public BiMap<V, K> inverse() {
            if (this.inverse == null) {
                this.inverse = new ConstrainedBiMap(delegate().inverse(), this, new InverseConstraint(this.constraint));
            }
            return this.inverse;
        }

        public Set<V> values() {
            return delegate().values();
        }
    }

    private static class ConstrainedEntries<K, V> extends ForwardingCollection<Entry<K, V>> {
        final MapConstraint<? super K, ? super V> constraint;
        final Collection<Entry<K, V>> entries;

        ConstrainedEntries(Collection<Entry<K, V>> collection, MapConstraint<? super K, ? super V> mapConstraint) {
            this.entries = collection;
            this.constraint = mapConstraint;
        }

        /* access modifiers changed from: protected */
        public Collection<Entry<K, V>> delegate() {
            return this.entries;
        }

        public Iterator<Entry<K, V>> iterator() {
            final Iterator it = this.entries.iterator();
            return new ForwardingIterator<Entry<K, V>>() {
                public Entry<K, V> next() {
                    return MapConstraints.constrainedEntry((Entry) it.next(), ConstrainedEntries.this.constraint);
                }

                /* access modifiers changed from: protected */
                public Iterator<Entry<K, V>> delegate() {
                    return it;
                }
            };
        }

        public Object[] toArray() {
            return standardToArray();
        }

        public <T> T[] toArray(T[] tArr) {
            return standardToArray(tArr);
        }

        public boolean contains(Object obj) {
            return Maps.containsEntryImpl(delegate(), obj);
        }

        public boolean containsAll(Collection<?> collection) {
            return standardContainsAll(collection);
        }

        public boolean remove(Object obj) {
            return Maps.removeEntryImpl(delegate(), obj);
        }

        public boolean removeAll(Collection<?> collection) {
            return standardRemoveAll(collection);
        }

        public boolean retainAll(Collection<?> collection) {
            return standardRetainAll(collection);
        }
    }

    static class ConstrainedEntrySet<K, V> extends ConstrainedEntries<K, V> implements Set<Entry<K, V>> {
        ConstrainedEntrySet(Set<Entry<K, V>> set, MapConstraint<? super K, ? super V> mapConstraint) {
            super(set, mapConstraint);
        }

        public boolean equals(Object obj) {
            return Sets.equalsImpl(this, obj);
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }

    private static class ConstrainedListMultimap<K, V> extends ConstrainedMultimap<K, V> implements ListMultimap<K, V> {
        ConstrainedListMultimap(ListMultimap<K, V> listMultimap, MapConstraint<? super K, ? super V> mapConstraint) {
            super(listMultimap, mapConstraint);
        }

        public List<V> get(K k) {
            return (List) super.get(k);
        }

        public List<V> removeAll(Object obj) {
            return (List) super.removeAll(obj);
        }

        public List<V> replaceValues(K k, Iterable<? extends V> iterable) {
            return (List) super.replaceValues(k, iterable);
        }
    }

    static class ConstrainedMap<K, V> extends ForwardingMap<K, V> {
        final MapConstraint<? super K, ? super V> constraint;
        private final Map<K, V> delegate;
        private transient Set<Entry<K, V>> entrySet;

        ConstrainedMap(Map<K, V> map, MapConstraint<? super K, ? super V> mapConstraint) {
            this.delegate = (Map) Preconditions.checkNotNull(map);
            this.constraint = (MapConstraint) Preconditions.checkNotNull(mapConstraint);
        }

        /* access modifiers changed from: protected */
        public Map<K, V> delegate() {
            return this.delegate;
        }

        public Set<Entry<K, V>> entrySet() {
            Set<Entry<K, V>> set = this.entrySet;
            if (set != null) {
                return set;
            }
            Set<Entry<K, V>> access$000 = MapConstraints.constrainedEntrySet(this.delegate.entrySet(), this.constraint);
            this.entrySet = access$000;
            return access$000;
        }

        public V put(K k, V v) {
            this.constraint.checkKeyValue(k, v);
            return this.delegate.put(k, v);
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            this.delegate.putAll(MapConstraints.checkMap(map, this.constraint));
        }
    }

    private static class ConstrainedMultimap<K, V> extends ForwardingMultimap<K, V> implements Serializable {
        transient Map<K, Collection<V>> asMap;
        final MapConstraint<? super K, ? super V> constraint;
        final Multimap<K, V> delegate;
        transient Collection<Entry<K, V>> entries;

        public ConstrainedMultimap(Multimap<K, V> multimap, MapConstraint<? super K, ? super V> mapConstraint) {
            this.delegate = (Multimap) Preconditions.checkNotNull(multimap);
            this.constraint = (MapConstraint) Preconditions.checkNotNull(mapConstraint);
        }

        /* access modifiers changed from: protected */
        public Multimap<K, V> delegate() {
            return this.delegate;
        }

        public Map<K, Collection<V>> asMap() {
            Map<K, Collection<V>> map = this.asMap;
            if (map != null) {
                return map;
            }
            final Map asMap2 = this.delegate.asMap();
            C25241 r1 = new ForwardingMap<K, Collection<V>>() {
                Set<Entry<K, Collection<V>>> entrySet;
                Collection<Collection<V>> values;

                /* access modifiers changed from: protected */
                public Map<K, Collection<V>> delegate() {
                    return asMap2;
                }

                public Set<Entry<K, Collection<V>>> entrySet() {
                    Set<Entry<K, Collection<V>>> set = this.entrySet;
                    if (set != null) {
                        return set;
                    }
                    Set<Entry<K, Collection<V>>> access$200 = MapConstraints.constrainedAsMapEntries(asMap2.entrySet(), ConstrainedMultimap.this.constraint);
                    this.entrySet = access$200;
                    return access$200;
                }

                public Collection<V> get(Object obj) {
                    try {
                        Collection<V> collection = ConstrainedMultimap.this.get(obj);
                        if (collection.isEmpty()) {
                            collection = null;
                        }
                        return collection;
                    } catch (ClassCastException unused) {
                        return null;
                    }
                }

                public Collection<Collection<V>> values() {
                    Collection<Collection<V>> collection = this.values;
                    if (collection != null) {
                        return collection;
                    }
                    ConstrainedAsMapValues constrainedAsMapValues = new ConstrainedAsMapValues(delegate().values(), entrySet());
                    this.values = constrainedAsMapValues;
                    return constrainedAsMapValues;
                }

                public boolean containsValue(Object obj) {
                    return values().contains(obj);
                }
            };
            this.asMap = r1;
            return r1;
        }

        public Collection<Entry<K, V>> entries() {
            Collection<Entry<K, V>> collection = this.entries;
            if (collection != null) {
                return collection;
            }
            Collection<Entry<K, V>> access$300 = MapConstraints.constrainedEntries(this.delegate.entries(), this.constraint);
            this.entries = access$300;
            return access$300;
        }

        public Collection<V> get(final K k) {
            return Constraints.constrainedTypePreservingCollection(this.delegate.get(k), new Constraint<V>() {
                public V checkElement(V v) {
                    ConstrainedMultimap.this.constraint.checkKeyValue(k, v);
                    return v;
                }
            });
        }

        public boolean put(K k, V v) {
            this.constraint.checkKeyValue(k, v);
            return this.delegate.put(k, v);
        }

        public boolean putAll(K k, Iterable<? extends V> iterable) {
            return this.delegate.putAll(k, MapConstraints.checkValues(k, iterable, this.constraint));
        }

        public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
            boolean z = false;
            for (Entry entry : multimap.entries()) {
                z |= put(entry.getKey(), entry.getValue());
            }
            return z;
        }

        public Collection<V> replaceValues(K k, Iterable<? extends V> iterable) {
            return this.delegate.replaceValues(k, MapConstraints.checkValues(k, iterable, this.constraint));
        }
    }

    private static class ConstrainedSetMultimap<K, V> extends ConstrainedMultimap<K, V> implements SetMultimap<K, V> {
        ConstrainedSetMultimap(SetMultimap<K, V> setMultimap, MapConstraint<? super K, ? super V> mapConstraint) {
            super(setMultimap, mapConstraint);
        }

        public Set<V> get(K k) {
            return (Set) super.get(k);
        }

        public Set<Entry<K, V>> entries() {
            return (Set) super.entries();
        }

        public Set<V> removeAll(Object obj) {
            return (Set) super.removeAll(obj);
        }

        public Set<V> replaceValues(K k, Iterable<? extends V> iterable) {
            return (Set) super.replaceValues(k, iterable);
        }
    }

    private static class ConstrainedSortedSetMultimap<K, V> extends ConstrainedSetMultimap<K, V> implements SortedSetMultimap<K, V> {
        ConstrainedSortedSetMultimap(SortedSetMultimap<K, V> sortedSetMultimap, MapConstraint<? super K, ? super V> mapConstraint) {
            super(sortedSetMultimap, mapConstraint);
        }

        public SortedSet<V> get(K k) {
            return (SortedSet) super.get((Object) k);
        }

        public SortedSet<V> removeAll(Object obj) {
            return (SortedSet) super.removeAll(obj);
        }

        public SortedSet<V> replaceValues(K k, Iterable<? extends V> iterable) {
            return (SortedSet) super.replaceValues((Object) k, (Iterable) iterable);
        }

        public Comparator<? super V> valueComparator() {
            return ((SortedSetMultimap) delegate()).valueComparator();
        }
    }

    private static class InverseConstraint<K, V> implements MapConstraint<K, V> {
        final MapConstraint<? super V, ? super K> constraint;

        public InverseConstraint(MapConstraint<? super V, ? super K> mapConstraint) {
            this.constraint = (MapConstraint) Preconditions.checkNotNull(mapConstraint);
        }

        public void checkKeyValue(K k, V v) {
            this.constraint.checkKeyValue(v, k);
        }
    }

    private enum NotNullMapConstraint implements MapConstraint<Object, Object> {
        INSTANCE;

        public String toString() {
            return "Not null";
        }

        public void checkKeyValue(Object obj, Object obj2) {
            Preconditions.checkNotNull(obj);
            Preconditions.checkNotNull(obj2);
        }
    }

    private MapConstraints() {
    }

    public static MapConstraint<Object, Object> notNull() {
        return NotNullMapConstraint.INSTANCE;
    }

    public static <K, V> Map<K, V> constrainedMap(Map<K, V> map, MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedMap(map, mapConstraint);
    }

    public static <K, V> Multimap<K, V> constrainedMultimap(Multimap<K, V> multimap, MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedMultimap(multimap, mapConstraint);
    }

    public static <K, V> ListMultimap<K, V> constrainedListMultimap(ListMultimap<K, V> listMultimap, MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedListMultimap(listMultimap, mapConstraint);
    }

    public static <K, V> SetMultimap<K, V> constrainedSetMultimap(SetMultimap<K, V> setMultimap, MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedSetMultimap(setMultimap, mapConstraint);
    }

    public static <K, V> SortedSetMultimap<K, V> constrainedSortedSetMultimap(SortedSetMultimap<K, V> sortedSetMultimap, MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedSortedSetMultimap(sortedSetMultimap, mapConstraint);
    }

    /* access modifiers changed from: private */
    public static <K, V> Entry<K, V> constrainedEntry(final Entry<K, V> entry, final MapConstraint<? super K, ? super V> mapConstraint) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(mapConstraint);
        return new ForwardingMapEntry<K, V>() {
            /* access modifiers changed from: protected */
            public Entry<K, V> delegate() {
                return entry;
            }

            public V setValue(V v) {
                mapConstraint.checkKeyValue(getKey(), v);
                return entry.setValue(v);
            }
        };
    }

    /* access modifiers changed from: private */
    public static <K, V> Entry<K, Collection<V>> constrainedAsMapEntry(final Entry<K, Collection<V>> entry, final MapConstraint<? super K, ? super V> mapConstraint) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(mapConstraint);
        return new ForwardingMapEntry<K, Collection<V>>() {
            /* access modifiers changed from: protected */
            public Entry<K, Collection<V>> delegate() {
                return entry;
            }

            public Collection<V> getValue() {
                return Constraints.constrainedTypePreservingCollection((Collection) entry.getValue(), new Constraint<V>() {
                    public V checkElement(V v) {
                        mapConstraint.checkKeyValue(C25192.this.getKey(), v);
                        return v;
                    }
                });
            }
        };
    }

    /* access modifiers changed from: private */
    public static <K, V> Set<Entry<K, Collection<V>>> constrainedAsMapEntries(Set<Entry<K, Collection<V>>> set, MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedAsMapEntries(set, mapConstraint);
    }

    /* access modifiers changed from: private */
    public static <K, V> Collection<Entry<K, V>> constrainedEntries(Collection<Entry<K, V>> collection, MapConstraint<? super K, ? super V> mapConstraint) {
        if (collection instanceof Set) {
            return constrainedEntrySet((Set) collection, mapConstraint);
        }
        return new ConstrainedEntries(collection, mapConstraint);
    }

    /* access modifiers changed from: private */
    public static <K, V> Set<Entry<K, V>> constrainedEntrySet(Set<Entry<K, V>> set, MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedEntrySet(set, mapConstraint);
    }

    public static <K, V> BiMap<K, V> constrainedBiMap(BiMap<K, V> biMap, MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedBiMap(biMap, null, mapConstraint);
    }

    /* access modifiers changed from: private */
    public static <K, V> Collection<V> checkValues(K k, Iterable<? extends V> iterable, MapConstraint<? super K, ? super V> mapConstraint) {
        ArrayList<Object> newArrayList = Lists.newArrayList(iterable);
        for (Object checkKeyValue : newArrayList) {
            mapConstraint.checkKeyValue(k, checkKeyValue);
        }
        return newArrayList;
    }

    /* access modifiers changed from: private */
    public static <K, V> Map<K, V> checkMap(Map<? extends K, ? extends V> map, MapConstraint<? super K, ? super V> mapConstraint) {
        LinkedHashMap linkedHashMap = new LinkedHashMap(map);
        for (Entry entry : linkedHashMap.entrySet()) {
            mapConstraint.checkKeyValue(entry.getKey(), entry.getValue());
        }
        return linkedHashMap;
    }
}
