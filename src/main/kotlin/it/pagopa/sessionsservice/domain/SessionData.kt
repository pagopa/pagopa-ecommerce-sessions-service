package it.pagopa.sessionsservice.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash


//@RedisHash("SessionData")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class SessionData(
    @JsonProperty("rptId") var rptId: String,
    @JsonProperty("email") var email: String,
    @JsonProperty("paymentToken") var paymentToken: String,
    @JsonProperty("sessionToken") var sessionToken: String?){

    init {
        require(EmailValidator.validateEmail(email)) { "Email must be RFC 5322 compliant." }
        require(RptId.validate(rptId))
    }

    fun toResponse(): SessionResponse {
        return SessionResponse(rptId, email, paymentToken, sessionToken)
    }
}

