package net.irext.server.service.businesslogic;

import net.irext.server.service.model.UserApp;
import org.springframework.stereotype.Controller;

/**
 * Filename:       UserLoginLogic
 * Revised:        Date: 2019-06-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext private server login logic
 * <p>
 * Revision log:
 * 2019-06-08: created by strawmanbobi
 */
@Controller
public class UserLoginLogic {

    public UserApp login(String appKey, String appSecret, Integer appType,
                         String iosId, String androidPackageName, String androidSignature) {
        return null;
    }
}
