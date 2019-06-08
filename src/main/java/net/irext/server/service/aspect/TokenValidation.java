package net.irext.server.service.aspect;

import net.irext.server.service.response.ServiceResponse;

/**
 * Filename:       TokenValidation.java
 * Revised:        Date: 2017-05-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Token validation aspect logic
 * <p>
 * Revision log:
 * 2017-05-08: created by strawmanbobi
 */
public interface TokenValidation {

    ServiceResponse validateToken(String userId, String token);

    <T extends ServiceResponse> T validateToken(String userId, String token,
                                                Class<T> c);

}
