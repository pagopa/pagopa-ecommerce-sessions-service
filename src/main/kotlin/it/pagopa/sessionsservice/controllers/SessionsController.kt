package it.pagopa.sessionsservice.controllers

import it.pagopa.sessionsservice.domain.SessionData
import it.pagopa.sessionsservice.repository.SessionRepository
import it.pagopa.sessionsservice.session.JwtTokenUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class SessionController(){
  @Autowired val jwtTokenUtil: JwtTokenUtil? = null
  @Autowired val sessionRepository: SessionRepository? = null

  val logger = LoggerFactory.getLogger(javaClass)

  @PostMapping("/session")
  fun postSession(@RequestBody sessionData: SessionData): ResponseEntity<SessionData>{
    sessionData.token = jwtTokenUtil?.generateToken(sessionData)
    sessionRepository?.save(sessionData)

    return ResponseEntity.ok(sessionData)
  }

  @GetMapping("/session")
  fun getSessions(): ResponseEntity<List<SessionData>>{
    return ResponseEntity.ok(sessionRepository?.findAll()?.toList())
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