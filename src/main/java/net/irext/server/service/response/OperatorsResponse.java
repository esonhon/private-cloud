package net.irext.server.service.response;

import java.util.List;

/**
 * Filename:       OperatorsResponse.java
 * Revised:        Date: 2017-04-10
 * Revision:       Revision: 1.0
 * <p>
 * Description:    List STB operators response
 * <p>
 * Revision log:
 * 2017-04-10: created by strawmanbobi
 */
public class OperatorsResponse extends ServiceResponse {

    private List<net.irext.server.model.StbOperator> entity;

    public OperatorsResponse(Status status, List<net.irext.server.model.StbOperator> cities) {
        super(status);
        this.entity = cities;
    }

    public OperatorsResponse() {

    }

    public List<net.irext.server.model.StbOperator> getEntity() {
        return entity;
    }

    public void setEntity(List<net.irext.server.model.StbOperator> entity) {
        this.entity = entity;
    }
}
