package it.pagopa.sessionsservice.controllers

import it.pagopa.sessionsservice.domain.SessionData
import it.pagopa.sessionsservice.session.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SessionController() {
  @Autowired
  val jwtTokenUtil: JwtTokenUtil? = null

  @PostMapping("/session")
  fun postSession(@RequestBody sessionData: SessionData): ResponseEntity<String> {
    val token = jwtTokenUtil?.generateToken(sessionData)

    return ResponseEntity.ok(token)
  }
}