package com.github.krystianmuchla.home.domain.id.accessdata;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationException;

import java.util.UUID;

public class AccessData {
    public final UUID id;
    public final UUID userId;
    public final String login;
    public final byte[] salt;
    public final byte[] secret;
    public final Time creationTime;
    public final Time modificationTime;
    public final Integer version;

    public AccessData(
        UUID id,
        UUID userId,
        String login,
        byte[] salt,
        byte[] secret,
        Time creationTime,
        Time modificationTime,
        Integer version
    ) throws AccessDataValidationException {
        this.id = id;
        this.userId = userId;
        this.login = login;
        this.salt = salt;
        this.secret = secret;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
        AccessDataValidator.validate(this);
    }

    public AccessData(UUID userId, String login, byte[] salt, byte[] secret) throws AccessDataValidationException {
        this(UUID.randomUUID(), userId, login, salt, secret, new Time(), new Time(), 1);
    }
}
