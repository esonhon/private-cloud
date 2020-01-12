package net.irext.server.service.cache;

/**
 * Filename:       IUserAppRepository.java
 * Revised:        Date: 2019-06-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext user application cache repository
 * <p>
 * Revision log:
 * 2019-06-08: created by strawmanbobi
 */
public interface IUserAppRepository {

    void add(Integer id, String token);

    void delete(Integer id);

    String find(Integer id);
}