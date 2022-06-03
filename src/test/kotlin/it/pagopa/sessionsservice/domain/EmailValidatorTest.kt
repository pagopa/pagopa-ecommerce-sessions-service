package it.pagopa.sessionsservice.domain

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmailValidatorTest {
    @Test
    fun `accepts valid email`() {
        val valid = "name+suffix@domain.tld"

        assertTrue(EmailValidator.validateEmail(valid))
    }

    @Test
    fun `rejects invalid email`() {
        val invalid = "@aaa"

        assertFalse(EmailValidator.validateEmail(invalid))
    }
}