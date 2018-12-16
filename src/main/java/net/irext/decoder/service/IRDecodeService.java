package net.irext.decoder.service;

import net.irext.decoder.model.RemoteIndex;
import net.irext.decoder.request.DecodeRequest;
import net.irext.decoder.response.DecodeResponse;
import net.irext.decoder.response.Status;
import net.irext.decodesdk.bean.ACStatus;
import net.irext.decodesdk.utils.Constants;
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
public class IRDecodeService {

    public IRDecodeService() {

    }

    @GetMapping("/decode")
    public DecodeResponse irDecode(@RequestBody DecodeRequest decodeRequest) {
        try {
            int indexId = decodeRequest.getIndexId();
            ACStatus acstatus = decodeRequest.getAcStatus();
            int keyCode = decodeRequest.getKeyCode();
            int changeWindDir = decodeRequest.getChangeWindDir();

            RemoteIndex index = indexingLogic.getRemoteIndex(indexId);
            if (null == index) {
                response.setEntity(null);
                response.setStatus(new Status(Constants.ERROR_CODE_NETWORK_ERROR, ""));
                return response;
            }

            byte []binaryContent = operationLogic.prepareBinary(indexId);
            System.out.println("binary content fetched : " + binaryContent.length);
            int []decoded = operationLogic.decodeIR(index.getCategoryId(), index.getSubCate(),
                    binaryContent, acstatus, keyCode, changeWindDir);
            response.setEntity(decoded);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return getExceptionResponse(DecodeResponse.class);
        }
    }
}
