package it.pagopa.sessionsservice.service

import it.pagopa.generated.session.server.model.SessionDataDto
import it.pagopa.sessionsservice.domain.SessionData
import it.pagopa.sessionsservice.session.JwtTokenUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import org.springframework.context.annotation.Import

@ExtendWith(SpringExtension::class)
@TestPropertySource(locations = ["classpath:application.test.properties"])
@OptIn(ExperimentalCoroutinesApi::class)
class SessionsServiceTest {
    @Mock
    lateinit var sessionOps: ReactiveRedisOperations<String, SessionData>

    @Mock
    lateinit var opsForValue: ReactiveValueOperations<String, SessionData>

    @InjectMocks
    lateinit var service: SessionsService

    @Mock
    lateinit var jwtTokenUtil: JwtTokenUtil

    @Test
    fun `returns null on non-existing rptId query`() = runTest {
        val rptId = "77777777777302016723749670035"

        /* preconditions */
        given(sessionOps.opsForValue()).willReturn(opsForValue)
        given(opsForValue.get(rptId)).willReturn(Mono.empty())

        /* test */
        assertNull(service.getToken(rptId))
    }

    @Test
    fun `gets existing session data given rptId`() = runTest {
        val rptId = "77777777777302016723749670035"
        val sessionData = SessionData(
            sessionToken = "sessionToken",
            paymentToken = "paymentToken",
            email = "john@example.com",
            rptId = rptId,
        )

        /* preconditions */
        given(sessionOps.opsForValue()).willReturn(opsForValue)
        given(opsForValue.get(rptId)).willReturn(Mono.just(sessionData))

        /* test */
        assertEquals(service.getToken(rptId), sessionData)
    }

    @Test
    fun `gets existing session data`() = runTest {
        val rptIds = listOf("77777777777302016723749670035", "77777777777302016723749670035")
        val sessionsData = rptIds.associateWith { rptId ->
            SessionData(
                sessionToken = "sessionToken",
                paymentToken = "paymentToken",
                email = "john@example.com",
                rptId = rptId,
            )
        }

        val expected = sessionsData.values.map {
            SessionDataDto(
                sessionToken = it.sessionToken!!,
                paymentToken = it.paymentToken,
                email = it.email,
                rptId = it.rptId,
            )
        }

        /* preconditions */
        given(sessionOps.scan()).willReturn(Flux.fromIterable(rptIds.asIterable()))
        given(sessionOps.opsForValue()).willReturn(opsForValue)

        for (rptId in rptIds) {
            given(opsForValue.get(rptId)).willReturn(Mono.just(sessionsData[rptId]!!))
        }

        /* test */
        // We're not interested in order, therefore we convert to Set before checking for equality
        assertEquals(service.getAllTokens().toSet(), expected.toSet())
    }

    @Test
    fun `returns empty Flow when there is no session data`() = runTest {
        val expected = emptySet<SessionData>()

        /* preconditions */
        given(sessionOps.scan()).willReturn(Flux.empty())
        given(sessionOps.opsForValue()).willReturn(opsForValue)
        given(opsForValue.get(any())).willReturn(Mono.empty())

        /* test */
        assertEquals(service.getAllTokens().toSet(), expected)
    }

    @Test
    fun `return true for valid session token`() = runTest {
        val rptId = "77777777777302016723749670035"
        val sessionData =
                SessionData(
                        sessionToken = "sessionToken",
                        paymentToken = "paymentToken",
                        email = "john@example.com",
                        rptId = rptId,
                )

        val sessionDataDto =
                SessionDataDto(
                        sessionToken = "sessionToken",
                        paymentToken = "paymentToken",
                        email = "john@example.com",
                        rptId = rptId
                )
        /* preconditions */
        given(sessionOps.opsForValue()).willReturn(opsForValue)
        given(opsForValue.get(rptId)).willReturn(Mono.just(sessionData))
        given(jwtTokenUtil.validateToken(sessionDataDto.sessionToken)).willReturn(true)

        /* test */
        assertEquals(service.validateSession(sessionDataDto), true)
    }

    @Test
    fun `return false for empty session token`() = runTest {
        val rptId = "77777777777302016723749670035"
        val sessionData =
                SessionData(
                        sessionToken = "sessionToken",
                        paymentToken = "paymentToken",
                        email = "john@example.com",
                        rptId = rptId,
                )

        val sessionDataDto =
                SessionDataDto(
                        sessionToken = "",
                        paymentToken = "paymentToken",
                        email = "john@example.com",
                        rptId = rptId
                )
        /* preconditions */
        given(sessionOps.opsForValue()).willReturn(opsForValue)
        given(opsForValue.get(rptId)).willReturn(Mono.just(sessionData))

        /* test */
        assertEquals(service.validateSession(sessionDataDto), false)
    }

    @Test
    fun `return false for invalid session token`() = runTest {
        val rptId = "77777777777302016723749670035"
        val sessionData =
                SessionData(
                        sessionToken = "sessionToken",
                        paymentToken = "paymentToken",
                        email = "john@example.com",
                        rptId = rptId,
                )

        val sessionDataDto =
                SessionDataDto(
                        sessionToken = "InvalidSessionToken",
                        paymentToken = "paymentToken",
                        email = "john@example.com",
                        rptId = rptId
                )
        /* preconditions */
        given(sessionOps.opsForValue()).willReturn(opsForValue)
        given(opsForValue.get(rptId)).willReturn(Mono.just(sessionData))
        given(jwtTokenUtil.validateToken(sessionDataDto.sessionToken)).willReturn(false)

        /* test */
        assertEquals(service.validateSession(sessionDataDto), false)
    }
}
