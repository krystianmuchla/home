package com.github.krystianmuchla.skyr.note.api;

import com.github.krystianmuchla.skyr.IntegrationTest;
import com.github.krystianmuchla.skyr.note.Note;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class NoteControllerTest extends IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void afterEachTest() {
        deleteNotes();
    }

    @Test
    void shouldAddNote() throws Exception {
        final var noteTitle = "Note title";
        final var noteContent = "Note content";

        final var resultActions = mockMvc.perform(post("/api/notes")
                .contentType("application/json")
                .content("""
                        {
                          "title": "%s",
                          "content": "%s"
                        }
                        """.formatted(noteTitle, noteContent)
                )
        );

        final var responseContent = resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final String noteId = JsonPath.read(responseContent, "$.id");
        final var notes = readNotes();
        assertThat(notes).hasSize(1);
        final var note = notes.getFirst();
        assertThat(note.id()).isEqualTo(UUID.fromString(noteId));
        assertThat(note.title()).isEqualTo(noteTitle);
        assertThat(note.content()).isEqualTo(noteContent);
        assertThat(note.creationTime()).isNotNull();
        assertThat(note.modificationTime()).isNotNull();
        assertThat(note.creationTime()).isEqualTo(note.modificationTime());
    }

    @Test
    void shouldRemoveNote() throws Exception {
        final var noteId = UUID.fromString("946a95dd-8cb5-4d59-ae7e-101ac3ea715b");
        final var time = Instant.parse("2011-11-11T11:11:11Z");
        createNote(new Note(noteId, "Note title", "Note content", time, time));

        final var resultActions = mockMvc.perform(delete("/api/notes/" + noteId));

        resultActions.andExpect(status().isNoContent());
        assertThat(readNotes()).isEmpty();
    }

    @Test
    void shouldGetNote() throws Exception {
        final var noteId = "81f6d5f3-9226-437f-bf5d-3e9eba985eb7";
        final var noteTitle = "Note title";
        final var noteContent = "Note content";
        final var noteCreationTime = "2010-10-10T10:10:10.000Z";
        final var noteModificationTime = "2011-11-11T11:11:11.111Z";
        createNote(new Note(
                UUID.fromString(noteId),
                noteTitle,
                noteContent,
                Instant.parse(noteCreationTime),
                Instant.parse(noteModificationTime)
        ));

        final var resultActions = mockMvc.perform(get("/api/notes/" + noteId));

        resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.title").value(noteTitle))
                .andExpect(jsonPath("$.content").value(noteContent))
                .andExpect(jsonPath("$.creationTime").value(noteCreationTime))
                .andExpect(jsonPath("$.modificationTime").value(noteModificationTime));
    }

    @Test
    void shouldGetNotes() throws Exception {
        final var noteId = "7a07f782-d2c0-4dc5-9cf2-a984b9ad9690";
        final var noteTitle = "Note title";
        final var noteContent = "Note content";
        final var noteCreationTime = "2010-10-10T10:10:10.100Z";
        final var noteModificationTime = "2011-11-11T11:11:11.111Z";
        createNote(new Note(
                UUID.fromString(noteId),
                noteTitle,
                noteContent,
                Instant.parse(noteCreationTime),
                Instant.parse(noteModificationTime)
        ));

        final var resultActions = mockMvc.perform(get("/api/notes"));

        resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(noteId))
                .andExpect(jsonPath("$.data[0].title").value(noteTitle))
                .andExpect(jsonPath("$.data[0].content").value(noteContent))
                .andExpect(jsonPath("$.data[0].creationTime").value(noteCreationTime))
                .andExpect(jsonPath("$.data[0].modificationTime").value(noteModificationTime))
                .andExpect(jsonPath("$.pagination.next").value(false))
                .andExpect(jsonPath("$.pagination.previous").value(false));
    }

    @Test
    void shouldUpdateNote() throws Exception {
        final var noteId = UUID.fromString("d4630597-d447-4b81-ab7f-839f839a6931");
        final var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        final var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        createNote(new Note(
                noteId,
                "Note title",
                "Note content",
                noteCreationTime,
                noteModificationTime
        ));
        final var noteTitle = "New note title";
        final var noteContent = "New note content";

        final var resultActions = mockMvc.perform(put("/api/notes/" + noteId)
                .contentType("application/json")
                .content("""
                        {
                          "title": "%s",
                          "content": "%s"
                        }
                        """.formatted(noteTitle, noteContent)
                )
        );

        resultActions.andExpect(status().isNoContent());
        final var notes = readNotes();
        assertThat(notes).hasSize(1);
        final var note = notes.getFirst();
        assertThat(note.id()).isEqualTo(noteId);
        assertThat(note.title()).isEqualTo(noteTitle);
        assertThat(note.content()).isEqualTo(noteContent);
        assertThat(note.creationTime()).isEqualTo(noteCreationTime);
        assertThat(note.modificationTime()).isAfter(noteModificationTime);
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

    private List<Note> readNotes() {
        return jdbcTemplate.query(
                "SELECT * FROM note",
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
}
