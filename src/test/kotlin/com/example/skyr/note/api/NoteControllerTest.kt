package com.example.skyr.note.api

import com.example.skyr.IntegrationTest
import com.example.skyr.note.Note
import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@AutoConfigureMockMvc
class NoteControllerTest : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @AfterEach
    fun afterEachTest() {
        clean()
    }

    @Test
    fun shouldCreateNote() {
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
        val notes = notes()
        assertThat(notes).hasSize(1)
        val note = notes.first()
        assertThat(note.id).isEqualTo(UUID.fromString(noteId))
        assertThat(note.title).isEqualTo(noteTitle)
        assertThat(note.content).isEqualTo(noteContent)
    }

    private fun notes(): List<Note> {
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

    private fun clean() {
        jdbcTemplate.update("DELETE FROM note")
    }
}
