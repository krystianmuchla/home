package com.github.krystianmuchla.home.domain.drive.directory;

import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationException;

public class DirectoryUpdate {
    public final String name;

    public DirectoryUpdate(String name) throws DirectoryValidationException {
        var validator = new DirectoryValidator();
        if (name != null) {
            validator.validateName(name);
        }
        validator.validate();
        this.name = name;
    }
}
