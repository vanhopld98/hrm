package vn.com.humanresourcesmanagement.configuration;

/*
 * Class này sử dụng để cấu hình sử dụng redis làm cache
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import vn.com.humanresourcesmanagement.common.constants.CacheConstant;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration extends CachingConfigurerSupport {

    private static final String SPACE = "_";

    @Value(value = "${spring.cache.redis.key-prefix}")
    private String springCacheRedisKeyPrefix;

    @Value("${spring.cache.redis.use-key-prefix}")
    private boolean springCacheRedisUseKeyPrefix;

    @Value("${spring.cache.redis.time-to-live}")
    private long springCacheRedisTimeToLive;

    private CacheKeyPrefix cacheKeyPrefix;

    @PostConstruct
    private void onPostConstruct() {
        if (springCacheRedisKeyPrefix != null) {
            springCacheRedisKeyPrefix = springCacheRedisKeyPrefix.trim();
        }
        if (springCacheRedisUseKeyPrefix && springCacheRedisKeyPrefix != null
                && !springCacheRedisKeyPrefix.isEmpty()) {
            // tạo folder cache chuẩn
            // ví dụ : contract-service:EContract.DisbursementMethodChanelNameV5
            // không nên sử dụng :: để sinh folder vì khi dùng :: sẽ bị sinh ra folder [Empty] đứng giữ
            cacheKeyPrefix = cacheName -> springCacheRedisKeyPrefix + ":" + cacheName + ":";
        } else {
            cacheKeyPrefix = CacheKeyPrefix.simple();
        }
    }

    @Primary
    @Bean("cacheManager")
    public RedisCacheManager cacheManagerDefault(@Qualifier("jedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .computePrefixWith(cacheKeyPrefix)
                        .entryTtl(Duration.ofSeconds(springCacheRedisTimeToLive))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                )
                .build();
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

    @Bean(CacheConstant.CACHE_KEY)
    @Override
    public KeyGenerator keyGenerator() {
        return (Object target, Method method, Object... params) -> StringUtils.arrayToDelimitedString(params, SPACE);
    }

}

class CustomCacheErrorHandler implements CacheErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException exception, @Nullable Cache cache, @Nullable Object key) {
        logger.warn(exception.getMessage(), cache, key);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, @Nullable Cache cache, @Nullable Object key, Object value) {
        logger.warn(exception.getMessage(), cache, key);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, @Nullable Cache cache, @Nullable Object key) {
        logger.warn(exception.getMessage(), cache, key);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, @Nullable Cache cache) {
        logger.warn(exception.getMessage(), cache);
    }

}
