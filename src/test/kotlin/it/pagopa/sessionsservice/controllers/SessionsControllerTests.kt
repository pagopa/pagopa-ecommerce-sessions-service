package it.pagopa.sessionsservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import it.pagopa.sessionsservice.domain.RptId
import it.pagopa.sessionsservice.domain.SessionData
import it.pagopa.sessionsservice.domain.SessionRequest
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
        val sessionRequest = SessionRequest(RptId("77777777777302016723749670035"), "jhondoe@mail.it", "token")


        mockMvc.post("/session") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(sessionRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
           // content { contentType(MediaType.APPLICATION_JSON) }
        }
    }
}