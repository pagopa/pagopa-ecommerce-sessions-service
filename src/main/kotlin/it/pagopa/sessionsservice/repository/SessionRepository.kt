package it.pagopa.sessionsservice.repository

import it.pagopa.sessionsservice.domain.SessionData
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SessionRepository: CrudRepository<SessionData, String>