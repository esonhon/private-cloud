package net.irext.decode.service.businesslogic;

import net.irext.decode.service.mapper.RemoteIndexMapper;
import net.irext.decode.service.model.RemoteIndex;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Filename:       CollectCodeLogic
 * Revised:        Date: 2018-12-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext Code Collector Collect Code Logic
 * <p>
 * Revision log:
 * 2018-12-08: created by strawmanbobi
 */
@Controller
public class IndexLogic {

    private static IndexLogic indexLogic;

    private RemoteIndexMapper remoteIndexMapper;

    public static IndexLogic getInstance(RemoteIndexMapper remoteIndexMapper) {
        if (null == indexLogic) {
            indexLogic = new IndexLogic(remoteIndexMapper);
        }
        return indexLogic;
    }

    public IndexLogic(RemoteIndexMapper remoteIndexMapper) {
        this.remoteIndexMapper = remoteIndexMapper;
    }

    public RemoteIndex getRemoteIndex(int indexId) {
        List<RemoteIndex> remoteIndexList = remoteIndexMapper.getRemoteIndexById(indexId);
        if (null != remoteIndexList && remoteIndexList.size() > 0) {
            return remoteIndexList.get(0);
        }
        return null;
    }
}
