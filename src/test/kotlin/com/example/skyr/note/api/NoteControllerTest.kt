package com.example.skyr.note.api

import com.example.skyr.IntegrationTest
import com.example.skyr.note.Note
import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@AutoConfigureMockMvc
class NoteControllerTest : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @AfterEach
    fun afterEachTest() {
        delete()
    }

    @Test
    fun shouldPostNote() {
        val noteTitle = "Note title"
        val noteContent = "Note content"

        val resultActions = mockMvc.perform(
            post("/api/notes").contentType("application/json")
                .content(
                    """
                    {
                      "title": "$noteTitle",
                      "content": "$noteContent"
                    }
                    """.trimIndent()
                )
        )

        val responseContent = resultActions.andExpect(status().isCreated)
            .andExpect(header().string("Content-Type", "application/json"))
            .andReturn()
            .response
            .contentAsString
        val noteId: String = JsonPath.read(responseContent, "\$.id")
        val notes = read()
        assertThat(notes).hasSize(1)
        val note = notes.first()
        assertThat(note.id).isEqualTo(UUID.fromString(noteId))
        assertThat(note.title).isEqualTo(noteTitle)
        assertThat(note.content).isEqualTo(noteContent)
    }

    @Test
    fun shouldDeleteNote() {
        val noteId = UUID.fromString("946a95dd-8cb5-4d59-ae7e-101ac3ea715b")
        create(Note(noteId, "Note title", "Note content"))

        val resultActions = mockMvc.perform(delete("/api/notes/$noteId"))

        resultActions.andExpect(status().isNoContent)
        assertThat(read()).isEmpty()
    }

    @Test
    fun shouldGetNote() {
        val noteId = UUID.fromString("81f6d5f3-9226-437f-bf5d-3e9eba985eb7")
        val noteTitle = "Note title"
        val noteContent = "noteContent"
        create(Note(noteId, noteTitle, noteContent))

        val resultActions = mockMvc.perform(get("/api/notes/$noteId"))

        resultActions.andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.title").value(noteTitle))
            .andExpect(jsonPath("\$.content").value(noteContent))
    }

    @Test
    fun shouldGetNotes() {
        val noteId = UUID.fromString("7a07f782-d2c0-4dc5-9cf2-a984b9ad9690")
        val noteTitle = "Note title"
        val noteContent = "Note content"
        create(Note(noteId, noteTitle, noteContent))

        val resultActions = mockMvc.perform(get("/api/notes"))

        resultActions.andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.data", hasSize<Any>(1)))
            .andExpect(jsonPath("\$.data[0].id").value(noteId.toString()))
            .andExpect(jsonPath("\$.data[0].title").value(noteTitle))
            .andExpect(jsonPath("\$.data[0].content").value(noteContent))
            .andExpect(jsonPath("\$.pagination.next").value(false))
            .andExpect(jsonPath("\$.pagination.previous").value(false))
    }

    @Test
    fun shouldPutNote() {
        val noteId = UUID.fromString("d4630597-d447-4b81-ab7f-839f839a6931")
        create(Note(noteId, "Note title", "Note content"))
        val noteTitle = "New note title"
        val noteContent = "New note content"

        val resultActions = mockMvc.perform(
            put("/api/notes/$noteId").contentType("application/json")
                .content(
                    """
                    {
                      "title": "$noteTitle",
                      "content": "$noteContent"
                    }
                    """.trimIndent()
                )
        )

        resultActions.andExpect(status().isNoContent)
        val notes = read()
        assertThat(notes).hasSize(1)
        val note = notes.first()
        assertThat(note.id).isEqualTo(noteId)
        assertThat(note.title).isEqualTo(noteTitle)
        assertThat(note.content).isEqualTo(noteContent)
    }

    private fun create(note: Note) {
        jdbcTemplate.update(
            "INSERT INTO note VALUES (UUID_TO_BIN(?), ?, ?)",
            note.id.toString(),
            note.title,
            note.content
        )
    }

    private fun delete() {
        jdbcTemplate.update("DELETE FROM note")
    }

    private fun read(): List<Note> {
        return jdbcTemplate.query(
            "SELECT BIN_TO_UUID(id) AS id, title, content FROM note"
        ) { resultSet, _ ->
            Note(
                UUID.fromString(resultSet.getString("id")),
                resultSet.getString("title"),
                resultSet.getString("content")
            )
        }
    }
}
