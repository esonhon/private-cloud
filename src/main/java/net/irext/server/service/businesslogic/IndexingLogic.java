package net.irext.server.service.businesslogic;

import net.irext.server.service.mapper.RemoteIndexMapper;
import net.irext.server.service.model.Category;
import net.irext.server.service.model.RemoteIndex;
import org.springframework.beans.factory.annotation.Autowired;
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
public class IndexingLogic {

    @Autowired
    private RemoteIndexMapper remoteIndexMapper;

    public RemoteIndex getRemoteIndex(int indexId) {
        List<RemoteIndex> remoteIndexList = remoteIndexMapper.getRemoteIndexById(indexId);
        if (null != remoteIndexList && remoteIndexList.size() > 0) {
            return remoteIndexList.get(0);
        }
        return null;
    }

    public List<Category>  listCategories(int lang, int from, int count) {
        return null;
    }
}
