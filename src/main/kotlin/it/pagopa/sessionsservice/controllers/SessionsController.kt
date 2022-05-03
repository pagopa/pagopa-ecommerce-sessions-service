package it.pagopa.sessionsservice.controllers

import it.pagopa.sessionsservice.domain.RptId
import it.pagopa.sessionsservice.domain.SessionData
import it.pagopa.sessionsservice.domain.SessionRequest
import it.pagopa.sessionsservice.repository.SessionRepository
import it.pagopa.sessionsservice.session.JwtTokenUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
class SessionController(){
  @Autowired val jwtTokenUtil: JwtTokenUtil? = null
  @Autowired val sessionRepository: SessionRepository? = null

  val logger = LoggerFactory.getLogger(javaClass)

  @PostMapping("/session")
  fun postSession(@RequestBody sessionRequest: SessionRequest): ResponseEntity<SessionData>{
    var sessionData: SessionData? =
      sessionRequest.paymentToken?.let {
        SessionData(sessionRequest.rptId, sessionRequest.email,
          it, jwtTokenUtil?.generateToken(sessionRequest))
      }
    return if (sessionData != null){
      sessionRepository?.save(sessionData)
      ResponseEntity.ok(sessionData)
    } else {
      ResponseEntity.badRequest().body(null)
    }
  }

  @GetMapping("/session")
  fun getSessions(@RequestParam(required = false) rptId: String?): ResponseEntity<Any> {
    return ResponseEntity.ok(sessionRepository?.findAll()?.toList())
  }

  @GetMapping("/session/{rptId}")
  fun getSession(@PathVariable rptId: String): ResponseEntity<Any>{
    return try {
      val rptIdObj = RptId(rptId)

      val sessionData = sessionRepository?.findById(rptIdObj)?.get()

      return ResponseEntity.ok(sessionData)
    } catch (nxElement: NoSuchElementException){
      throw ResponseStatusException(
        HttpStatus.NOT_FOUND, "Cannot found session data bound to this rptId"
      )
    }
  }

  @PostMapping("/session/validate")
  fun validateSession(@RequestBody sessionData: SessionData): ResponseEntity<String> {
    val storedSessionData: SessionData = sessionRepository?.findById(sessionData.rptId)?.get()
      ?: return ResponseEntity.badRequest().body("")

    return if (storedSessionData.token == sessionData.token && sessionData.token?.let { jwtTokenUtil?.validateToken(it) } == true){
      ResponseEntity.ok("")
    } else {
      ResponseEntity.badRequest().body("")
    }
  }
}