package com.github.krystianmuchla.home.domain.drive.file;

import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;

public class FileUpdate {
    public final String name;

    public FileUpdate(String name) throws FileValidationException {
        var validator = new FileValidator();
        if (name != null) {
            validator.validateName(name);
        }
        validator.validate();
        this.name = name;
    }
}
