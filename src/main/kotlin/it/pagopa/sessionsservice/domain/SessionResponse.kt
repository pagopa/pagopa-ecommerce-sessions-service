package it.pagopa.sessionsservice.domain


data class SessionResponse(var rptId: String, var email: String, var paymentToken: String, var sessionToken: String?){
    init {
        require(EmailValidator.validateEmail(email)) { "Email must be RFC 5322 compliant." }
    }
}