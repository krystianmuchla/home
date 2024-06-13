package com.github.krystianmuchla.home.util;

import java.util.Collection;

public class CollectionService {
    public static String join(final String delimiter, final Collection<?> collection) {
        return StreamService.join(delimiter, collection.stream());
    }
}
