package net.irext.server.redis.service;

import net.irext.server.redis.model.CachedAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

/**
 * Filename:       AdminService.java
 * Revised:        Date: 2017-04-27
 * Revision:       Revision: 1.0
 * <p>
 * Description:    CachedAdmin redis service
 * <p>
 * Revision log:
 * 2017-04-27: created by strawmanbobi
 */

@Repository
public class AdminService {

    @Autowired
    RedisTemplate<String, CachedAdmin> redisTemplate;

    public RedisTemplate<String, CachedAdmin> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, CachedAdmin> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void put(CachedAdmin cachedAdmin) {
        ValueOperations<String, CachedAdmin> valueOper = redisTemplate.opsForValue();
        valueOper.set(cachedAdmin.getId(), cachedAdmin);
    }

    public void delete(String id) {
        ValueOperations<String, CachedAdmin> valueOper = redisTemplate.opsForValue();
        RedisOperations<String, CachedAdmin> RedisOperations = valueOper.getOperations();
        RedisOperations.delete(id);
    }

    public CachedAdmin get(String id) {
        ValueOperations<String, CachedAdmin> valueOper = redisTemplate.opsForValue();
        return valueOper.get(id);
    }
}
