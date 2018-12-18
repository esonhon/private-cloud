package net.irext.decoder.businesslogic;

import net.irext.decoder.model.RemoteIndex;
import org.springframework.stereotype.Controller;

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

    public static IndexLogic getInstance() {
        if (null == indexLogic) {
            indexLogic = new IndexLogic();
        }
        return indexLogic;
    }

    public RemoteIndex getRemoteIndex(int indexId) {
        return null;
    }
}
