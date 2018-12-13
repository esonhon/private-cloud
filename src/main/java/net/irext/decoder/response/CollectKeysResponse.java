package net.irext.decoder.response;

import net.irext.decoder.model.CollectKey;

import java.util.List;

/**
 * Filename:       CollectKeysResponse.java
 * Revised:        Date: 2018-12-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP Response class
 * <p>
 * Revision log:
 * 2018-12-08: created by strawmanbobi
 */
public class CollectKeysResponse extends ServiceResponse {
    List<CollectKey> entity;

    public CollectKeysResponse(Status status, List<CollectKey> entity) {
        super(status);
        this.entity = entity;
    }

    public CollectKeysResponse() {

    }

    public List<CollectKey> getEntity() {
        return entity;
    }

    public void setEntity(List<CollectKey> entity) {
        this.entity = entity;
    }
}