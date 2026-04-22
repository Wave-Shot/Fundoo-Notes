package com.fundoonotes.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    // RedisTemplate is our connection to Redis
    // Spring auto-injects the one we configured in RedisConfig
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /*
     * Store any value with a key and expiry time.
     * Example: store("otp:user@gmail.com", "483921", Duration.ofMinutes(10))
     * After 10 minutes Redis automatically deletes this entry.
     */
    public void store(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /*
     * Retrieve a value by key.
     * Returns null if key doesn't exist or has expired.
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /*
     * Check if a key exists in Redis.
     * Useful for checking if a token is blacklisted.
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /*
     * Delete a key manually.
     * Useful when OTP is verified — delete it so it can't be reused.
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // ── Convenience methods with standard key prefixes ──────────────────────

    // Token blacklist: when user logs out, store their token here until it expires
    public void blacklistToken(String token, Duration ttl) {
        store("blacklist:" + token, "true", ttl);
    }

    public boolean isTokenBlacklisted(String token) {
        return exists("blacklist:" + token);
    }

    // OTP storage: for forgot password flow
    public void storeOtp(String email, String otp) {
        store("otp:" + email, otp, Duration.ofMinutes(10));
    }

    public String getOtp(String email) {
        Object value = get("otp:" + email);
        return value != null ? value.toString() : null;
    }

    public void deleteOtp(String email) {
        delete("otp:" + email);
    }

    // Verification token: for email verification after registration
    public void storeVerificationToken(String email, String token) {
        store("verify:" + email, token, Duration.ofHours(24));
    }

    public String getVerificationToken(String email) {
        Object value = get("verify:" + email);
        return value != null ? value.toString() : null;
    }
}