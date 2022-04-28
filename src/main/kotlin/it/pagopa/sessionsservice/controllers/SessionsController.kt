package it.pagopa.sessionsservice.controllers

import it.pagopa.sessionsservice.session.SessionRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SessionController{

  @PostMapping("/session")
  fun postSession(@RequestBody sessionRequest: SessionRequest): Any{
    return sessionRequest;
  }

}