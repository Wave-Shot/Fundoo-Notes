package com.fundoonotes.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching   // Activates @Cacheable, @CachePut, @CacheEvict annotations
public class RedisConfig {

    /*
     * RedisTemplate is the main class you use to talk to Redis manually.
     * Think of it like JdbcTemplate but for Redis.
     *
     * We configure two serializers:
     * - Key serializer: StringRedisSerializer — keys are stored as plain strings
     * - Value serializer: GenericJackson2JsonRedisSerializer — values stored as JSON
     *   so you can actually read them in Redis CLI if needed
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Keys will look like: "token:123" or "otp:user@gmail.com"
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Values will be stored as JSON strings
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}