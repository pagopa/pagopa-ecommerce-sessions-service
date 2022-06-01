package it.pagopa.sessionsservice.service

import it.pagopa.generated.session.server.model.SessionDataDto
import it.pagopa.sessionsservice.domain.SessionData
import it.pagopa.sessionsservice.session.JwtTokenUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Service


@Service
class SessionsService {
    @Autowired
    private lateinit var sessionOps: ReactiveRedisOperations<String, SessionData>
    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil
    var logger: Logger = LoggerFactory.getLogger(SessionsService::class.java)

    suspend fun validateSession(sessionData: SessionDataDto): Boolean {
        val data = getToken(sessionData.rptId)
        return if (data == null || data.sessionToken.isNullOrEmpty()) {
            logger.info("Session data validation failed. Session data not found in db.")
            false
        } else {
            val isValid = jwtTokenUtil.validateToken(data.sessionToken!!)
            logger.info("Session data validation - success: ${isValid}.")

            data.sessionToken == sessionData.sessionToken && isValid
        }
    }

    suspend fun getToken(rptId: String): SessionData? {
        logger.info("Searching for session data with rptId (${rptId}.")
        return sessionOps.opsForValue().get(rptId).awaitSingleOrNull()
    }

    fun getAllTokens(): Flow<SessionDataDto> {
        logger.info("Searching for all session data.")
        return sessionOps.scan().asFlow().map {
            return@map sessionOps.opsForValue().get(it).awaitSingleOrNull()
        }.mapNotNull {
            return@mapNotNull it?.let {
                SessionDataDto(
                    sessionToken = it.sessionToken!!,
                    paymentToken = it.paymentToken,
                    email = it.email,
                    rptId = it.rptId,
                )
            }
        }
    }

    suspend fun postToken(sessionDataDto: SessionDataDto): SessionData? {
        logger.info("Creating new session data for ${sessionDataDto.rptId}.")

        val sessionData = SessionData(
            sessionDataDto.rptId, sessionDataDto.email, sessionDataDto.paymentToken, jwtTokenUtil.generateToken(sessionDataDto)
        )
        val success = sessionOps.opsForValue().set(sessionDataDto.rptId, sessionData).awaitSingle()

        return if (success) {
            sessionData
        } else {
            null
        }
    }
}