package net.irext.server.service.model;

/**
 * Filename:       IRBinary.java
 * Revised:        Date: 2018-12-30
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Remote binary in cache
 * <p>
 * Revision log:
 * 2018-12-30: created by strawmanbobi
 */
public class IRBinary {

    private Integer id;
    private byte[] binary;

    public IRBinary(Integer id, byte[] binary) {
        this.id = id;
        this.binary = binary;
    }

    public IRBinary() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getBinary() {
        return binary;
    }

    public void setBinary(byte[] binary) {
        this.binary = binary;
    }
}
