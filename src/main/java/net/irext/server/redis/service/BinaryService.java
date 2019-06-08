package net.irext.server.redis.service;

import net.irext.server.redis.model.CachedBinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

/**
 * Filename:       BinaryService.java
 * Revised:        Date: 2017-04-27
 * Revision:       Revision: 1.0
 * <p>
 * Description:    CachedBinary redis service
 * <p>
 * Revision log:
 * 2017-04-27: created by strawmanbobi
 */

@Repository
public class BinaryService {

    @Autowired
    RedisTemplate<Integer, CachedBinary> redisTemplate;

    public RedisTemplate<Integer, CachedBinary> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<Integer, CachedBinary> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void put(CachedBinary cachedBinary) {
        ValueOperations<Integer, CachedBinary> valueOper = redisTemplate.opsForValue();
        valueOper.set(cachedBinary.getId(), cachedBinary);
    }

    public void delete(Integer id) {
        ValueOperations<Integer, CachedBinary> valueOper = redisTemplate.opsForValue();
        RedisOperations<Integer, CachedBinary> RedisOperations = valueOper.getOperations();
        RedisOperations.delete(id);
    }

    public CachedBinary get(Integer id) {
        ValueOperations<Integer, CachedBinary> valueOper = redisTemplate.opsForValue();
        return valueOper.get(id);
    }
}
