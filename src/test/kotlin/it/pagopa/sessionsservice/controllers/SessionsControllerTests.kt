package it.pagopa.sessionsservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import it.pagopa.sessionsservice.domain.SessionData
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest
class SessionsControllerTests {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var mapper: ObjectMapper

    @Test
    fun postSessionTest() {
        val sessionData = SessionData("id","token", "jhondoe@mail.it")

        mockMvc.post("/session") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(sessionData)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
           // content { contentType(MediaType.APPLICATION_JSON) }
        }
    }
}