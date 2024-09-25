package com.github.krystianmuchla.home.application.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionService {
    public static String join(String delimiter, Collection<?> collection) {
        return StreamService.join(delimiter, collection.stream());
    }

    public static <K, V> Map<K, V> toMap(Function<V, K> identifier, Collection<V> values) {
        return values.stream().collect(Collectors.toMap(identifier, Function.identity()));
    }
}
