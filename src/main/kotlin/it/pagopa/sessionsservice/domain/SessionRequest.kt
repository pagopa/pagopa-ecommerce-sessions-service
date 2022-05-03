package it.pagopa.sessionsservice.domain

data class SessionRequest(var rptId: RptId, var email: String, var paymentToken: String?)