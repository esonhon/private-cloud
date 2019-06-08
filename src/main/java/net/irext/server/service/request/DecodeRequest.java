package net.irext.server.service.request;

import net.irext.server.sdk.bean.ACStatus;

/**
 * Filename:       DecodeRequest.java
 * Revised:        Date: 2017-05-16
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP server online
 * <p>
 * Revision log:
 * 2017-05-16: created by strawmanbobi
 */
public class DecodeRequest extends BaseRequest{

    private int remoteIndexId;
    private ACStatus acStatus;
    private int keyCode;
    private int changeWindDir;
    private String sessionId;

    public DecodeRequest(int remoteIndexId, ACStatus acStatus, int keyCode, int changeWindDir) {
        this.remoteIndexId = remoteIndexId;
        this.acStatus = acStatus;
        this.keyCode = keyCode;
        this.changeWindDir = changeWindDir;
    }

    public DecodeRequest() {

    }

    public int getRemoteIndexId() {
        return remoteIndexId;
    }

    public void setRemoteIndexId(int remoteIndexId) {
        this.remoteIndexId = remoteIndexId;
    }

    public ACStatus getAcStatus() {
        return acStatus;
    }

    public void setAcStatus(ACStatus acStatus) {
        this.acStatus = acStatus;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getChangeWindDir() {
        return changeWindDir;
    }

    public void setChangeWindDir(int changeWindDir) {
        this.changeWindDir = changeWindDir;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
