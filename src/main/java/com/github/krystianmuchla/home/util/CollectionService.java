package com.github.krystianmuchla.home.util;

import java.util.Collection;

public class CollectionService {
    public static String join(String delimiter, Collection<?> collection) {
        return StreamService.join(delimiter, collection.stream());
    }
}
