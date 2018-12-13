package net.irext.collector.response;

import net.irext.collector.model.CollectRemote;

import java.util.List;

/**
 * Filename:       CollectRemotesResponse.java
 * Revised:        Date: 2018-12-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP Response class
 * <p>
 * Revision log:
 * 2018-12-08: created by strawmanbobi
 */
public class CollectRemotesResponse extends ServiceResponse {
    List<CollectRemote> entity;

    public CollectRemotesResponse(Status status, List<CollectRemote> entity) {
        super(status);
        this.entity = entity;
    }

    public CollectRemotesResponse() {

    }

    public List<CollectRemote> getEntity() {
        return entity;
    }

    public void setEntity(List<CollectRemote> entity) {
        this.entity = entity;
    }
}
