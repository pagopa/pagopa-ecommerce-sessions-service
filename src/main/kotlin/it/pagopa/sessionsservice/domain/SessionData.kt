package it.pagopa.sessionsservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("SessionData")
data class SessionData(@Id var rptId: RptId, var email: String, var paymentToken: String, var sessionToken: String?){
    init {
        require(EmailValidator.validateEmail(email)) { "Email must be RFC 5322 compliant." }
    }
}

