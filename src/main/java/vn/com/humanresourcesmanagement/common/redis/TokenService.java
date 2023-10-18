package vn.com.humanresourcesmanagement.common.redis;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings("unchecked")
public class TokenService extends RedisService {

    private static final String SUB_FIX = "_access_token_customers";

    @Override
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key.toLowerCase() + SUB_FIX);
    }

    @Override
    public void set(String key, String token) {
        redisTemplate.opsForValue().set(key.toLowerCase() + SUB_FIX, token);
    }

    @Override
    public void set(String key, String token, Date expiredDate) {
        long diff = expiredDate.getTime() - new Date().getTime();
        redisTemplate.opsForValue().set(key.toLowerCase() + SUB_FIX, token, diff, TimeUnit.MILLISECONDS);
    }

    public void remove(String key) {
        redisTemplate.delete(key.toLowerCase() + SUB_FIX);
    }

}