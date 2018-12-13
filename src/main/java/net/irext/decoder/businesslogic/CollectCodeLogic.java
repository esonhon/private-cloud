package net.irext.decoder.businesslogic;

import net.irext.decoder.model.IRCode;
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
public class CollectCodeLogic {

    private Integer codeCollectState = 0;
    private IRCode irCode;

    private static CollectCodeLogic collectCodeLogic;

    public static CollectCodeLogic getInstance(IRCode irCode) {
        if (null == collectCodeLogic) {
            collectCodeLogic = new CollectCodeLogic(irCode);
        }
        return collectCodeLogic;
    }

    public CollectCodeLogic(IRCode irCode) {
        this.irCode = irCode;
    }

    public CollectCodeLogic() {

    }

    public boolean collectCodeWorkUnit() {
        // step 1. figure out the category and brand for this code

        // step 2. go through the FSM for remote in each category
        codeCollectState++;
        System.out.println("state = " + codeCollectState + " : " + irCode.getKey());

        // step 3. record key code

        // step 4. temporarily save to file

        return true;
    }
}
