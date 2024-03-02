package com.github.krystianmuchla.home.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MultiValueHashMap<K, V> extends HashMap<K, Collection<V>> implements MultiValueMap<K, V> {
    @Override
    public void add(final K key, final V value) {
        this.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    @Override
    public void addAll(final K key, final Collection<V> values) {
        this.computeIfAbsent(key, k -> new ArrayList<>()).addAll(values);
    }

    public static <T, S> MultiValueHashMap<T, S> of(final T key, final S value) {
        return new MultiValueHashMap<>() {{
            put(key, new ArrayList<>() {{
                add(value);
            }});
        }};
    }
}
