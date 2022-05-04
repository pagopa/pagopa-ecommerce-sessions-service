package it.pagopa.sessionsservice.session

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import it.pagopa.sessionsservice.domain.SessionRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SecureRandom
import javax.crypto.SecretKey


@Component
class JwtTokenUtil{
    // Base64-encoded secret key
    @Value("\${jwt.secret}")
    private val jwtSecret: String? = null

    val logger = LoggerFactory.getLogger(javaClass)

    private fun getKey(): SecretKey {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
    }

    // Generate a signed JWT token (HMAC-SHA-256)
    fun generateToken(sessionRequest: SessionRequest): String? {
        try {
            return Jwts
                .builder()
                .claim("rptId", sessionRequest.rptId)
                .claim("email", sessionRequest.email)
                .claim("paymentToken", sessionRequest.paymentToken)
                .claim("jti", generateNonce())
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact()
        } catch (jExc: JwtException){
            logger.error("Error during JWT token generation\n${jExc.message}")
            return null
        }
    }

    // Generate token
    fun validateToken(token: String): Boolean {
        try {
            val tokenBody = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).body
            return true
        } catch (signExc: SignatureException){
            logger.info("Error during JWS signature validation\n${signExc.message}")
            return false
        } catch (exc: JwtException){
            logger.error("Unexpected error during token validation\n${exc.message}")
            return false
        }
    }

    fun generateNonce(): String? {
        val secureRandom = SecureRandom()
        val stringBuilder = StringBuilder()
        for (i in 0..14) {
            stringBuilder.append(secureRandom.nextInt(10))
        }
        return stringBuilder.toString()
    }
}