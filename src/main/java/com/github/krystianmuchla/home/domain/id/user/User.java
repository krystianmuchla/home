package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.id.user.error.UserValidationException;

import java.util.UUID;

public class User {
    public final UUID id;
    public final String name;
    public final Time creationTime;
    public final Time modificationTime;
    public final Integer version;

    public User(UUID id, String name, Time creationTime, Time modificationTime, Integer version) throws UserValidationException {
        this.id = id;
        this.name = name;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
        UserValidator.validate(this);
    }

    public User(String name) throws UserValidationException {
        this(UUID.randomUUID(), name, null, null, null);
    }
}
