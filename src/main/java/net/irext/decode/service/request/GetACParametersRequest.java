package net.irext.decode.service.request;

/**
 * Filename:       GetACParametersRequest.java
 * Revised:        Date: 2017-05-16
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP decode online
 * <p>
 * Revision log:
 * 2019-02-14: created by strawmanbobi
 */
public class GetACParametersRequest {

    private int remoteIndexId;
    private String sessionId;
    private int mode;

    public GetACParametersRequest(int remoteIndexId, String sessionId, int mode) {
        this.remoteIndexId = remoteIndexId;
        this.sessionId = sessionId;
        this.mode = mode;
    }

    public GetACParametersRequest() {

    }

    public int getRemoteIndexId() {
        return remoteIndexId;
    }

    public void setRemoteIndexId(int remoteIndexId) {
        this.remoteIndexId = remoteIndexId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
