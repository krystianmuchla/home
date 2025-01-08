package com.github.krystianmuchla.home.domain.core;

import java.util.HashMap;
import java.util.Map;

public class Model<T> {
    protected Map<T, Object> updates = new HashMap<>();

    public Map<T, Object> consumeUpdates() {
        var updates = this.updates;
        this.updates = new HashMap<>();
        return updates;
    }
}
