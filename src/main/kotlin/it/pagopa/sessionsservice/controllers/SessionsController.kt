package it.pagopa.sessionsservice.controllers

import it.pagopa.generated.session.server.api.SessionApi
import it.pagopa.generated.session.server.api.SessionsApi
import it.pagopa.generated.session.server.model.SessionDataDto
import it.pagopa.generated.session.server.model.SessionRequestDto
import it.pagopa.sessionsservice.service.SessionsService
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class SessionsController: SessionApi, SessionsApi {
    @Autowired lateinit var sessionsService: SessionsService

    override suspend fun getToken(rptId: String): ResponseEntity<SessionDataDto> {
        if (rptId.isEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val sessionData = sessionsService.getToken(rptId)

        return if (sessionData != null) {
            ResponseEntity.ok(
                SessionDataDto(
                    rptId = sessionData.rptId,
                    paymentToken = sessionData.paymentToken,
                    sessionToken = sessionData.sessionToken!!,
                    email = sessionData.email
                )
            )
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    override suspend fun validateSession(sessionDataDto: SessionDataDto): ResponseEntity<Unit> {
        val isValid = sessionsService.validateSession(sessionDataDto)

        return if (!isValid) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        } else {
            ResponseEntity.ok().build()
        }
    }

    override fun getAllTokens(): ResponseEntity<Flow<SessionDataDto>> {
       return ResponseEntity.ok(sessionsService.getAllTokens())
    }

    override suspend fun postToken(sessionRequestDto: SessionRequestDto): ResponseEntity<SessionDataDto> {
        val sessionDataDtoRequest = SessionDataDto(
            paymentToken = sessionRequestDto.paymentToken,
            sessionToken = "",
            rptId = sessionRequestDto.rptId,
            email = sessionRequestDto.email
        )
        val sessionData = sessionsService.postToken(sessionDataDtoRequest)

        return if (sessionData != null) {
            ResponseEntity.ok(
                SessionDataDto(
                    paymentToken = sessionData.paymentToken,
                    sessionToken = sessionData.sessionToken!!,
                    rptId = sessionData.rptId,
                    email = sessionData.email
                )
            )
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
    }
}
