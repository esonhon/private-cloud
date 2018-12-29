package net.irext.decoder.redisrepo;

import net.irext.decoder.model.DecodeSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Filename:       RedisRepositoryImpl.java
 * Revised:        Date: 2018-12-29
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Redis cache class
 * <p>
 * Revision log:
 * 2018-12-29: created by strawmanbobi
 */
@Repository
public class RedisRepositoryImpl implements RedisRepository {
    private static final String KEY = "SESSION_KEY";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    public RedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public void add(final DecodeSession decodeSession) {
        hashOperations.put(KEY, decodeSession.getId(), decodeSession.getName());
    }

    public void delete(final Integer id) {
        hashOperations.delete(KEY, id);
    }

    public DecodeSession find(final Integer id){
        return (DecodeSession) hashOperations.get(KEY, id);
    }

    public Map<Object, Object> findAllDecodeSessions(){
        return hashOperations.entries(KEY);
    }


}