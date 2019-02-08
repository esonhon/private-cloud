package net.irext.decode.service.request;

/**
 * Filename:       OpenRequest.java
 * Revised:        Date: 2018-12-18
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP decode online
 * <p>
 * Revision log:
 * 2018-12-18: created by strawmanbobi
 */
public class OpenRequest {

    private int remoteIndexId;

    public OpenRequest(int remoteIndexId) {
        this.remoteIndexId = remoteIndexId;
    }

    public OpenRequest() {

    }

    public int getRemoteIndexId() {
        return remoteIndexId;
    }

    public void setRemoteIndexId(int remoteIndexId) {
        this.remoteIndexId = remoteIndexId;
    }

}
