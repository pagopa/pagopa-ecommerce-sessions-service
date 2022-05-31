package it.pagopa.sessionsservice.controllers

import it.pagopa.generated.session.server.api.SessionApi
import it.pagopa.generated.session.server.api.SessionsApi
import it.pagopa.generated.session.server.model.SessionDataDto
import it.pagopa.generated.session.server.model.SessionRequestDto
import it.pagopa.sessionsservice.service.SessionsService
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class SessionsController: SessionApi, SessionsApi {
    @Autowired lateinit var sessionsService: SessionsService

    override fun getToken(rptId: String?, exchange: ServerWebExchange?): Mono<ResponseEntity<SessionDataDto>> {
        if (rptId.isNullOrEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return mono { return@mono sessionsService.getToken(rptId) }.map { sessionData ->
            return@map if(sessionData != null) {
                ResponseEntity.ok(
                    SessionDataDto()
                        .rptId(sessionData.rptId)
                        .paymentToken(sessionData.paymentToken)
                        .sessionToken(sessionData.sessionToken)
                        .email(sessionData.email)
                )
            } else {
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
            }}
    }

    override fun validateSession(
        sessionDataDto: Mono<SessionDataDto>?,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
       return sessionDataDto?.flatMap { sessionData ->
           return@flatMap mono { sessionsService.validateSession(sessionData) }
               .map{ isValid ->
                   if(!isValid){
                       throw ResponseStatusException(HttpStatus.BAD_REQUEST)
                   } else {
                       return@map ResponseEntity.ok().build()
                   }
               }
       }
           ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
    }

    override fun getAllTokens(exchange: ServerWebExchange?): Mono<ResponseEntity<Flux<SessionDataDto>>> {
       return Mono.just(ResponseEntity.ok(sessionsService.getAllTokens().asFlux()))
    }

    override fun postToken(
        sessionRequestDto: Mono<SessionRequestDto>?,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<SessionDataDto>> {

        return sessionRequestDto!!.flatMap {
            mono {
                sessionsService.postToken(SessionDataDto()
                    .paymentToken(it.paymentToken)
                    .sessionToken("")
                    .rptId(it.rptId)
                    .email(it.email)
                )
            }
        }.map {
            if (it != null) {
                return@map ResponseEntity.ok(
                    SessionDataDto()
                        .paymentToken(it.paymentToken)
                        .sessionToken(it.sessionToken)
                        .rptId(it.rptId)
                        .email(it.email)
                )
            } else {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST)
            }
        }
    }
}