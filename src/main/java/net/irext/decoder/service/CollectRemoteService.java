package net.irext.decoder.service;

import net.irext.decoder.mapper.CollectRemoteMapper;
import net.irext.decoder.model.CollectRemote;
import net.irext.decoder.response.CollectRemotesResponse;
import net.irext.decoder.response.ServiceResponse;
import net.irext.decoder.response.Status;
import org.springframework.web.bind.annotation.*;

/**
 * Filename:       CollectRemoteService
 * Revised:        Date: 2018-12-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext Remote Management Service
 * <p>
 * Revision log:
 * 2018-12-08: created by strawmanbobi
 */
@RestController
@RequestMapping("/irext/collect")
public class CollectRemoteService {

    private CollectRemoteMapper collectRemoteMapper;

    public CollectRemoteService(CollectRemoteMapper collectRemoteMapper) {
        this.collectRemoteMapper = collectRemoteMapper;
    }

    @GetMapping("/list_collect_remotes")
    public CollectRemotesResponse listCollectRemotes() {
        CollectRemotesResponse collectRemotesResponse = new CollectRemotesResponse();
        collectRemotesResponse.setEntity(collectRemoteMapper.listCollectRemotes());
        collectRemotesResponse.setStatus(new Status());
        return collectRemotesResponse;
    }

    @PostMapping("/create_collect_remote")
    private ServiceResponse createCollectRemote(@RequestBody CollectRemote collectRemote) {

        ServiceResponse serviceResponse = new ServiceResponse();
        collectRemoteMapper.createCollectRemote(collectRemote);
        serviceResponse.setStatus(new Status());
        return serviceResponse;
    }
}
