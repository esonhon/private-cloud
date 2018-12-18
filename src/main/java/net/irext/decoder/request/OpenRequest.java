package net.irext.decoder.request;

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

    private int indexId;

    public OpenRequest(int indexId) {
        this.indexId = indexId;
    }

    public OpenRequest() {

    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

}
