package it.pagopa.sessionsservice.domain

data class SessionRequest(val paymentToken: String, val email: String)