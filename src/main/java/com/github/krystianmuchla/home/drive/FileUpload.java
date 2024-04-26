package com.github.krystianmuchla.home.drive;

import java.io.InputStream;

public record FileUpload(String fileName, InputStream inputStream) {
}
