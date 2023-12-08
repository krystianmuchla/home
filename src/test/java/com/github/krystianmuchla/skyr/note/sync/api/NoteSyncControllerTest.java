package com.github.krystianmuchla.skyr.note.sync.api;

import com.github.krystianmuchla.skyr.IntegrationTest;
import com.github.krystianmuchla.skyr.note.Note;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class NoteSyncControllerTest extends IntegrationTest {
    private static UUID initialSyncId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void beforeAllTests(@Autowired final JdbcTemplate jdbcTemplate) {
        initialSyncId = jdbcTemplate.query(
                "SELECT sync_id FROM note_sync",
                (resultSet, rowNum) -> UUID.fromString(resultSet.getString("sync_id"))
        ).getFirst();
    }

    @AfterEach
    void afterEachTest() {
        deleteNotes();
        updateSyncId(initialSyncId);
    }

    @Test
    void shouldSyncNotes() throws Exception {
        final var externalSyncId = UUID.fromString("ac50944e-0677-445e-92a6-1b0bfd14f343");
        final var externalNotes = List.of(
                new Note(
                        UUID.fromString("4d8af443-bfa9-4d47-a886-b1ddc82a958d"),
                        "External note",
                        "4d8af443-bfa9-4d47-a886-b1ddc82a958d",
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                        UUID.fromString("cb8b51f8-63e5-4964-94e4-0b3b7944e7d4"),
                        "External note with same id and earlier modification time",
                        "cb8b51f8-63e5-4964-94e4-0b3b7944e7d4",
                        Instant.parse("2000-01-01T00:00:00Z"),
                        Instant.parse("2000-01-01T00:00:00Z")
                ),
                new Note(
                        UUID.fromString("2109af10-c870-4e8d-8f53-7220d693ca78"),
                        "External note with same id and same modification time",
                        "2109af10-c870-4e8d-8f53-7220d693ca78",
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                        UUID.fromString("2f6772bc-2a58-4e25-9318-3a60d4fb52dc"),
                        "External note with same id and later modification time",
                        "2f6772bc-2a58-4e25-9318-3a60d4fb52dc",
                        Instant.parse("2020-12-31T23:59:59Z"),
                        Instant.parse("2020-12-31T23:59:59Z")
                ),
                new Note(
                        UUID.fromString("fb9e3586-9e71-49dd-a4f7-a6ed86a8999e"),
                        "External note with no content",
                        null,
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                        UUID.fromString("7bf721b5-30de-4697-92aa-1a6b61a418a9"),
                        "External note with same id and no content and earlier modification time",
                        null,
                        Instant.parse("2000-01-01T00:00:00Z"),
                        Instant.parse("2000-01-01T00:00:00Z")
                ),
                new Note(
                        UUID.fromString("c0dc5769-d1df-4e4f-8a49-87e3d8651c6e"),
                        "External note with same id and no content and same modification time",
                        null,
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                        UUID.fromString("caa96b92-1469-43cd-b58e-59a4c271a270"),
                        "External note with same id and no content and later modification time",
                        null,
                        Instant.parse("2020-12-31T23:59:59Z"),
                        Instant.parse("2020-12-31T23:59:59Z")
                )
        );
        final var syncId = UUID.fromString("e4af1370-c730-4842-a990-d52380bf6661");
        final var notes = List.of(
                new Note(
                        UUID.fromString("0981a57b-9ccd-455e-956d-2daf39e45480"),
                        "0981a57b-9ccd-455e-956d-2daf39e45480",
                        "0981a57b-9ccd-455e-956d-2daf39e45480",
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                        UUID.fromString("cb8b51f8-63e5-4964-94e4-0b3b7944e7d4"),
                        "cb8b51f8-63e5-4964-94e4-0b3b7944e7d4",
                        "cb8b51f8-63e5-4964-94e4-0b3b7944e7d4",
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                        UUID.fromString("2109af10-c870-4e8d-8f53-7220d693ca78"),
                        "2109af10-c870-4e8d-8f53-7220d693ca78",
                        "2109af10-c870-4e8d-8f53-7220d693ca78",
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                        UUID.fromString("2f6772bc-2a58-4e25-9318-3a60d4fb52dc"),
                        "2f6772bc-2a58-4e25-9318-3a60d4fb52dc",
                        "2f6772bc-2a58-4e25-9318-3a60d4fb52dc",
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                        UUID.fromString("7bf721b5-30de-4697-92aa-1a6b61a418a9"),
                        "7bf721b5-30de-4697-92aa-1a6b61a418a9",
                        "7bf721b5-30de-4697-92aa-1a6b61a418a9",
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                        UUID.fromString("c0dc5769-d1df-4e4f-8a49-87e3d8651c6e"),
                        "c0dc5769-d1df-4e4f-8a49-87e3d8651c6e",
                        "c0dc5769-d1df-4e4f-8a49-87e3d8651c6e",
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                        UUID.fromString("caa96b92-1469-43cd-b58e-59a4c271a270"),
                        "caa96b92-1469-43cd-b58e-59a4c271a270",
                        "caa96b92-1469-43cd-b58e-59a4c271a270",
                        Instant.parse("2010-10-10T10:10:10Z"),
                        Instant.parse("2010-10-10T10:10:10Z")
                )
        );
        updateSyncId(syncId);
        createNotes(notes);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
                .contentType("application/json")
                .content("""
                        {
                          "syncId": "%s",
                          "modifiedNotes": [
                            {
                              "id": "%s",
                              "title": "%s",
                              "content": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            },
                            {
                              "id": "%s",
                              "title": "%s",
                              "content": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            },
                            {
                              "id": "%s",
                              "title": "%s",
                              "content": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            },
                            {
                              "id": "%s",
                              "title": "%s",
                              "content": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            },
                            {
                              "id": "%s",
                              "title": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            },
                            {
                              "id": "%s",
                              "title": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            },
                            {
                              "id": "%s",
                              "title": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            },
                            {
                              "id": "%s",
                              "title": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            }
                          ]
                        }
                        """.formatted(externalSyncId,
                        externalNotes.get(0).id(),
                        externalNotes.get(0).title(),
                        externalNotes.get(0).content(),
                        externalNotes.get(0).creationTime(),
                        externalNotes.get(0).modificationTime(),
                        externalNotes.get(1).id(),
                        externalNotes.get(1).title(),
                        externalNotes.get(1).content(),
                        externalNotes.get(1).creationTime(),
                        externalNotes.get(1).modificationTime(),
                        externalNotes.get(2).id(),
                        externalNotes.get(2).title(),
                        externalNotes.get(2).content(),
                        externalNotes.get(2).creationTime(),
                        externalNotes.get(2).modificationTime(),
                        externalNotes.get(3).id(),
                        externalNotes.get(3).title(),
                        externalNotes.get(3).content(),
                        externalNotes.get(3).creationTime(),
                        externalNotes.get(3).modificationTime(),
                        externalNotes.get(4).id(),
                        externalNotes.get(4).title(),
                        externalNotes.get(4).creationTime(),
                        externalNotes.get(4).modificationTime(),
                        externalNotes.get(5).id(),
                        externalNotes.get(5).title(),
                        externalNotes.get(5).creationTime(),
                        externalNotes.get(5).modificationTime(),
                        externalNotes.get(6).id(),
                        externalNotes.get(6).title(),
                        externalNotes.get(6).creationTime(),
                        externalNotes.get(6).modificationTime(),
                        externalNotes.get(7).id(),
                        externalNotes.get(7).title(),
                        externalNotes.get(7).creationTime(),
                        externalNotes.get(7).modificationTime())
                )
        );

        final var responseContent = resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.modifiedNotes", hasSize(5)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "$.syncId"));
        assertThat(syncIdResponse).isNotEqualTo(syncId).isNotEqualTo(externalSyncId);
        final var modifiedNotesResponse = getModifiedNotes(responseContent);
        assertThat(modifiedNotesResponse).hasSize(5).contains(
                notes.get(0),
                notes.get(1),
                notes.get(2),
                notes.get(4),
                notes.get(5)
        );
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(7).contains(
                externalNotes.get(0),
                externalNotes.get(3),
                notes.get(0),
                notes.get(1),
                notes.get(2),
                notes.get(4),
                notes.get(5)
        );
    }

    @Test
    void shouldSyncNotesWithEmptyExternalNotes() throws Exception {
        final var externalSyncId = UUID.fromString("465049ee-170c-4054-aabd-cd67c7185e9e");
        final var syncId = UUID.fromString("52f884ad-262d-436b-99bf-9f2f4d39287a");
        final var note = new Note(
                UUID.fromString("2642208c-09bb-44fd-b036-fab080035974"),
                "2642208c-09bb-44fd-b036-fab080035974",
                "2642208c-09bb-44fd-b036-fab080035974",
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
        );
        updateSyncId(syncId);
        createNote(note);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
                .contentType("application/json")
                .content("""
                        {
                          "syncId": "%s",
                          "modifiedNotes": []
                        }
                        """.formatted(externalSyncId))
        );

        final var responseContent = resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.syncId", equalTo(syncId.toString())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var modifiedNotesResponse = getModifiedNotes(responseContent);
        assertThat(modifiedNotesResponse).hasSize(1).contains(note);
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(1);
        assertThat(notesDb.getFirst()).isEqualTo(note);
    }

    @Test
    void shouldSkipNoteSyncForSameSyncIds() throws Exception {
        final var syncId = UUID.fromString("5770e671-490b-4331-ac7f-fd22abe92841");
        final var note = new Note(
                UUID.fromString("186c56c5-dfb9-41fe-a322-a196e30f53a7"),
                "186c56c5-dfb9-41fe-a322-a196e30f53a7",
                "186c56c5-dfb9-41fe-a322-a196e30f53a7",
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
        );
        updateSyncId(syncId);
        createNote(note);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
                .contentType("application/json")
                .content("""
                        {
                          "syncId": "%s",
                          "modifiedNotes": []
                        }
                        """.formatted(syncId))
        );

        resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.syncId", equalTo(syncId.toString())))
                .andExpect(jsonPath("$.modifiedNotes", hasSize(0)));
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(1);
        assertThat(notesDb.getFirst()).isEqualTo(note);
    }

    private void updateSyncId(final UUID syncId) {
        jdbcTemplate.update("UPDATE note_sync SET sync_id = ?", syncId.toString());
    }

    private void createNote(final Note note) {
        jdbcTemplate.update(
                "INSERT INTO note VALUES (?, ?, ?, ?, ?)",
                note.id().toString(),
                note.title(),
                note.content(),
                Timestamp.valueOf(LocalDateTime.ofInstant(note.creationTime(), ZoneOffset.UTC)).toString(),
                Timestamp.valueOf(LocalDateTime.ofInstant(note.modificationTime(), ZoneOffset.UTC)).toString()
        );
    }

    private void createNotes(final List<Note> notes) {
        notes.forEach(this::createNote);
    }

    private List<Note> readNotes() {
        return jdbcTemplate.query(
                "SELECT * FROM note order by modification_time",
                (resultSet, rowNum) -> new Note(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getTimestamp("creation_time").toLocalDateTime().toInstant(ZoneOffset.UTC),
                        resultSet.getTimestamp("modification_time").toLocalDateTime().toInstant(ZoneOffset.UTC)
                )
        );
    }

    private void deleteNotes() {
        jdbcTemplate.update("DELETE FROM note");
    }

    private List<Note> getModifiedNotes(final String responseContent) throws IOException {
        final Map<String, Object> responseMap = new ObjectMapper().readValue(
                responseContent,
                new TypeReference<Map<String, Object>>() {
                }
        );
        final List<Map<String, Object>> modifiedNotes = (List<Map<String, Object>>) responseMap.get("modifiedNotes");
        return modifiedNotes.stream().map(modifiedNote -> new Note(
                UUID.fromString((String) modifiedNote.get("id")),
                (String) modifiedNote.get("title"),
                (String) modifiedNote.get("content"),
                Instant.parse((String) modifiedNote.get("creationTime")),
                Instant.parse((String) modifiedNote.get("modificationTime"))
        )).toList();
    }
}
