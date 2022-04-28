package it.pagopa.sessionsservice.domain

import org.springframework.data.redis.core.RedisHash

@RedisHash("SessionData")
data class SessionData(val rptId: String, val email: String)