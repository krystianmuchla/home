package com.github.krystianmuchla.home.util;

import java.util.Collection;
import java.util.Map;

public interface MultiValueMap<T, U> extends Map<T, Collection<U>> {
    void add(final T key, final U value);

    void addAll(final T key, final Collection<U> values);
}
