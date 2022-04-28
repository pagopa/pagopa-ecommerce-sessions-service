package it.pagopa.sessionsservice.repository

import it.pagopa.sessionsservice.domain.SessionData

interface TokenRepository {
    fun getTokenFromSessionData(sessionData: SessionData)
    fun addSession(token: String, sessionData: SessionData)
    
}