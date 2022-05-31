package it.pagopa.sessionsservice.service

import it.pagopa.generated.session.server.model.SessionDataDto
import it.pagopa.sessionsservice.domain.SessionData
import it.pagopa.sessionsservice.session.JwtTokenUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class SessionsService {
    @Autowired
    private lateinit var sessionOps: ReactiveRedisOperations<String, SessionData>
    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil
    var logger: Logger = LoggerFactory.getLogger(SessionsService::class.java)

    fun validateSession(sessionData: SessionDataDto): Mono<Boolean> {
        return getToken(sessionData.rptId).map { data ->
            if (data == null || data.sessionToken.isNullOrEmpty()){
                logger.info("Session data validation failed. Session data not found in db.")
                return@map false
            } else {
                val isValid = jwtTokenUtil.validateToken(data.sessionToken!!)
                logger.info("Session data validation - success: ${isValid}.")

                return@map data.sessionToken == sessionData.sessionToken && isValid
            }
        }
    }

    fun getToken(rptId: String): Mono<SessionData> {
        logger.info("Searching for session data with rptId (${rptId}.")
        return sessionOps.opsForValue().get(rptId)
    }

    fun getAllTokens(): Flux<SessionDataDto> {
        logger.info("Searching for all session data.")
        return Flux.concat(sessionOps.scan().map(sessionOps.opsForValue()::get)).map {
            SessionDataDto()
                .sessionToken(it.sessionToken)
                .paymentToken(it.paymentToken)
                .email(it.email)
                .rptId(it.rptId)
        }
    }

    fun postToken(sessionDataDto: SessionDataDto): Mono<SessionData> {
        logger.info("Creating new session data for ${sessionDataDto.rptId}.")

        val sessionData = SessionData(
            sessionDataDto.rptId, sessionDataDto.email, sessionDataDto.paymentToken, jwtTokenUtil.generateToken(sessionDataDto)
        )
        return sessionOps.opsForValue().set(sessionDataDto.rptId, sessionData)
            .mapNotNull {
                if(it){
                    return@mapNotNull sessionData
                } else {
                    return@mapNotNull null
                }
            }
    }
}