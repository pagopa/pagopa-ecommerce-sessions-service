package it.pagopa.sessionsservice.service

import it.pagopa.generated.session.server.model.SessionDataDto
import it.pagopa.sessionsservice.domain.RptId
import it.pagopa.sessionsservice.domain.SessionData
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
@Slf4j
class SessionsService {
    @Autowired
    private lateinit var sessionOps: ReactiveRedisOperations<String, SessionData>

    fun validateSession(): Mono<ResponseEntity<Void>> {
        return Mono.empty()
    }

    fun getToken(rptId: String?): Mono<ResponseEntity<SessionDataDto>> {
        return Mono.empty()
    }

    fun getAllTokens(): Flux<SessionDataDto> {
        return Flux.concat(sessionOps.scan().map(sessionOps.opsForValue()::get)).map {
            SessionDataDto()
                .sessionToken(it.sessionToken)
                .paymentToken(it.paymentToken)
                .email(it.email)
                .rptId(it.rptId)
        }
    }

    fun postToken(sessionDataDto: SessionDataDto): Mono<Boolean> {
        return sessionOps.opsForValue().set(sessionDataDto.rptId, SessionData(
                sessionDataDto.rptId, sessionDataDto.email, sessionDataDto.paymentToken, sessionDataDto.sessionToken
            )
        )
    }
}