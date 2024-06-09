package com.github.krystianmuchla.home.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    void add(final K key, final V value);

    void addAll(final K key, final List<V> values);

    Optional<V> getFirst(final K key);
}
