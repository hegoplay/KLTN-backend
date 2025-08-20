package iuh.fit.se.services.user_service.serviceImpl;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
    
    private final RedissonClient redissonClient;
    private static final String BLACKLIST_MAP = "jwt:blacklist:map";
    
    @Value("${jwt.expirationMs}")
	private int jwtExpirationMs;
    
    public TokenBlacklistService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
    
    public void blacklistToken(String token, long expirationTime, TimeUnit timeUnit) {
    	RMapCache<String, String> blacklist = redissonClient.getMapCache(BLACKLIST_MAP);
    	blacklist.put(token, "blacklisted", expirationTime, timeUnit);
    }
    
    public boolean isTokenBlacklisted(String token) {
        RMapCache<String, String> blacklist = redissonClient.getMapCache(BLACKLIST_MAP);
        return blacklist.containsKey(token);
    }
}