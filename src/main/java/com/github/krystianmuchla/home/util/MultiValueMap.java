package com.github.krystianmuchla.home.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    void add(K key, V value);

    void addAll(K key, List<V> values);

    void addAll(K key, V... values);

    Optional<V> getFirst(K key);

    Optional<List<V>> getAll(K key);
}
