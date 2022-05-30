package it.pagopa.sessionsservice.config


import it.pagopa.sessionsservice.domain.SessionData
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisConfiguration {
    @Bean
    fun redisOperations(factory: ReactiveRedisConnectionFactory?): ReactiveRedisOperations<String, SessionData> {
        val serializer: Jackson2JsonRedisSerializer<SessionData> = Jackson2JsonRedisSerializer(SessionData::class.java)
        val builder: RedisSerializationContextBuilder<String, SessionData> =
            RedisSerializationContext.newSerializationContext(StringRedisSerializer())
        val context: RedisSerializationContext<String, SessionData> = builder.value(serializer).build()

        return ReactiveRedisTemplate(factory!!, context)
    }
}