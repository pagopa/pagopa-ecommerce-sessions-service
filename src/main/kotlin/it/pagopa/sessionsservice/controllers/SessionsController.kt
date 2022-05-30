package it.pagopa.sessionsservice.controllers

import it.pagopa.generated.session.server.api.SessionApi
import it.pagopa.generated.session.server.api.SessionsApi
import it.pagopa.generated.session.server.model.SessionDataDto
import it.pagopa.generated.session.server.model.SessionRequestDto
import it.pagopa.sessionsservice.service.SessionsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class SessionsController: SessionApi, SessionsApi {
    @Autowired lateinit var sessionsService: SessionsService

    override fun getToken(rptId: String?, exchange: ServerWebExchange?): Mono<ResponseEntity<SessionDataDto>> {
        // TODO("Not yet implemented")
        return Mono.empty()
    }

    override fun validateSession(
        sessionDataDto: Mono<SessionDataDto>?,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        // TODO("Not yet implemented")
        return Mono.empty()
    }

    override fun getAllTokens(exchange: ServerWebExchange?): Mono<ResponseEntity<Flux<SessionDataDto>>> {
       return Mono.just(ResponseEntity.ok(sessionsService.getAllTokens()))
    }

    override fun postToken(
        sessionRequestDto: Mono<SessionRequestDto>?,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<SessionDataDto>> {

        return sessionRequestDto!!.flatMap{ sessionsService.postToken(SessionDataDto()
            .paymentToken(it.paymentToken)
            .sessionToken("")
            .rptId(it.rptId)
            .email(it.email)) }
            .map { ResponseEntity.ok(SessionDataDto()) }

    }
}