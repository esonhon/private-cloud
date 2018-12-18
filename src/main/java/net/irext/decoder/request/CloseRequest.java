package net.irext.decoder.request;

/**
 * Filename:       CloseRequest.java
 * Revised:        Date: 2018-12-18
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP decode online
 * <p>
 * Revision log:
 * 2018-12-18: created by strawmanbobi
 */
public class CloseRequest {

    private int indexId;

    public CloseRequest(int indexId) {
        this.indexId = indexId;
    }

    public CloseRequest() {

    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

}
