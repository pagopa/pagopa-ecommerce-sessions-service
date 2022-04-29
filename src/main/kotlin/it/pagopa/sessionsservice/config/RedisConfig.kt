package it.pagopa.sessionsservice.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.convert.RedisCustomConversions
import java.util.*


@Configuration
class RedisConfig {
    @Value("\${redis.port}") val redisPort: String? = null
    @Value("\${redis.hostname}") val redisHostname: String? = null
    @Value("\${redis.password}") val redisPassword: String? = null
    val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun redisCustomConversions(
        readingByteConverter: RptIdReadingByteConverter?,
        writingByteConverter: RptIdWritingByteConverter?,
        readingStringConverter: RptIdReadingStringConverter?,
        writingStringConverter: RptIdWritingStringConverter?
    ): RedisCustomConversions? {
        return RedisCustomConversions(
            listOf(
                readingByteConverter,
                writingByteConverter,
                readingStringConverter,
                writingStringConverter
            )
        )
    }
    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory? {
        logger.info("Trying to connect with redis db - port: $redisPort, hostname: $redisHostname with password: ${!redisPassword.isNullOrEmpty()}")
        try {
            val redisStandaloneConfiguration =
                redisHostname?.let { RedisStandaloneConfiguration(it, Integer.parseInt(redisPort)) }
            redisStandaloneConfiguration?.password = RedisPassword.of(redisPassword)

            return redisStandaloneConfiguration?.let { JedisConnectionFactory(it) }
        } catch (exc: Exception) {
            logger.error("Failed to create connection with Redis db.")
            throw exc
        }
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String?, Any?>? {
        val template: RedisTemplate<String?, Any?> = RedisTemplate()
        jedisConnectionFactory()?.let { template.setConnectionFactory(it) }
        return template
    }
}