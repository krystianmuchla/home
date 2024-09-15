package com.github.krystianmuchla.home.db;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    protected Map<String, Object> updates = new HashMap<>();

    public Map<String, Object> consumeUpdates() {
        var updates = this.updates;
        this.updates = new HashMap<>();
        return updates;
    }
}
