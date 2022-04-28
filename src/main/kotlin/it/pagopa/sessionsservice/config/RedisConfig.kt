package it.pagopa.sessionsservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Configuration
class RedisConfig {
    @Value("\${redis.port}") val redisPort: String? = null
    @Value("\${redis.hostname}") val redisHostname: String? = null
    @Value("\${redis.password}") val redisPassword: String? = null

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory? {
        val redisStandaloneConfiguration = redisHostname?.let { RedisStandaloneConfiguration(it, Integer.parseInt(redisPort)) }
        redisStandaloneConfiguration?.password = RedisPassword.of(redisPassword)
        return redisStandaloneConfiguration?.let { JedisConnectionFactory(it) }
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String?, Any?>? {
        val template: RedisTemplate<String?, Any?> = RedisTemplate()
        jedisConnectionFactory()?.let { template.setConnectionFactory(it) }
        return template
    }
}