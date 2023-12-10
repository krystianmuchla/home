package com.github.krystianmuchla.skyr.note.grave;

import com.github.krystianmuchla.skyr.InstantFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "note-grave.cleaner", value = "enabled", havingValue = "true")
public class NoteGraveCleaner {
    private final NoteGraveDao noteGraveDao;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void cleanOldGraves() {
        final var creationTimeThreshold = InstantFactory.create().minus(1, ChronoUnit.MONTHS);
        noteGraveDao.delete(creationTimeThreshold);
    }
}
