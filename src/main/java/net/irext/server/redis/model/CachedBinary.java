package net.irext.server.redis.model;

import java.io.Serializable;

/**
 * Filename:       CachedBinary.java
 * Revised:        Date: 2017-05-16
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IR binary cache for performance optimization on decoding
 * <p>
 * Revision log:
 * 2017-05-16: created by strawmanbobi
 */
public class CachedBinary implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String OBJECT_KEY = "BINARY";

    private int id;
    private byte[] binaries;

    public CachedBinary() {
    }

    public CachedBinary(String id) {
    }

    public CachedBinary(int id, byte[] binaries) {
        this.id = id;
        this.binaries = binaries;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getBinaries() {
        return binaries;
    }

    public void setBinaries(byte[] binaries) {
        this.binaries = binaries;
    }

    @Override
    public String toString() {
        return "CachedBinary{" +
                "id='" + id + '\'' +
                ", binaries=" + binaries.length +
                '}';
    }

    public int getKey() {
        return getId();
    }

    public String getObjectKey() {
        return OBJECT_KEY;
    }
}