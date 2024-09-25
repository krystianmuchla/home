package com.github.krystianmuchla.home.application.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MultiValueHashMap<K, V> extends HashMap<K, List<V>> implements MultiValueMap<K, V> {
    @Override
    public void add(K key, V value) {
        this.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    @Override
    public void addAll(K key, List<V> values) {
        this.computeIfAbsent(key, k -> new ArrayList<>()).addAll(values);
    }

    @Override
    @SafeVarargs
    public final void addAll(K key, V... values) {
        addAll(key, List.of(values));
    }

    @Override
    public Optional<V> getFirst(K key) {
        var value = get(key);
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(value.getFirst());
    }

    @Override
    public Optional<List<V>> maybeGet(K key) {
        return Optional.ofNullable(get(key));
    }

    @SafeVarargs
    public static <T, S> MultiValueHashMap<T, S> of(T key, S... values) {
        return new MultiValueHashMap<>() {{
            addAll(key, List.of(values));
        }};
    }
}
