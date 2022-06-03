package it.pagopa.sessionsservice.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class RptIdTest {
    @Test
    fun `empty RptId is invalid`() {
        assertThrows<IllegalArgumentException> {
            RptId("")
        }
    }

    @Test
    fun `format 1 RptId is valid`() {
        assertDoesNotThrow {
            RptId("a")
            RptId("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        }
    }

    @Test
    fun `invalid RptId is rejected`() {
        assertThrows<IllegalArgumentException> {
            RptId("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa0")
        }
    }

    @Test
    fun `format 2 RptId is valid`() {
        assertDoesNotThrow {
            RptId("RF001")
            RptId("RF001111111111111111111Aa")
        }
    }
}