package net.irext.decoder.model;

/**
 * Filename:       DecodeSession.java
 * Revised:        Date: 2018-12-29
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Decode session for decoding within connection
 * <p>
 * Revision log:
 * 2018-12-29: created by strawmanbobi
 */
public class DecodeSession {

    private Integer id;
    private String name;

    public DecodeSession(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public DecodeSession() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
