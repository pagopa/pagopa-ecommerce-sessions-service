package it.pagopa.sessionsservice.controllers

import it.pagopa.generated.session.server.model.SessionDataDto
import it.pagopa.generated.session.server.model.SessionRequestDto
import it.pagopa.sessionsservice.domain.SessionData
import it.pagopa.sessionsservice.service.SessionsService
import it.pagopa.sessionsservice.session.JwtTokenUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest
@Import(JwtTokenUtil::class)
@TestPropertySource(locations = ["classpath:application.test.properties"])
@OptIn(ExperimentalCoroutinesApi::class)
class SessionsControllerTests(
    @Autowired val webClient: WebTestClient,
    @Autowired val jwtTokenUtil: JwtTokenUtil
) {

    @MockBean
    lateinit var sessionsService: SessionsService

    @Test
    fun `getToken succeeds with valid input`() = runTest {
        val rptId = "77777777777302016723749670035"
        val sessionData = SessionDataDto(
            paymentToken = "paymentToken",
            sessionToken = "sessionToken",
            rptId = rptId,
            email = "foo@example.com"
        )

        /* preconditions */
        given(sessionsService.getToken(rptId)).willReturn(
            SessionData(
                sessionData.rptId,
                sessionData.email,
                sessionData.paymentToken,
                jwtTokenUtil.generateToken(sessionData)
            )
        )

        /* test */
        webClient.get()
            .uri { it.path("/session/{rptId}").build(rptId) }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `getToken returns 404 on non-existing session data`() = runTest {
        val rptId = "77777777777302016723749670035"

        /* preconditions */
        given(sessionsService.getToken(rptId)).willReturn(null)

        /* test */
        webClient.get()
            .uri { it.path("/session/{rptId}").build(rptId) }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(404)
    }

    @Test
    fun `validateSession succeeds with valid input`() = runTest {
        val sessionData = SessionDataDto(
            paymentToken = "token",
            sessionToken = "",
            rptId = "77777777777302016723749670035",
            email = "jhondoe@mail.it"
        )

        /* preconditions */
        given(sessionsService.validateSession(sessionData)).willReturn(true)

        /* test */
        webClient.post()
            .uri("/session/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(sessionData)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `validateSession returns error on invalid session`() = runTest {
        val sessionData = SessionDataDto(
            paymentToken = "token",
            sessionToken = "",
            rptId = "77777777777302016723749670035",
            email = "jhondoe@mail.it"
        )

        /* preconditions */
        given(sessionsService.validateSession(sessionData)).willReturn(false)

        /* test */
        webClient.post()
            .uri("/session/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(sessionData)
            .exchange()
            .expectStatus().isEqualTo(400)
    }

    @Test
    fun `validateSession returns error on invalid input`() = runTest {
        /* test */
        webClient.post()
            .uri("/session/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus().isEqualTo(400)
    }

    @Test
    fun `getAllTokens returns stored tokens`() {
        val tokens = setOf(
            SessionDataDto(
                paymentToken = "token",
                sessionToken = "",
                rptId = "77777777777302016723749670035",
                email = "johndoe@mail.it"
            ),
            SessionDataDto(
                paymentToken = "token2",
                sessionToken = "",
                rptId = "77777777777302016723749670036",
                email = "marysue@mail.it"
            )
        )

        /* preconditions */
        given(sessionsService.getAllTokens()).willReturn(tokens.asFlow())

        /* test */
        webClient.get()
            .uri("/sessions")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(SessionDataDto::class.java)
            .value<WebTestClient.ListBodySpec<SessionDataDto>> {
                assertEquals(it.toSet(), tokens)
            }
    }

    @Test
    fun `postToken succeeds with valid input`() = runTest {
        val sessionRequest = SessionRequestDto("77777777777302016723749670035", "jhondoe@mail.it", "token")
        val sessionData = SessionDataDto(
            paymentToken = sessionRequest.paymentToken,
            sessionToken = "",
            rptId = sessionRequest.rptId,
            email = sessionRequest.email
        )

        /* preconditions */
        given(sessionsService.postToken(sessionData)).willReturn(
            SessionData(
                sessionData.rptId,
                sessionData.email,
                sessionData.paymentToken,
                jwtTokenUtil.generateToken(sessionData)
            )
        )

        /* test */
        webClient.post()
            .uri("/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(sessionRequest)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `postToken rejects invalid input`() = runTest {
        webClient.post()
            .uri("/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus().isEqualTo(400)
    }

    @Test
    fun `postToken returns error when session data cannot be retrieved`() = runTest {
        val sessionRequest = SessionRequestDto("77777777777302016723749670035", "jhondoe@mail.it", "token")
        val sessionData = SessionDataDto(
            paymentToken = sessionRequest.paymentToken,
            sessionToken = "",
            rptId = sessionRequest.rptId,
            email = sessionRequest.email
        )

        /* preconditions */
        given(sessionsService.postToken(sessionData)).willReturn(null)

        /* test */
        webClient.post()
            .uri("/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(sessionRequest)
            .exchange()
            .expectStatus().isEqualTo(400)
    }
}