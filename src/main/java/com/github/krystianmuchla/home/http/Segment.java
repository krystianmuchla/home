package com.github.krystianmuchla.home.http;

import java.util.Arrays;
import java.util.List;

public class Segment {
    public static List<String> segments(final String path) {
        return Arrays.stream(path.split("/")).filter(segment -> !segment.isBlank()).toList();
    }
}
