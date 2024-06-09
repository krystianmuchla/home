package com.github.krystianmuchla.home.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MultiValueHashMap<K, V> extends HashMap<K, List<V>> implements MultiValueMap<K, V> {
    @Override
    public void add(final K key, final V value) {
        this.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    @Override
    public void addAll(final K key, final List<V> values) {
        this.computeIfAbsent(key, k -> new ArrayList<>()).addAll(values);
    }

    @Override
    public Optional<V> getFirst(final K key) {
        final var value = get(key);
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(value.getFirst());
    }

    public static <T, S> MultiValueHashMap<T, S> of(final T key, final S value) {
        return new MultiValueHashMap<>() {{
            put(key, new ArrayList<>() {{
                add(value);
            }});
        }};
    }
}
