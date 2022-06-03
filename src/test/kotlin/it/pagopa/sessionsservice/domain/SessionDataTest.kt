package it.pagopa.sessionsservice.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class SessionDataTest {
    @Test
    fun `can construct SessionData from valid email and RptId`(){
        val rptId = "a"
        val email = "foo@example.com"

        assertDoesNotThrow {
            SessionData(
                rptId = rptId,
                email = email,
                paymentToken = "",
                sessionToken = null
            )
        }
    }

    @Test
    fun `constructor throws on invalid email`() {
        val rptId = "a"
        val invalidEmail = "@example.com"

        assertThrows<IllegalArgumentException> {
            SessionData(
                rptId = rptId,
                email = invalidEmail,
                paymentToken = "",
                sessionToken = null
            )
        }
    }

    @Test
    fun `constructor throws on invalid rptId`() {
        val rptId = ""
        val invalidEmail = "@example.com"

        assertThrows<IllegalArgumentException> {
            SessionData(
                rptId = rptId,
                email = invalidEmail,
                paymentToken = "",
                sessionToken = null
            )
        }
    }
}