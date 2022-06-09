package it.pagopa.sessionsservice.session

import it.pagopa.generated.session.server.model.SessionDataDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@TestPropertySource(locations = ["classpath:application.test.properties"])
@Import(JwtTokenUtil::class)
data class JwtTokenUtilTests(@Autowired val jwtTokenUtil: JwtTokenUtil) {

    companion object {
        val sessionData = SessionDataDto(
            rptId = "rptId",
            email = "email@example.com",
            paymentToken = "paymentToken",
            sessionToken = "sessionToken"
        )
    }

    @Test
    fun `generateToken does generate a token`() {
        assertNotNull(jwtTokenUtil.generateToken(sessionData))
    }

    @Test
    fun `generateToken does generate a valid token`() {
        val token = jwtTokenUtil.generateToken(sessionData)!!

        assertTrue(jwtTokenUtil.validateToken(token))
    }

    @Test
    fun `generateNonce does generate a nonce of length 14`() {
        assertEquals(15, jwtTokenUtil.generateNonce().length)
    }
}