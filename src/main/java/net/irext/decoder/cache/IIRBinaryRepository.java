package net.irext.decoder.cache;

import net.irext.decoder.model.IRBinary;

import java.util.Map;

/**
 * Filename:       IRRepository.java
 * Revised:        Date: 2018-12-30
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IR binary cache for performance optimization on decoding
 * <p>
 * Revision log:
 * 2018-12-30: created by strawmanbobi
 */
public interface IIRBinaryRepository {

    Map<Object, Object> findAllIRBinaries();

    void add(Integer id, byte[] binaries);

    void delete(Integer id);

    byte[] find(Integer id);
}