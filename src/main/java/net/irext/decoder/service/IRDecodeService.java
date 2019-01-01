package net.irext.decoder.service;

import net.irext.decoder.businesslogic.DecodeLogic;
import net.irext.decoder.businesslogic.IndexLogic;
import net.irext.decoder.mapper.RemoteIndexMapper;
import net.irext.decoder.model.RemoteIndex;
import net.irext.decoder.redisrepo.IDecodeSessionRepository;
import net.irext.decoder.redisrepo.IIRBinaryRepository;
import net.irext.decoder.request.CloseRequest;
import net.irext.decoder.request.DecodeRequest;
import net.irext.decoder.request.OpenRequest;
import net.irext.decoder.response.DecodeResponse;
import net.irext.decoder.response.ServiceResponse;
import net.irext.decoder.response.Status;
import net.irext.decoder.service.base.AbstractBaseService;
import net.irext.decodesdk.bean.ACStatus;
import net.irext.decodesdk.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Filename:       IRDecodeService.java
 * Revised:        Date: 2018-12-16
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext Decode WebService
 * <p>
 * Revision log:
 * 2018-12-16: created by strawmanbobi
 */
@RestController
@RequestMapping("/irext")
public class IRDecodeService extends AbstractBaseService {

    Logger logger = LogManager.getLogger(IRDecodeService.class);

    private RemoteIndexMapper remoteIndexMapper;

    @Autowired
    private IIRBinaryRepository irBinaryRepository;

    @Autowired
    private IDecodeSessionRepository decodeSessionRepository;

    public IRDecodeService(RemoteIndexMapper remoteIndexMapper) {
        this.remoteIndexMapper = remoteIndexMapper;
    }

    @PostMapping("/open")
    public ServiceResponse irOpen(@RequestBody OpenRequest openRequest) {
        try {
            int remoteIndexId = openRequest.getRemoteIndexId();

            logger.trace("irOpen API called : " + remoteIndexId);

            ServiceResponse response = new ServiceResponse();
            RemoteIndex remoteIndex = IndexLogic.getInstance(remoteIndexMapper).getRemoteIndex(remoteIndexId);
            if (null == remoteIndex) {
                response.setStatus(new Status(Constants.ERROR_CODE_NETWORK_ERROR, ""));
                return response;
            }
            byte []binaryContent = DecodeLogic.getInstance().openIRBinary(irBinaryRepository, remoteIndex);
            System.out.println("binary content fetched : " + binaryContent.length);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return getExceptionResponse(DecodeResponse.class);
        }
    }

    @PostMapping("/decode")
    public DecodeResponse irDecode(@RequestBody DecodeRequest decodeRequest) {
        try {
            int indexId = decodeRequest.getIndexId();
            ACStatus acstatus = decodeRequest.getAcStatus();
            int keyCode = decodeRequest.getKeyCode();
            int changeWindDir = decodeRequest.getChangeWindDir();

            DecodeResponse response = new DecodeResponse();
            int[] irArray = DecodeLogic.getInstance().decode();

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return getExceptionResponse(DecodeResponse.class);
        }
    }

    @PostMapping("/close")
    public ServiceResponse irClose(@RequestBody CloseRequest closeRequest) {
        try {

            ServiceResponse response = new ServiceResponse();
            DecodeLogic.getInstance().decode();

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return getExceptionResponse(DecodeResponse.class);
        }
    }
}
