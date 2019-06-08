package net.irext.server.service.restapi;

import net.irext.server.service.Constants;
import net.irext.server.service.businesslogic.UserLoginLogic;
import net.irext.server.service.cache.IUserAppRepository;
import net.irext.server.service.model.UserApp;
import net.irext.server.service.request.AppSignInRequest;
import net.irext.server.service.response.LoginResponse;
import net.irext.server.service.response.Status;
import net.irext.server.service.restapi.base.AbstractBaseService;
import net.irext.server.service.utils.MD5Util;
import net.irext.server.service.utils.VeDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Filename:       SignInServiceImpl.java
 * Revised:        Date: 2017-04-27
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Admin service interface implementation
 * <p>
 * Revision log:
 * 2017-04-27: created by strawmanbobi
 */
@RestController
@RequestMapping("/irext-server/app")
@Service("SignInService")
public class SignInService extends AbstractBaseService {

    @Autowired
    private UserLoginLogic loginLogic;

    @Autowired
    private IUserAppRepository userAppRepository;

    @PostMapping("/app_login")
    public LoginResponse signIn(AppSignInRequest appSignInRequest) {
        try {
            LoginResponse response = new LoginResponse();
            response.setStatus(new Status());

            if (appSignInRequest == null ||
                    StringUtils.isEmpty(appSignInRequest.getAppKey()) ||
                    StringUtils.isEmpty(appSignInRequest.getAppSecret())) {
                response.getStatus().setCode(Constants.ERROR_CODE_NETWORK_ERROR);
                return response;
            }

            UserApp userApp = loginLogic.login(appSignInRequest.getAppKey(),
                    appSignInRequest.getAppSecret(),
                    appSignInRequest.getAppType(),
                    appSignInRequest.getiOSID(),
                    appSignInRequest.getAndroidPackageName(),
                    appSignInRequest.getAndroidSignature());

            if (userApp != null) {
                // generate token by date time
                String currentTime = VeDate.getNow().toString();
                String tokenSource = currentTime + userApp.getAppKey();
                String token = MD5Util.MD5Encode(tokenSource, null);
                userAppRepository.add(userApp.getId(), token);

                userApp.setToken(token);

                response.getStatus().setCode(Constants.ERROR_CODE_SUCCESS);
                response.setEntity(userApp);
            } else {
                response.getStatus().setCode(Constants.ERROR_CODE_AUTH_FAILURE);
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return getExceptionResponse(LoginResponse.class);
        }
    }
}
