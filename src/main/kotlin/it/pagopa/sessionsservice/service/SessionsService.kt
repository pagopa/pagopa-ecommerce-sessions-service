package it.pagopa.sessionsservice.service

import it.pagopa.generated.session.server.model.SessionDataDto
import it.pagopa.sessionsservice.repository.SessionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SessionsService {
    @Autowired protected lateinit var sessionRepository: SessionRepository

    fun validateSession(): Mono<ResponseEntity<Void>> {
        return Mono.empty()
    }

    fun getToken(rptId: String?): Mono<ResponseEntity<SessionDataDto>>{
        return Mono.empty()
    }

    fun getAllTokens(): Flux<SessionDataDto> {
        return sessionRepository
            .findAll().let {
                Flux.fromIterable(
                    it
                        .map { sessionData -> SessionDataDto()
                            .rptId(sessionData.rptId.rptId)
                            .email(sessionData.email)
                            .sessionToken(sessionData.sessionToken)
                            .paymentToken(sessionData.paymentToken)}
                )}
    }

    fun postToken(){}
}