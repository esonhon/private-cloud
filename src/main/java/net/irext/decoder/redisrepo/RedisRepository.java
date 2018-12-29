package net.irext.decoder.redisrepo;

import net.irext.decoder.model.DecodeSession;

import java.util.Map;

/**
 * Filename:       RedisRepository.java
 * Revised:        Date: 2018-12-29
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Redis cache class
 * <p>
 * Revision log:
 * 2018-12-29: created by strawmanbobi
 */
public interface RedisRepository {

    Map<Object, Object> findAllDecodeSessions();

    void add(DecodeSession decodeSession);

    void delete(Integer id);

    DecodeSession find(Integer id);

}