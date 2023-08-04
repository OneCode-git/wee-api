//package com.wee.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
//
//import java.time.Duration;
//
//@Configuration
//public class RedisCacheConfig {
//
//    @Value("${cache.ttl.minutes:60}")
//    private long cacheTtlMinutes;
//
//    @Bean
//    public RedisCacheConfiguration cacheConfiguration() {
//        return RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(cacheTtlMinutes))
//                .disableCachingNullValues()
//                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
//    }
//
//    @Bean
//    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
//        return (builder) -> builder
//                .withCacheConfiguration("linkCache",
//                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
//    }
//}
