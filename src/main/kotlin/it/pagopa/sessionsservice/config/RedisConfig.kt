package it.pagopa.sessionsservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate


class RedisConfig(
    @Value("redis.port") val redisPort: Int,
    @Value("redis.hostanme") val redisHostname: String,
    @Value("redis.password") val redisPassword: String
) {

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory? {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(redisHostname, redisPort)
        redisStandaloneConfiguration.password = RedisPassword.of(redisPassword)
        return JedisConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String?, Any?>? {
        val template: RedisTemplate<String?, Any?> = RedisTemplate()
        jedisConnectionFactory()?.let { template.setConnectionFactory(it) }
        return template
    }
}