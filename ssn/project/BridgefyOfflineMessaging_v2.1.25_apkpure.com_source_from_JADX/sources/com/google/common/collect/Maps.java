package com.google.common.collect;

import com.google.android.gms.common.api.Api.BaseClientBuilder;
import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.MapDifference.ValueDifference;
import com.j256.ormlite.stmt.query.SimpleComparison;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible(emulated = true)
public final class Maps {
    static final MapJoiner STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator(SimpleComparison.EQUAL_TO_OPERATION);

    private static abstract class AbstractFilteredMap<K, V> extends ImprovedAbstractMap<K, V> {
        final Predicate<? super Entry<K, V>> predicate;
        final Map<K, V> unfiltered;

        AbstractFilteredMap(Map<K, V> map, Predicate<? super Entry<K, V>> predicate2) {
            this.unfiltered = map;
            this.predicate = predicate2;
        }

        /* access modifiers changed from: 0000 */
        public boolean apply(Object obj, V v) {
            return this.predicate.apply(Maps.immutableEntry(obj, v));
        }

        public V put(K k, V v) {
            Preconditions.checkArgument(apply(k, v));
            return this.unfiltered.put(k, v);
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            for (Entry entry : map.entrySet()) {
                Preconditions.checkArgument(apply(entry.getKey(), entry.getValue()));
            }
            this.unfiltered.putAll(map);
        }

        public boolean containsKey(Object obj) {
            return this.unfiltered.containsKey(obj) && apply(obj, this.unfiltered.get(obj));
        }

        public V get(Object obj) {
            V v = this.unfiltered.get(obj);
            if (v == null || !apply(obj, v)) {
                return null;
            }
            return v;
        }

        public boolean isEmpty() {
            return entrySet().isEmpty();
        }

        public V remove(Object obj) {
            if (containsKey(obj)) {
                return this.unfiltered.remove(obj);
            }
            return null;
        }

        /* access modifiers changed from: 0000 */
        public Collection<V> createValues() {
            return new FilteredMapValues(this, this.unfiltered, this.predicate);
        }
    }

    private static class AsMapView<K, V> extends ImprovedAbstractMap<K, V> {
        final Function<? super K, V> function;
        private final Set<K> set;

        /* access modifiers changed from: 0000 */
        public Set<K> backingSet() {
            return this.set;
        }

        AsMapView(Set<K> set2, Function<? super K, V> function2) {
            this.set = (Set) Preconditions.checkNotNull(set2);
            this.function = (Function) Preconditions.checkNotNull(function2);
        }

        public Set<K> createKeySet() {
            return Maps.removeOnlySet(backingSet());
        }

        /* access modifiers changed from: 0000 */
        public Collection<V> createValues() {
            return Collections2.transform(this.set, this.function);
        }

        public int size() {
            return backingSet().size();
        }

        public boolean containsKey(Object obj) {
            return backingSet().contains(obj);
        }

        public V get(Object obj) {
            if (Collections2.safeContains(backingSet(), obj)) {
                return this.function.apply(obj);
            }
            return null;
        }

        public V remove(Object obj) {
            if (backingSet().remove(obj)) {
                return this.function.apply(obj);
            }
            return null;
        }

        public void clear() {
            backingSet().clear();
        }

        /* access modifiers changed from: protected */
        public Set<Entry<K, V>> createEntrySet() {
            return new EntrySet<K, V>() {
                /* access modifiers changed from: 0000 */
                public Map<K, V> map() {
                    return AsMapView.this;
                }

                public Iterator<Entry<K, V>> iterator() {
                    return Maps.asMapEntryIterator(AsMapView.this.backingSet(), AsMapView.this.function);
                }
            };
        }
    }

    private static final class BiMapConverter<A, B> extends Converter<A, B> implements Serializable {
        private static final long serialVersionUID = 0;
        private final BiMap<A, B> bimap;

        BiMapConverter(BiMap<A, B> biMap) {
            this.bimap = (BiMap) Preconditions.checkNotNull(biMap);
        }

        /* access modifiers changed from: protected */
        public B doForward(A a) {
            return convert(this.bimap, a);
        }

        /* access modifiers changed from: protected */
        public A doBackward(B b) {
            return convert(this.bimap.inverse(), b);
        }

        private static <X, Y> Y convert(BiMap<X, Y> biMap, X x) {
            Y y = biMap.get(x);
            Preconditions.checkArgument(y != null, "No non-null mapping present for input: %s", x);
            return y;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof BiMapConverter)) {
                return false;
            }
            return this.bimap.equals(((BiMapConverter) obj).bimap);
        }

        public int hashCode() {
            return this.bimap.hashCode();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Maps.asConverter(");
            sb.append(this.bimap);
            sb.append(")");
            return sb.toString();
        }
    }

    private enum EntryFunction implements Function<Entry<?, ?>, Object> {
        KEY {
            public Object apply(Entry<?, ?> entry) {
                return entry.getKey();
            }
        },
        VALUE {
            public Object apply(Entry<?, ?> entry) {
                return entry.getValue();
            }
        }
    }

    static abstract class EntrySet<K, V> extends ImprovedAbstractSet<Entry<K, V>> {
        /* access modifiers changed from: 0000 */
        public abstract Map<K, V> map();

        EntrySet() {
        }

        public int size() {
            return map().size();
        }

        public void clear() {
            map().clear();
        }

        public boolean contains(Object obj) {
            boolean z = false;
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            Object key = entry.getKey();
            Object safeGet = Maps.safeGet(map(), key);
            if (Objects.equal(safeGet, entry.getValue()) && (safeGet != null || map().containsKey(key))) {
                z = true;
            }
            return z;
        }

        public boolean isEmpty() {
            return map().isEmpty();
        }

        public boolean remove(Object obj) {
            if (!contains(obj)) {
                return false;
            }
            return map().keySet().remove(((Entry) obj).getKey());
        }

        public boolean removeAll(Collection<?> collection) {
            try {
                return super.removeAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException unused) {
                return Sets.removeAllImpl((Set<?>) this, collection.iterator());
            }
        }

        public boolean retainAll(Collection<?> collection) {
            try {
                return super.retainAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException unused) {
                HashSet newHashSetWithExpectedSize = Sets.newHashSetWithExpectedSize(collection.size());
                for (Object next : collection) {
                    if (contains(next)) {
                        newHashSetWithExpectedSize.add(((Entry) next).getKey());
                    }
                }
                return map().keySet().retainAll(newHashSetWithExpectedSize);
            }
        }
    }

    public interface EntryTransformer<K, V1, V2> {
        V2 transformEntry(K k, V1 v1);
    }

    static final class FilteredEntryBiMap<K, V> extends FilteredEntryMap<K, V> implements BiMap<K, V> {
        private final BiMap<V, K> inverse;

        private static <K, V> Predicate<Entry<V, K>> inversePredicate(final Predicate<? super Entry<K, V>> predicate) {
            return new Predicate<Entry<V, K>>() {
                public boolean apply(Entry<V, K> entry) {
                    return predicate.apply(Maps.immutableEntry(entry.getValue(), entry.getKey()));
                }
            };
        }

        FilteredEntryBiMap(BiMap<K, V> biMap, Predicate<? super Entry<K, V>> predicate) {
            super(biMap, predicate);
            this.inverse = new FilteredEntryBiMap(biMap.inverse(), inversePredicate(predicate), this);
        }

        private FilteredEntryBiMap(BiMap<K, V> biMap, Predicate<? super Entry<K, V>> predicate, BiMap<V, K> biMap2) {
            super(biMap, predicate);
            this.inverse = biMap2;
        }

        /* access modifiers changed from: 0000 */
        public BiMap<K, V> unfiltered() {
            return (BiMap) this.unfiltered;
        }

        public V forcePut(K k, V v) {
            Preconditions.checkArgument(apply(k, v));
            return unfiltered().forcePut(k, v);
        }

        public BiMap<V, K> inverse() {
            return this.inverse;
        }

        public Set<V> values() {
            return this.inverse.keySet();
        }
    }

    static class FilteredEntryMap<K, V> extends AbstractFilteredMap<K, V> {
        final Set<Entry<K, V>> filteredEntrySet;

        private class EntrySet extends ForwardingSet<Entry<K, V>> {
            private EntrySet() {
            }

            /* access modifiers changed from: protected */
            public Set<Entry<K, V>> delegate() {
                return FilteredEntryMap.this.filteredEntrySet;
            }

            public Iterator<Entry<K, V>> iterator() {
                return new TransformedIterator<Entry<K, V>, Entry<K, V>>(FilteredEntryMap.this.filteredEntrySet.iterator()) {
                    /* access modifiers changed from: 0000 */
                    public Entry<K, V> transform(final Entry<K, V> entry) {
                        return new ForwardingMapEntry<K, V>() {
                            /* access modifiers changed from: protected */
                            public Entry<K, V> delegate() {
                                return entry;
                            }

                            public V setValue(V v) {
                                Preconditions.checkArgument(FilteredEntryMap.this.apply(getKey(), v));
                                return super.setValue(v);
                            }
                        };
                    }
                };
            }
        }

        class KeySet extends KeySet<K, V> {
            KeySet() {
                super(FilteredEntryMap.this);
            }

            public boolean remove(Object obj) {
                if (!FilteredEntryMap.this.containsKey(obj)) {
                    return false;
                }
                FilteredEntryMap.this.unfiltered.remove(obj);
                return true;
            }

            private boolean removeIf(Predicate<? super K> predicate) {
                return Iterables.removeIf(FilteredEntryMap.this.unfiltered.entrySet(), Predicates.and(FilteredEntryMap.this.predicate, Maps.keyPredicateOnEntries(predicate)));
            }

            public boolean removeAll(Collection<?> collection) {
                return removeIf(Predicates.m8648in(collection));
            }

            public boolean retainAll(Collection<?> collection) {
                return removeIf(Predicates.not(Predicates.m8648in(collection)));
            }

            public Object[] toArray() {
                return Lists.newArrayList(iterator()).toArray();
            }

            public <T> T[] toArray(T[] tArr) {
                return Lists.newArrayList(iterator()).toArray(tArr);
            }
        }

        FilteredEntryMap(Map<K, V> map, Predicate<? super Entry<K, V>> predicate) {
            super(map, predicate);
            this.filteredEntrySet = Sets.filter(map.entrySet(), this.predicate);
        }

        /* access modifiers changed from: protected */
        public Set<Entry<K, V>> createEntrySet() {
            return new EntrySet();
        }

        /* access modifiers changed from: 0000 */
        public Set<K> createKeySet() {
            return new KeySet();
        }
    }

    private static class FilteredEntrySortedMap<K, V> extends FilteredEntryMap<K, V> implements SortedMap<K, V> {

        class SortedKeySet extends KeySet implements SortedSet<K> {
            SortedKeySet() {
                super();
            }

            public Comparator<? super K> comparator() {
                return FilteredEntrySortedMap.this.sortedMap().comparator();
            }

            public SortedSet<K> subSet(K k, K k2) {
                return (SortedSet) FilteredEntrySortedMap.this.subMap(k, k2).keySet();
            }

            public SortedSet<K> headSet(K k) {
                return (SortedSet) FilteredEntrySortedMap.this.headMap(k).keySet();
            }

            public SortedSet<K> tailSet(K k) {
                return (SortedSet) FilteredEntrySortedMap.this.tailMap(k).keySet();
            }

            public K first() {
                return FilteredEntrySortedMap.this.firstKey();
            }

            public K last() {
                return FilteredEntrySortedMap.this.lastKey();
            }
        }

        FilteredEntrySortedMap(SortedMap<K, V> sortedMap, Predicate<? super Entry<K, V>> predicate) {
            super(sortedMap, predicate);
        }

        /* access modifiers changed from: 0000 */
        public SortedMap<K, V> sortedMap() {
            return (SortedMap) this.unfiltered;
        }

        public SortedSet<K> keySet() {
            return (SortedSet) super.keySet();
        }

        /* access modifiers changed from: 0000 */
        public SortedSet<K> createKeySet() {
            return new SortedKeySet();
        }

        public Comparator<? super K> comparator() {
            return sortedMap().comparator();
        }

        public K firstKey() {
            return keySet().iterator().next();
        }

        public K lastKey() {
            SortedMap sortedMap = sortedMap();
            while (true) {
                K lastKey = sortedMap.lastKey();
                if (apply(lastKey, this.unfiltered.get(lastKey))) {
                    return lastKey;
                }
                sortedMap = sortedMap().headMap(lastKey);
            }
        }

        public SortedMap<K, V> headMap(K k) {
            return new FilteredEntrySortedMap(sortedMap().headMap(k), this.predicate);
        }

        public SortedMap<K, V> subMap(K k, K k2) {
            return new FilteredEntrySortedMap(sortedMap().subMap(k, k2), this.predicate);
        }

        public SortedMap<K, V> tailMap(K k) {
            return new FilteredEntrySortedMap(sortedMap().tailMap(k), this.predicate);
        }
    }

    private static class FilteredKeyMap<K, V> extends AbstractFilteredMap<K, V> {
        Predicate<? super K> keyPredicate;

        FilteredKeyMap(Map<K, V> map, Predicate<? super K> predicate, Predicate<? super Entry<K, V>> predicate2) {
            super(map, predicate2);
            this.keyPredicate = predicate;
        }

        /* access modifiers changed from: protected */
        public Set<Entry<K, V>> createEntrySet() {
            return Sets.filter(this.unfiltered.entrySet(), this.predicate);
        }

        /* access modifiers changed from: 0000 */
        public Set<K> createKeySet() {
            return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
        }

        public boolean containsKey(Object obj) {
            return this.unfiltered.containsKey(obj) && this.keyPredicate.apply(obj);
        }
    }

    private static final class FilteredMapValues<K, V> extends Values<K, V> {
        Predicate<? super Entry<K, V>> predicate;
        Map<K, V> unfiltered;

        FilteredMapValues(Map<K, V> map, Map<K, V> map2, Predicate<? super Entry<K, V>> predicate2) {
            super(map);
            this.unfiltered = map2;
            this.predicate = predicate2;
        }

        public boolean remove(Object obj) {
            return Iterables.removeFirstMatching(this.unfiltered.entrySet(), Predicates.and(this.predicate, Maps.valuePredicateOnEntries(Predicates.equalTo(obj)))) != null;
        }

        private boolean removeIf(Predicate<? super V> predicate2) {
            return Iterables.removeIf(this.unfiltered.entrySet(), Predicates.and(this.predicate, Maps.valuePredicateOnEntries(predicate2)));
        }

        public boolean removeAll(Collection<?> collection) {
            return removeIf(Predicates.m8648in(collection));
        }

        public boolean retainAll(Collection<?> collection) {
            return removeIf(Predicates.not(Predicates.m8648in(collection)));
        }

        public Object[] toArray() {
            return Lists.newArrayList(iterator()).toArray();
        }

        public <T> T[] toArray(T[] tArr) {
            return Lists.newArrayList(iterator()).toArray(tArr);
        }
    }

    @GwtCompatible
    static abstract class ImprovedAbstractMap<K, V> extends AbstractMap<K, V> {
        private transient Set<Entry<K, V>> entrySet;
        private transient Set<K> keySet;
        private transient Collection<V> values;

        /* access modifiers changed from: 0000 */
        public abstract Set<Entry<K, V>> createEntrySet();

        ImprovedAbstractMap() {
        }

        public Set<Entry<K, V>> entrySet() {
            Set<Entry<K, V>> set = this.entrySet;
            if (set != null) {
                return set;
            }
            Set<Entry<K, V>> createEntrySet = createEntrySet();
            this.entrySet = createEntrySet;
            return createEntrySet;
        }

        public Set<K> keySet() {
            Set<K> set = this.keySet;
            if (set != null) {
                return set;
            }
            Set<K> createKeySet = createKeySet();
            this.keySet = createKeySet;
            return createKeySet;
        }

        /* access modifiers changed from: 0000 */
        public Set<K> createKeySet() {
            return new KeySet(this);
        }

        public Collection<V> values() {
            Collection<V> collection = this.values;
            if (collection != null) {
                return collection;
            }
            Collection<V> createValues = createValues();
            this.values = createValues;
            return createValues;
        }

        /* access modifiers changed from: 0000 */
        public Collection<V> createValues() {
            return new Values(this);
        }
    }

    static class KeySet<K, V> extends ImprovedAbstractSet<K> {
        final Map<K, V> map;

        KeySet(Map<K, V> map2) {
            this.map = (Map) Preconditions.checkNotNull(map2);
        }

        /* access modifiers changed from: 0000 */
        public Map<K, V> map() {
            return this.map;
        }

        public Iterator<K> iterator() {
            return Maps.keyIterator(map().entrySet().iterator());
        }

        public int size() {
            return map().size();
        }

        public boolean isEmpty() {
            return map().isEmpty();
        }

        public boolean contains(Object obj) {
            return map().containsKey(obj);
        }

        public boolean remove(Object obj) {
            if (!contains(obj)) {
                return false;
            }
            map().remove(obj);
            return true;
        }

        public void clear() {
            map().clear();
        }
    }

    static class MapDifferenceImpl<K, V> implements MapDifference<K, V> {
        final Map<K, ValueDifference<V>> differences;
        final Map<K, V> onBoth;
        final Map<K, V> onlyOnLeft;
        final Map<K, V> onlyOnRight;

        MapDifferenceImpl(Map<K, V> map, Map<K, V> map2, Map<K, V> map3, Map<K, ValueDifference<V>> map4) {
            this.onlyOnLeft = Maps.unmodifiableMap(map);
            this.onlyOnRight = Maps.unmodifiableMap(map2);
            this.onBoth = Maps.unmodifiableMap(map3);
            this.differences = Maps.unmodifiableMap(map4);
        }

        public boolean areEqual() {
            return this.onlyOnLeft.isEmpty() && this.onlyOnRight.isEmpty() && this.differences.isEmpty();
        }

        public Map<K, V> entriesOnlyOnLeft() {
            return this.onlyOnLeft;
        }

        public Map<K, V> entriesOnlyOnRight() {
            return this.onlyOnRight;
        }

        public Map<K, V> entriesInCommon() {
            return this.onBoth;
        }

        public Map<K, ValueDifference<V>> entriesDiffering() {
            return this.differences;
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof MapDifference)) {
                return false;
            }
            MapDifference mapDifference = (MapDifference) obj;
            if (!entriesOnlyOnLeft().equals(mapDifference.entriesOnlyOnLeft()) || !entriesOnlyOnRight().equals(mapDifference.entriesOnlyOnRight()) || !entriesInCommon().equals(mapDifference.entriesInCommon()) || !entriesDiffering().equals(mapDifference.entriesDiffering())) {
                z = false;
            }
            return z;
        }

        public int hashCode() {
            return Objects.hashCode(entriesOnlyOnLeft(), entriesOnlyOnRight(), entriesInCommon(), entriesDiffering());
        }

        public String toString() {
            if (areEqual()) {
                return "equal";
            }
            StringBuilder sb = new StringBuilder("not equal");
            if (!this.onlyOnLeft.isEmpty()) {
                sb.append(": only on left=");
                sb.append(this.onlyOnLeft);
            }
            if (!this.onlyOnRight.isEmpty()) {
                sb.append(": only on right=");
                sb.append(this.onlyOnRight);
            }
            if (!this.differences.isEmpty()) {
                sb.append(": value differences=");
                sb.append(this.differences);
            }
            return sb.toString();
        }
    }

    private static class SortedAsMapView<K, V> extends AsMapView<K, V> implements SortedMap<K, V> {
        SortedAsMapView(SortedSet<K> sortedSet, Function<? super K, V> function) {
            super(sortedSet, function);
        }

        /* access modifiers changed from: 0000 */
        public SortedSet<K> backingSet() {
            return (SortedSet) super.backingSet();
        }

        public Comparator<? super K> comparator() {
            return backingSet().comparator();
        }

        public Set<K> keySet() {
            return Maps.removeOnlySortedSet(backingSet());
        }

        public SortedMap<K, V> subMap(K k, K k2) {
            return Maps.asMap(backingSet().subSet(k, k2), this.function);
        }

        public SortedMap<K, V> headMap(K k) {
            return Maps.asMap(backingSet().headSet(k), this.function);
        }

        public SortedMap<K, V> tailMap(K k) {
            return Maps.asMap(backingSet().tailSet(k), this.function);
        }

        public K firstKey() {
            return backingSet().first();
        }

        public K lastKey() {
            return backingSet().last();
        }
    }

    static class SortedKeySet<K, V> extends KeySet<K, V> implements SortedSet<K> {
        SortedKeySet(SortedMap<K, V> sortedMap) {
            super(sortedMap);
        }

        /* access modifiers changed from: 0000 */
        public SortedMap<K, V> map() {
            return (SortedMap) super.map();
        }

        public Comparator<? super K> comparator() {
            return map().comparator();
        }

        public SortedSet<K> subSet(K k, K k2) {
            return new SortedKeySet(map().subMap(k, k2));
        }

        public SortedSet<K> headSet(K k) {
            return new SortedKeySet(map().headMap(k));
        }

        public SortedSet<K> tailSet(K k) {
            return new SortedKeySet(map().tailMap(k));
        }

        public K first() {
            return map().firstKey();
        }

        public K last() {
            return map().lastKey();
        }
    }

    static class SortedMapDifferenceImpl<K, V> extends MapDifferenceImpl<K, V> implements SortedMapDifference<K, V> {
        SortedMapDifferenceImpl(SortedMap<K, V> sortedMap, SortedMap<K, V> sortedMap2, SortedMap<K, V> sortedMap3, SortedMap<K, ValueDifference<V>> sortedMap4) {
            super(sortedMap, sortedMap2, sortedMap3, sortedMap4);
        }

        public SortedMap<K, ValueDifference<V>> entriesDiffering() {
            return (SortedMap) super.entriesDiffering();
        }

        public SortedMap<K, V> entriesInCommon() {
            return (SortedMap) super.entriesInCommon();
        }

        public SortedMap<K, V> entriesOnlyOnLeft() {
            return (SortedMap) super.entriesOnlyOnLeft();
        }

        public SortedMap<K, V> entriesOnlyOnRight() {
            return (SortedMap) super.entriesOnlyOnRight();
        }
    }

    static class TransformedEntriesMap<K, V1, V2> extends ImprovedAbstractMap<K, V2> {
        final Map<K, V1> fromMap;
        final EntryTransformer<? super K, ? super V1, V2> transformer;

        TransformedEntriesMap(Map<K, V1> map, EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
            this.fromMap = (Map) Preconditions.checkNotNull(map);
            this.transformer = (EntryTransformer) Preconditions.checkNotNull(entryTransformer);
        }

        public int size() {
            return this.fromMap.size();
        }

        public boolean containsKey(Object obj) {
            return this.fromMap.containsKey(obj);
        }

        public V2 get(Object obj) {
            Object obj2 = this.fromMap.get(obj);
            if (obj2 != null || this.fromMap.containsKey(obj)) {
                return this.transformer.transformEntry(obj, obj2);
            }
            return null;
        }

        public V2 remove(Object obj) {
            if (this.fromMap.containsKey(obj)) {
                return this.transformer.transformEntry(obj, this.fromMap.remove(obj));
            }
            return null;
        }

        public void clear() {
            this.fromMap.clear();
        }

        public Set<K> keySet() {
            return this.fromMap.keySet();
        }

        /* access modifiers changed from: protected */
        public Set<Entry<K, V2>> createEntrySet() {
            return new EntrySet<K, V2>() {
                /* access modifiers changed from: 0000 */
                public Map<K, V2> map() {
                    return TransformedEntriesMap.this;
                }

                public Iterator<Entry<K, V2>> iterator() {
                    return Iterators.transform(TransformedEntriesMap.this.fromMap.entrySet().iterator(), Maps.asEntryToEntryFunction(TransformedEntriesMap.this.transformer));
                }
            };
        }
    }

    static class TransformedEntriesSortedMap<K, V1, V2> extends TransformedEntriesMap<K, V1, V2> implements SortedMap<K, V2> {
        /* access modifiers changed from: protected */
        public SortedMap<K, V1> fromMap() {
            return (SortedMap) this.fromMap;
        }

        TransformedEntriesSortedMap(SortedMap<K, V1> sortedMap, EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
            super(sortedMap, entryTransformer);
        }

        public Comparator<? super K> comparator() {
            return fromMap().comparator();
        }

        public K firstKey() {
            return fromMap().firstKey();
        }

        public SortedMap<K, V2> headMap(K k) {
            return Maps.transformEntries(fromMap().headMap(k), this.transformer);
        }

        public K lastKey() {
            return fromMap().lastKey();
        }

        public SortedMap<K, V2> subMap(K k, K k2) {
            return Maps.transformEntries(fromMap().subMap(k, k2), this.transformer);
        }

        public SortedMap<K, V2> tailMap(K k) {
            return Maps.transformEntries(fromMap().tailMap(k), this.transformer);
        }
    }

    private static class UnmodifiableBiMap<K, V> extends ForwardingMap<K, V> implements BiMap<K, V>, Serializable {
        private static final long serialVersionUID = 0;
        final BiMap<? extends K, ? extends V> delegate;
        BiMap<V, K> inverse;
        final Map<K, V> unmodifiableMap;
        transient Set<V> values;

        UnmodifiableBiMap(BiMap<? extends K, ? extends V> biMap, BiMap<V, K> biMap2) {
            this.unmodifiableMap = Collections.unmodifiableMap(biMap);
            this.delegate = biMap;
            this.inverse = biMap2;
        }

        /* access modifiers changed from: protected */
        public Map<K, V> delegate() {
            return this.unmodifiableMap;
        }

        public V forcePut(K k, V v) {
            throw new UnsupportedOperationException();
        }

        public BiMap<V, K> inverse() {
            BiMap<V, K> biMap = this.inverse;
            if (biMap != null) {
                return biMap;
            }
            UnmodifiableBiMap unmodifiableBiMap = new UnmodifiableBiMap(this.delegate.inverse(), this);
            this.inverse = unmodifiableBiMap;
            return unmodifiableBiMap;
        }

        public Set<V> values() {
            Set<V> set = this.values;
            if (set != null) {
                return set;
            }
            Set<V> unmodifiableSet = Collections.unmodifiableSet(this.delegate.values());
            this.values = unmodifiableSet;
            return unmodifiableSet;
        }
    }

    static class UnmodifiableEntries<K, V> extends ForwardingCollection<Entry<K, V>> {
        private final Collection<Entry<K, V>> entries;

        UnmodifiableEntries(Collection<Entry<K, V>> collection) {
            this.entries = collection;
        }

        /* access modifiers changed from: protected */
        public Collection<Entry<K, V>> delegate() {
            return this.entries;
        }

        public Iterator<Entry<K, V>> iterator() {
            final Iterator it = super.iterator();
            return new UnmodifiableIterator<Entry<K, V>>() {
                public boolean hasNext() {
                    return it.hasNext();
                }

                public Entry<K, V> next() {
                    return Maps.unmodifiableEntry((Entry) it.next());
                }
            };
        }

        public Object[] toArray() {
            return standardToArray();
        }

        public <T> T[] toArray(T[] tArr) {
            return standardToArray(tArr);
        }
    }

    static class UnmodifiableEntrySet<K, V> extends UnmodifiableEntries<K, V> implements Set<Entry<K, V>> {
        UnmodifiableEntrySet(Set<Entry<K, V>> set) {
            super(set);
        }

        public boolean equals(Object obj) {
            return Sets.equalsImpl(this, obj);
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }

    static class ValueDifferenceImpl<V> implements ValueDifference<V> {
        private final V left;
        private final V right;

        static <V> ValueDifference<V> create(V v, V v2) {
            return new ValueDifferenceImpl(v, v2);
        }

        private ValueDifferenceImpl(V v, V v2) {
            this.left = v;
            this.right = v2;
        }

        public V leftValue() {
            return this.left;
        }

        public V rightValue() {
            return this.right;
        }

        public boolean equals(Object obj) {
            boolean z = false;
            if (!(obj instanceof ValueDifference)) {
                return false;
            }
            ValueDifference valueDifference = (ValueDifference) obj;
            if (Objects.equal(this.left, valueDifference.leftValue()) && Objects.equal(this.right, valueDifference.rightValue())) {
                z = true;
            }
            return z;
        }

        public int hashCode() {
            return Objects.hashCode(this.left, this.right);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            sb.append(this.left);
            sb.append(", ");
            sb.append(this.right);
            sb.append(")");
            return sb.toString();
        }
    }

    static class Values<K, V> extends AbstractCollection<V> {
        final Map<K, V> map;

        Values(Map<K, V> map2) {
            this.map = (Map) Preconditions.checkNotNull(map2);
        }

        /* access modifiers changed from: 0000 */
        public final Map<K, V> map() {
            return this.map;
        }

        public Iterator<V> iterator() {
            return Maps.valueIterator(map().entrySet().iterator());
        }

        public boolean remove(Object obj) {
            try {
                return super.remove(obj);
            } catch (UnsupportedOperationException unused) {
                for (Entry entry : map().entrySet()) {
                    if (Objects.equal(obj, entry.getValue())) {
                        map().remove(entry.getKey());
                        return true;
                    }
                }
                return false;
            }
        }

        public boolean removeAll(Collection<?> collection) {
            try {
                return super.removeAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException unused) {
                HashSet newHashSet = Sets.newHashSet();
                for (Entry entry : map().entrySet()) {
                    if (collection.contains(entry.getValue())) {
                        newHashSet.add(entry.getKey());
                    }
                }
                return map().keySet().removeAll(newHashSet);
            }
        }

        public boolean retainAll(Collection<?> collection) {
            try {
                return super.retainAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException unused) {
                HashSet newHashSet = Sets.newHashSet();
                for (Entry entry : map().entrySet()) {
                    if (collection.contains(entry.getValue())) {
                        newHashSet.add(entry.getKey());
                    }
                }
                return map().keySet().retainAll(newHashSet);
            }
        }

        public int size() {
            return map().size();
        }

        public boolean isEmpty() {
            return map().isEmpty();
        }

        public boolean contains(Object obj) {
            return map().containsValue(obj);
        }

        public void clear() {
            map().clear();
        }
    }

    private Maps() {
    }

    static <K> Function<Entry<K, ?>, K> keyFunction() {
        return EntryFunction.KEY;
    }

    static <V> Function<Entry<?, V>, V> valueFunction() {
        return EntryFunction.VALUE;
    }

    static <K, V> Iterator<K> keyIterator(Iterator<Entry<K, V>> it) {
        return Iterators.transform(it, keyFunction());
    }

    static <K, V> Iterator<V> valueIterator(Iterator<Entry<K, V>> it) {
        return Iterators.transform(it, valueFunction());
    }

    static <K, V> UnmodifiableIterator<V> valueIterator(final UnmodifiableIterator<Entry<K, V>> unmodifiableIterator) {
        return new UnmodifiableIterator<V>() {
            public boolean hasNext() {
                return unmodifiableIterator.hasNext();
            }

            public V next() {
                return ((Entry) unmodifiableIterator.next()).getValue();
            }
        };
    }

    @GwtCompatible(serializable = true)
    @Beta
    public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(Map<K, ? extends V> map) {
        if (map instanceof ImmutableEnumMap) {
            return (ImmutableEnumMap) map;
        }
        if (map.isEmpty()) {
            return ImmutableMap.m8687of();
        }
        for (Entry entry : map.entrySet()) {
            Preconditions.checkNotNull(entry.getKey());
            Preconditions.checkNotNull(entry.getValue());
        }
        return ImmutableEnumMap.asImmutable(new EnumMap(map));
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int i) {
        return new HashMap<>(capacity(i));
    }

    static int capacity(int i) {
        if (i >= 3) {
            return i < 1073741824 ? i + (i / 3) : BaseClientBuilder.API_PRIORITY_OTHER;
        }
        CollectPreconditions.checkNonnegative(i, "expectedSize");
        return i + 1;
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
        return new HashMap<>(map);
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
        return new LinkedHashMap<>(map);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return new MapMaker().makeMap();
    }

    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<>();
    }

    public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> sortedMap) {
        return new TreeMap<>(sortedMap);
    }

    public static <C, K extends C, V> TreeMap<K, V> newTreeMap(Comparator<C> comparator) {
        return new TreeMap<>(comparator);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> cls) {
        return new EnumMap<>((Class) Preconditions.checkNotNull(cls));
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
        return new EnumMap<>(map);
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<>();
    }

    public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> map, Map<? extends K, ? extends V> map2) {
        if (map instanceof SortedMap) {
            return difference((SortedMap) map, map2);
        }
        return difference(map, map2, Equivalence.equals());
    }

    @Beta
    public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> map, Map<? extends K, ? extends V> map2, Equivalence<? super V> equivalence) {
        Preconditions.checkNotNull(equivalence);
        HashMap newHashMap = newHashMap();
        HashMap hashMap = new HashMap(map2);
        HashMap newHashMap2 = newHashMap();
        HashMap newHashMap3 = newHashMap();
        doDifference(map, map2, equivalence, newHashMap, hashMap, newHashMap2, newHashMap3);
        return new MapDifferenceImpl(newHashMap, hashMap, newHashMap2, newHashMap3);
    }

    private static <K, V> void doDifference(Map<? extends K, ? extends V> map, Map<? extends K, ? extends V> map2, Equivalence<? super V> equivalence, Map<K, V> map3, Map<K, V> map4, Map<K, V> map5, Map<K, ValueDifference<V>> map6) {
        for (Entry entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (map2.containsKey(key)) {
                Object remove = map4.remove(key);
                if (equivalence.equivalent(value, remove)) {
                    map5.put(key, value);
                } else {
                    map6.put(key, ValueDifferenceImpl.create(value, remove));
                }
            } else {
                map3.put(key, value);
            }
        }
    }

    /* access modifiers changed from: private */
    public static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
        if (map instanceof SortedMap) {
            return Collections.unmodifiableSortedMap((SortedMap) map);
        }
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> SortedMapDifference<K, V> difference(SortedMap<K, ? extends V> sortedMap, Map<? extends K, ? extends V> map) {
        Preconditions.checkNotNull(sortedMap);
        Preconditions.checkNotNull(map);
        Comparator orNaturalOrder = orNaturalOrder(sortedMap.comparator());
        TreeMap newTreeMap = newTreeMap(orNaturalOrder);
        TreeMap newTreeMap2 = newTreeMap(orNaturalOrder);
        newTreeMap2.putAll(map);
        TreeMap newTreeMap3 = newTreeMap(orNaturalOrder);
        TreeMap newTreeMap4 = newTreeMap(orNaturalOrder);
        doDifference(sortedMap, map, Equivalence.equals(), newTreeMap, newTreeMap2, newTreeMap3, newTreeMap4);
        return new SortedMapDifferenceImpl(newTreeMap, newTreeMap2, newTreeMap3, newTreeMap4);
    }

    static <E> Comparator<? super E> orNaturalOrder(Comparator<? super E> comparator) {
        return comparator != null ? comparator : Ordering.natural();
    }

    @Beta
    public static <K, V> Map<K, V> asMap(Set<K> set, Function<? super K, V> function) {
        if (set instanceof SortedSet) {
            return asMap((SortedSet) set, function);
        }
        return new AsMapView(set, function);
    }

    @Beta
    public static <K, V> SortedMap<K, V> asMap(SortedSet<K> sortedSet, Function<? super K, V> function) {
        return Platform.mapsAsMapSortedSet(sortedSet, function);
    }

    static <K, V> SortedMap<K, V> asMapSortedIgnoreNavigable(SortedSet<K> sortedSet, Function<? super K, V> function) {
        return new SortedAsMapView(sortedSet, function);
    }

    static <K, V> Iterator<Entry<K, V>> asMapEntryIterator(Set<K> set, final Function<? super K, V> function) {
        return new TransformedIterator<K, Entry<K, V>>(set.iterator()) {
            /* access modifiers changed from: 0000 */
            public Entry<K, V> transform(K k) {
                return Maps.immutableEntry(k, function.apply(k));
            }
        };
    }

    /* access modifiers changed from: private */
    public static <E> Set<E> removeOnlySet(final Set<E> set) {
        return new ForwardingSet<E>() {
            /* access modifiers changed from: protected */
            public Set<E> delegate() {
                return set;
            }

            public boolean add(E e) {
                throw new UnsupportedOperationException();
            }

            public boolean addAll(Collection<? extends E> collection) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /* access modifiers changed from: private */
    public static <E> SortedSet<E> removeOnlySortedSet(final SortedSet<E> sortedSet) {
        return new ForwardingSortedSet<E>() {
            /* access modifiers changed from: protected */
            public SortedSet<E> delegate() {
                return sortedSet;
            }

            public boolean add(E e) {
                throw new UnsupportedOperationException();
            }

            public boolean addAll(Collection<? extends E> collection) {
                throw new UnsupportedOperationException();
            }

            public SortedSet<E> headSet(E e) {
                return Maps.removeOnlySortedSet(super.headSet(e));
            }

            public SortedSet<E> subSet(E e, E e2) {
                return Maps.removeOnlySortedSet(super.subSet(e, e2));
            }

            public SortedSet<E> tailSet(E e) {
                return Maps.removeOnlySortedSet(super.tailSet(e));
            }
        };
    }

    @Beta
    public static <K, V> ImmutableMap<K, V> toMap(Iterable<K> iterable, Function<? super K, V> function) {
        return toMap(iterable.iterator(), function);
    }

    @Beta
    public static <K, V> ImmutableMap<K, V> toMap(Iterator<K> it, Function<? super K, V> function) {
        Preconditions.checkNotNull(function);
        LinkedHashMap newLinkedHashMap = newLinkedHashMap();
        while (it.hasNext()) {
            Object next = it.next();
            newLinkedHashMap.put(next, function.apply(next));
        }
        return ImmutableMap.copyOf(newLinkedHashMap);
    }

    public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterable<V> iterable, Function<? super V, K> function) {
        return uniqueIndex(iterable.iterator(), function);
    }

    public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterator<V> it, Function<? super V, K> function) {
        Preconditions.checkNotNull(function);
        Builder builder = ImmutableMap.builder();
        while (it.hasNext()) {
            Object next = it.next();
            builder.put(function.apply(next), next);
        }
        return builder.build();
    }

    @GwtIncompatible("java.util.Properties")
    public static ImmutableMap<String, String> fromProperties(Properties properties) {
        Builder builder = ImmutableMap.builder();
        Enumeration propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String str = (String) propertyNames.nextElement();
            builder.put(str, properties.getProperty(str));
        }
        return builder.build();
    }

    @GwtCompatible(serializable = true)
    public static <K, V> Entry<K, V> immutableEntry(K k, V v) {
        return new ImmutableEntry(k, v);
    }

    static <K, V> Set<Entry<K, V>> unmodifiableEntrySet(Set<Entry<K, V>> set) {
        return new UnmodifiableEntrySet(Collections.unmodifiableSet(set));
    }

    static <K, V> Entry<K, V> unmodifiableEntry(final Entry<? extends K, ? extends V> entry) {
        Preconditions.checkNotNull(entry);
        return new AbstractMapEntry<K, V>() {
            public K getKey() {
                return entry.getKey();
            }

            public V getValue() {
                return entry.getValue();
            }
        };
    }

    @Beta
    public static <A, B> Converter<A, B> asConverter(BiMap<A, B> biMap) {
        return new BiMapConverter(biMap);
    }

    public static <K, V> BiMap<K, V> synchronizedBiMap(BiMap<K, V> biMap) {
        return Synchronized.biMap(biMap, null);
    }

    public static <K, V> BiMap<K, V> unmodifiableBiMap(BiMap<? extends K, ? extends V> biMap) {
        return new UnmodifiableBiMap(biMap, null);
    }

    public static <K, V1, V2> Map<K, V2> transformValues(Map<K, V1> map, Function<? super V1, V2> function) {
        return transformEntries(map, asEntryTransformer(function));
    }

    public static <K, V1, V2> SortedMap<K, V2> transformValues(SortedMap<K, V1> sortedMap, Function<? super V1, V2> function) {
        return transformEntries(sortedMap, asEntryTransformer(function));
    }

    public static <K, V1, V2> Map<K, V2> transformEntries(Map<K, V1> map, EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        if (map instanceof SortedMap) {
            return transformEntries((SortedMap) map, entryTransformer);
        }
        return new TransformedEntriesMap(map, entryTransformer);
    }

    public static <K, V1, V2> SortedMap<K, V2> transformEntries(SortedMap<K, V1> sortedMap, EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        return Platform.mapsTransformEntriesSortedMap(sortedMap, entryTransformer);
    }

    static <K, V1, V2> SortedMap<K, V2> transformEntriesIgnoreNavigable(SortedMap<K, V1> sortedMap, EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        return new TransformedEntriesSortedMap(sortedMap, entryTransformer);
    }

    static <K, V1, V2> EntryTransformer<K, V1, V2> asEntryTransformer(final Function<? super V1, V2> function) {
        Preconditions.checkNotNull(function);
        return new EntryTransformer<K, V1, V2>() {
            public V2 transformEntry(K k, V1 v1) {
                return function.apply(v1);
            }
        };
    }

    static <K, V1, V2> Function<V1, V2> asValueToValueFunction(final EntryTransformer<? super K, V1, V2> entryTransformer, final K k) {
        Preconditions.checkNotNull(entryTransformer);
        return new Function<V1, V2>() {
            public V2 apply(V1 v1) {
                return entryTransformer.transformEntry(k, v1);
            }
        };
    }

    static <K, V1, V2> Function<Entry<K, V1>, V2> asEntryToValueFunction(final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        Preconditions.checkNotNull(entryTransformer);
        return new Function<Entry<K, V1>, V2>() {
            public V2 apply(Entry<K, V1> entry) {
                return entryTransformer.transformEntry(entry.getKey(), entry.getValue());
            }
        };
    }

    static <V2, K, V1> Entry<K, V2> transformEntry(final EntryTransformer<? super K, ? super V1, V2> entryTransformer, final Entry<K, V1> entry) {
        Preconditions.checkNotNull(entryTransformer);
        Preconditions.checkNotNull(entry);
        return new AbstractMapEntry<K, V2>() {
            public K getKey() {
                return entry.getKey();
            }

            public V2 getValue() {
                return entryTransformer.transformEntry(entry.getKey(), entry.getValue());
            }
        };
    }

    static <K, V1, V2> Function<Entry<K, V1>, Entry<K, V2>> asEntryToEntryFunction(final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        Preconditions.checkNotNull(entryTransformer);
        return new Function<Entry<K, V1>, Entry<K, V2>>() {
            public Entry<K, V2> apply(Entry<K, V1> entry) {
                return Maps.transformEntry(entryTransformer, entry);
            }
        };
    }

    static <K> Predicate<Entry<K, ?>> keyPredicateOnEntries(Predicate<? super K> predicate) {
        return Predicates.compose(predicate, keyFunction());
    }

    static <V> Predicate<Entry<?, V>> valuePredicateOnEntries(Predicate<? super V> predicate) {
        return Predicates.compose(predicate, valueFunction());
    }

    public static <K, V> Map<K, V> filterKeys(Map<K, V> map, Predicate<? super K> predicate) {
        if (map instanceof SortedMap) {
            return filterKeys((SortedMap) map, predicate);
        }
        if (map instanceof BiMap) {
            return filterKeys((BiMap) map, predicate);
        }
        Preconditions.checkNotNull(predicate);
        Predicate keyPredicateOnEntries = keyPredicateOnEntries(predicate);
        return map instanceof AbstractFilteredMap ? filterFiltered((AbstractFilteredMap) map, keyPredicateOnEntries) : new FilteredKeyMap<>((Map) Preconditions.checkNotNull(map), predicate, keyPredicateOnEntries);
    }

    public static <K, V> SortedMap<K, V> filterKeys(SortedMap<K, V> sortedMap, Predicate<? super K> predicate) {
        return filterEntries(sortedMap, keyPredicateOnEntries(predicate));
    }

    public static <K, V> BiMap<K, V> filterKeys(BiMap<K, V> biMap, Predicate<? super K> predicate) {
        Preconditions.checkNotNull(predicate);
        return filterEntries(biMap, keyPredicateOnEntries(predicate));
    }

    public static <K, V> Map<K, V> filterValues(Map<K, V> map, Predicate<? super V> predicate) {
        if (map instanceof SortedMap) {
            return filterValues((SortedMap) map, predicate);
        }
        if (map instanceof BiMap) {
            return filterValues((BiMap) map, predicate);
        }
        return filterEntries(map, valuePredicateOnEntries(predicate));
    }

    public static <K, V> SortedMap<K, V> filterValues(SortedMap<K, V> sortedMap, Predicate<? super V> predicate) {
        return filterEntries(sortedMap, valuePredicateOnEntries(predicate));
    }

    public static <K, V> BiMap<K, V> filterValues(BiMap<K, V> biMap, Predicate<? super V> predicate) {
        return filterEntries(biMap, valuePredicateOnEntries(predicate));
    }

    public static <K, V> Map<K, V> filterEntries(Map<K, V> map, Predicate<? super Entry<K, V>> predicate) {
        if (map instanceof SortedMap) {
            return filterEntries((SortedMap) map, predicate);
        }
        if (map instanceof BiMap) {
            return filterEntries((BiMap) map, predicate);
        }
        Preconditions.checkNotNull(predicate);
        return map instanceof AbstractFilteredMap ? filterFiltered((AbstractFilteredMap) map, predicate) : new FilteredEntryMap<>((Map) Preconditions.checkNotNull(map), predicate);
    }

    public static <K, V> SortedMap<K, V> filterEntries(SortedMap<K, V> sortedMap, Predicate<? super Entry<K, V>> predicate) {
        return Platform.mapsFilterSortedMap(sortedMap, predicate);
    }

    static <K, V> SortedMap<K, V> filterSortedIgnoreNavigable(SortedMap<K, V> sortedMap, Predicate<? super Entry<K, V>> predicate) {
        Preconditions.checkNotNull(predicate);
        return sortedMap instanceof FilteredEntrySortedMap ? filterFiltered((FilteredEntrySortedMap) sortedMap, predicate) : new FilteredEntrySortedMap((SortedMap) Preconditions.checkNotNull(sortedMap), predicate);
    }

    public static <K, V> BiMap<K, V> filterEntries(BiMap<K, V> biMap, Predicate<? super Entry<K, V>> predicate) {
        Preconditions.checkNotNull(biMap);
        Preconditions.checkNotNull(predicate);
        return biMap instanceof FilteredEntryBiMap ? filterFiltered((FilteredEntryBiMap) biMap, predicate) : new FilteredEntryBiMap(biMap, predicate);
    }

    private static <K, V> Map<K, V> filterFiltered(AbstractFilteredMap<K, V> abstractFilteredMap, Predicate<? super Entry<K, V>> predicate) {
        return new FilteredEntryMap(abstractFilteredMap.unfiltered, Predicates.and(abstractFilteredMap.predicate, predicate));
    }

    private static <K, V> SortedMap<K, V> filterFiltered(FilteredEntrySortedMap<K, V> filteredEntrySortedMap, Predicate<? super Entry<K, V>> predicate) {
        return new FilteredEntrySortedMap(filteredEntrySortedMap.sortedMap(), Predicates.and(filteredEntrySortedMap.predicate, predicate));
    }

    private static <K, V> BiMap<K, V> filterFiltered(FilteredEntryBiMap<K, V> filteredEntryBiMap, Predicate<? super Entry<K, V>> predicate) {
        return new FilteredEntryBiMap(filteredEntryBiMap.unfiltered(), Predicates.and(filteredEntryBiMap.predicate, predicate));
    }

    private static <K, V> Entry<K, V> unmodifiableOrNull(Entry<K, V> entry) {
        if (entry == null) {
            return null;
        }
        return unmodifiableEntry(entry);
    }

    static <V> V safeGet(Map<?, V> map, Object obj) {
        Preconditions.checkNotNull(map);
        try {
            return map.get(obj);
        } catch (ClassCastException unused) {
            return null;
        } catch (NullPointerException unused2) {
            return null;
        }
    }

    static boolean safeContainsKey(Map<?, ?> map, Object obj) {
        Preconditions.checkNotNull(map);
        try {
            return map.containsKey(obj);
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused2) {
            return false;
        }
    }

    static <V> V safeRemove(Map<?, V> map, Object obj) {
        Preconditions.checkNotNull(map);
        try {
            return map.remove(obj);
        } catch (ClassCastException unused) {
            return null;
        } catch (NullPointerException unused2) {
            return null;
        }
    }

    static boolean containsKeyImpl(Map<?, ?> map, Object obj) {
        return Iterators.contains(keyIterator(map.entrySet().iterator()), obj);
    }

    static boolean containsValueImpl(Map<?, ?> map, Object obj) {
        return Iterators.contains(valueIterator(map.entrySet().iterator()), obj);
    }

    static <K, V> boolean containsEntryImpl(Collection<Entry<K, V>> collection, Object obj) {
        if (!(obj instanceof Entry)) {
            return false;
        }
        return collection.contains(unmodifiableEntry((Entry) obj));
    }

    static <K, V> boolean removeEntryImpl(Collection<Entry<K, V>> collection, Object obj) {
        if (!(obj instanceof Entry)) {
            return false;
        }
        return collection.remove(unmodifiableEntry((Entry) obj));
    }

    static boolean equalsImpl(Map<?, ?> map, Object obj) {
        if (map == obj) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        return map.entrySet().equals(((Map) obj).entrySet());
    }

    static String toStringImpl(Map<?, ?> map) {
        StringBuilder newStringBuilderForCollection = Collections2.newStringBuilderForCollection(map.size());
        newStringBuilderForCollection.append('{');
        STANDARD_JOINER.appendTo(newStringBuilderForCollection, map);
        newStringBuilderForCollection.append('}');
        return newStringBuilderForCollection.toString();
    }

    static <K, V> void putAllImpl(Map<K, V> map, Map<? extends K, ? extends V> map2) {
        for (Entry entry : map2.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

    static <K> K keyOrNull(Entry<K, ?> entry) {
        if (entry == null) {
            return null;
        }
        return entry.getKey();
    }

    static <V> V valueOrNull(Entry<?, V> entry) {
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }
}
