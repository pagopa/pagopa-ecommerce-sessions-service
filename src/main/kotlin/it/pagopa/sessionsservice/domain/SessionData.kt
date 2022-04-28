package it.pagopa.sessionsservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("SessionData")
data class SessionData(@Id val id: String, val rptId: String, val email: String)