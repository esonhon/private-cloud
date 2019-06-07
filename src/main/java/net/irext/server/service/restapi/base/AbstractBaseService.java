package net.irext.server.service.restapi.base;

import net.irext.server.service.Constants;
import net.irext.server.service.response.ServiceResponse;
import net.irext.server.service.response.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Filename:       AbstractBaseService.java
 * Revised:        Date: 2017-04-27
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Base restapi abstract class implemented restapi interface
 * <p>
 * Revision log:
 * 2017-04-27: created by strawmanbobi
 */
public abstract class AbstractBaseService {
    // note : not using ASPECT here but keep the ASPECT sketch
    protected static Log log = LogFactory.getLog(AbstractBaseService.class);

    protected ServiceResponse getExceptionResponse() {
        ServiceResponse r = new ServiceResponse();
        Status status = new Status();
        status.setCode(Constants.ERROR_CODE_AUTH_FAILURE);
        r.setStatus(status);
        return r;
    }

    protected <T extends ServiceResponse> T getExceptionResponse(Class<T> c) {
        T r = null;
        Status status = new Status();
        try {
            r = c.newInstance();
        } catch (Exception e) {
            log.error("Error when new instance of class: " + c.getName(), e);
        }
        status.setCode(Constants.ERROR_CODE_AUTH_FAILURE);
        if (null != r) {
            r.setStatus(status);
        }
        return r;
    }
}
