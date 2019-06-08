package net.irext.server.redis.model;

import java.io.Serializable;

/**
 * Filename:       CachedAdmin.java
 * Revised:        Date: 2017-04-27
 * Revision:       Revision: 1.0
 * <p>
 * Description:    CachedAdmin DAO for redis
 * <p>
 * Revision log:
 * 2017-04-27: created by strawmanbobi
 */
public class CachedAdmin implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String OBJECT_KEY = "ADMIN";

    private String id;
    private String token;

    public CachedAdmin() {
    }

    public CachedAdmin(String id) {
    }

    public CachedAdmin(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String toString() {
        return "CachedAdmin [id=" + id + ", token=" + token + "]";
    }

    public String getKey() {
        return getId();
    }

    public String getObjectKey() {
        return OBJECT_KEY;
    }
}