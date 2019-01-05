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

    private String sessionId;
    private Integer binaryId;

    public DecodeSession(String sessionId, Integer binaryId) {
        this.sessionId = sessionId;
        this.binaryId = binaryId;
    }

    public DecodeSession() {

    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getBinaryId() {
        return binaryId;
    }

    public void setBinaryId(Integer binaryId) {
        this.binaryId = binaryId;
    }
}
