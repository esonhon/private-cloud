package net.irext.server.service.restapi;

import net.irext.server.service.businesslogic.DecodeLogic;
import net.irext.server.service.cache.IDecodeSessionRepository;
import net.irext.server.service.cache.IIRBinaryRepository;
import net.irext.server.service.mapper.RemoteIndexMapper;
import net.irext.server.service.model.ACParameters;
import net.irext.server.service.model.DecodeSession;
import net.irext.server.service.model.RemoteIndex;
import net.irext.server.service.request.CloseRequest;
import net.irext.server.service.request.DecodeRequest;
import net.irext.server.service.request.GetACParametersRequest;
import net.irext.server.service.request.OpenRequest;
import net.irext.server.service.response.*;
import net.irext.server.service.utils.LoggerUtil;
import net.irext.server.service.utils.MD5Util;
import net.irext.server.service.businesslogic.IndexingLogic;
import net.irext.server.service.restapi.base.AbstractBaseService;
import net.irext.server.sdk.bean.ACStatus;
import net.irext.server.sdk.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

/**
 * Filename:       IRDecodeService.java
 * Revised:        Date: 2018-12-16
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext Decode Webservice
 * <p>
 * Revision log:
 * 2018-12-16: created by strawmanbobi
 */
@RestController
@RequestMapping("/irext-server/decode")
@Service("IRDecodeService")
public class IRDecodeService extends AbstractBaseService {

    private static final String TAG = IRDecodeService.class.getSimpleName();

    private RemoteIndexMapper remoteIndexMapper;

    @Autowired
    private ServletContext context;

    @Autowired
    private IIRBinaryRepository irBinaryRepository;

    @Autowired
    private IDecodeSessionRepository decodeSessionRepository;

    public IRDecodeService(RemoteIndexMapper remoteIndexMapper) {
        this.remoteIndexMapper = remoteIndexMapper;
    }

    @PostMapping("/open")
    public StringResponse irOpen(HttpServletRequest request, @RequestBody OpenRequest openRequest) {
        try {
            int remoteIndexId = openRequest.getRemoteIndexId();

            LoggerUtil.getInstance().trace(TAG, "irOpen API called : " + remoteIndexId);

            StringResponse response = new StringResponse();
            RemoteIndex remoteIndex = IndexingLogic.getInstance(remoteIndexMapper).getRemoteIndex(remoteIndexId);
            if (null == remoteIndex) {
                response.setStatus(new Status(Constants.ERROR_CODE_NETWORK_ERROR,
                        Constants.ERROR_CODE_NETWORK_ERROR_TEXT));
                response.setEntity(null);
                return response;
            } else {
                LoggerUtil.getInstance().trace(TAG, "remoteIndex get : " + remoteIndex.getId() + ", " +
                        remoteIndex.getRemoteMap());
            }
            RemoteIndex cachedRemoteIndex =
                    DecodeLogic.getInstance().openIRBinary(context, irBinaryRepository, remoteIndex);

            if (null != cachedRemoteIndex) {
                LoggerUtil.getInstance().trace(TAG, "binary content fetched : " +
                        cachedRemoteIndex.getRemoteMap());
                remoteIndex.setBinaries(cachedRemoteIndex.getBinaries());
                // construct a session with this binary
                String address = request.getRemoteAddr();
                LoggerUtil.getInstance().trace(TAG, "request Address = " + address);
                String timeStamp =
                        new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                String sessionId = MD5Util.MD5Encode(address + timeStamp, null);
                DecodeSession decodeSession = new DecodeSession(sessionId, remoteIndex.getId());
                decodeSessionRepository.add(decodeSession.getSessionId(), decodeSession.getBinaryId());
                response.setEntity(decodeSession.getSessionId());
            }
            response.setStatus(new Status(Constants.ERROR_CODE_SUCCESS, Constants.ERROR_CODE_SUCESS_TEXT));
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return getExceptionResponse(StringResponse.class);
        }
    }

    @PostMapping("/get_ac_parameters")
    public ACParametersResponse getACParameters(@RequestBody GetACParametersRequest getACParametersRequest) {
        try {
            int remoteIndexId = getACParametersRequest.getRemoteIndexId();
            String sessionId = getACParametersRequest.getSessionId();
            int mode = getACParametersRequest.getMode();

            RemoteIndex cachedRemoteIndex = getCachedRemoteIndex(sessionId, remoteIndexId);
            ACParametersResponse response = new ACParametersResponse();

            if (null == cachedRemoteIndex) {
                response.setEntity(null);
                response.setStatus(new Status(Constants.ERROR_CODE_INVALID_SESSION,
                        Constants.ERROR_CODE_INVALID_SESSION_TEXT));
                return response;
            }

            if (cachedRemoteIndex.getCategoryId() != Constants.CategoryID.AIR_CONDITIONER.getValue()) {
                response.setEntity(null);
                response.setStatus(new Status(Constants.ERROR_CODE_INVALID_CATEGORY,
                        Constants.ERROR_CODE_INVALID_CATEGORY_TEXT));
                return response;
            }

            ACParameters acParameters = DecodeLogic.getInstance().getACParameters(cachedRemoteIndex, mode);

            response.setStatus(new Status(Constants.ERROR_CODE_SUCCESS, Constants.ERROR_CODE_SUCESS_TEXT));
            response.setEntity(acParameters);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return getExceptionResponse(ACParametersResponse.class);
        }
    }

    @PostMapping("/decode")
    public DecodeResponse irDecode(@RequestBody DecodeRequest decodeRequest) {
        try {
            int remoteIndexId = decodeRequest.getRemoteIndexId();
            ACStatus acStatus = decodeRequest.getAcStatus();
            int keyCode = decodeRequest.getKeyCode();
            int changeWindDir = decodeRequest.getChangeWindDir();
            String sessionId = decodeRequest.getSessionId();

            RemoteIndex cachedRemoteIndex = getCachedRemoteIndex(sessionId, remoteIndexId);;
            DecodeResponse response = new DecodeResponse();

            if (null == cachedRemoteIndex) {
                response.setEntity(null);
                response.setStatus(new Status(Constants.ERROR_CODE_INVALID_SESSION,
                        Constants.ERROR_CODE_INVALID_SESSION_TEXT));
                return response;
            }

            int[] irArray = DecodeLogic.getInstance().decode(
                    cachedRemoteIndex,
                    acStatus,
                    keyCode,
                    changeWindDir);

            response.setStatus(new Status(Constants.ERROR_CODE_SUCCESS, Constants.ERROR_CODE_SUCESS_TEXT));
            response.setEntity(irArray);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return getExceptionResponse(DecodeResponse.class);
        }
    }

    @PostMapping("/close")
    public ServiceResponse irClose(@RequestBody CloseRequest closeRequest) {
        try {
            String sessionId = closeRequest.getSessionId();
            ServiceResponse response = new ServiceResponse();
            DecodeLogic.getInstance().close(decodeSessionRepository, sessionId);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return getExceptionResponse(DecodeResponse.class);
        }
    }

    private RemoteIndex getCachedRemoteIndex(String sessionId, int remoteIndexId) {
        RemoteIndex cachedRemoteIndex = null;

        if (null == sessionId) {
            LoggerUtil.getInstance().trace(TAG, "sessionId is not given, abort");
        } else {
            Integer cachedRemoteIndexId = decodeSessionRepository.find(sessionId);
            if (null != cachedRemoteIndexId) {
                cachedRemoteIndex = irBinaryRepository.find(cachedRemoteIndexId);
                if (null != cachedRemoteIndex) {
                    if (remoteIndexId != cachedRemoteIndex.getId()) {
                        cachedRemoteIndex = null;
                    }
                }
            }
        }
        return cachedRemoteIndex;
    }
}
