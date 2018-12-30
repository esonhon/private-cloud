package net.irext.decoder.redisrepo.impl;

import net.irext.decoder.model.IRBinary;
import net.irext.decoder.redisrepo.IIRBinaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
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
    private static final String KEY = "SESSION_KEY";

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

    public void add(final IRBinary IRBinary) {
        hashOperations.put(KEY, IRBinary.getId(), IRBinary.getBinary());
    }

    public void delete(final Integer id) {
        hashOperations.delete(KEY, id);
    }

    public IRBinary find(final Integer id) {
        return (IRBinary) hashOperations.get(KEY, id);
    }

    public Map<Object, Object> findAllIRBinaries() {
        return hashOperations.entries(KEY);
    }
}