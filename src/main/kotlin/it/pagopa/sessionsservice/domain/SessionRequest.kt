package it.pagopa.sessionsservice.domain

data class SessionRequest(var rptId: RptId, var email: String, var paymentToken: String?){
    init {
        require(EmailValidator.validateEmail(email)) { "Email must be RFC 5322 compliant." }
    }
}