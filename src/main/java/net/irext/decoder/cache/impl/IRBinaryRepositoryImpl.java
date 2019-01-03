package net.irext.decoder.cache.impl;

import net.irext.decoder.cache.IIRBinaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * Filename:       IIRBinaryRepositoryImpl.java
 * Revised:        Date: 2018-12-29
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Redis cache class
 * <p>
 * Revision log:
 * 2018-12-29: created by strawmanbobi
 */
@Repository
public class IRBinaryRepositoryImpl implements IIRBinaryRepository {
    private static final String KEY = "BINARY_KEY";

    @Resource(name="redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    public IRBinaryRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public void add(Integer id, byte[] binaries) {
        hashOperations.put(KEY, id, binaries);
    }

    public void delete(final Integer id) {
        hashOperations.delete(KEY, id);
    }

    public byte[] find(final Integer id) {
        return (byte[])hashOperations.get(KEY, id);
    }

    public Map<Object, Object> findAllIRBinaries() {
        return hashOperations.entries(KEY);
    }
}