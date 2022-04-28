package it.pagopa.sessionsservice.session

data class SessionRequest(val paymentToken: String, val email: String)