package com.github.krystianmuchla.home.drive;

public enum EntryType {
    DIR, FILE;

    public String asClass() {
        return this.name().toLowerCase();
    }
}
