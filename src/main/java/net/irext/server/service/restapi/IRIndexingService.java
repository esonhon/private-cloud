package net.irext.server.service.restapi;

import net.irext.server.service.Constants;
import net.irext.server.service.businesslogic.IndexingLogic;
import net.irext.server.service.mapper.RemoteIndexMapper;
import net.irext.server.service.model.Category;
import net.irext.server.service.request.*;
import net.irext.server.service.response.*;
import net.irext.server.service.restapi.base.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import java.util.List;

/**
 * Filename:       IRIndexingService.java
 * Revised:        Date: 2019-06-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext Indexing Webservice
 * <p>
 * Revision log:
 * 2019-06-08: created by strawmanbobi
 */
@RestController
@RequestMapping("/irext-server/indexing")
@Service("IRIndexingService")
public class IRIndexingService extends AbstractBaseService {

    private static final String TAG = IRIndexingService.class.getSimpleName();

    @Autowired
    private ServletContext context;

    @Autowired
    private IndexingLogic indexingLogic;

    public IRIndexingService(RemoteIndexMapper remoteIndexMapper) {
    }

    @PostMapping("/list_categories")
    public CategoriesResponse listCategories(HttpServletRequest request,
                                             @HeaderParam("user-lang") String userLang,
                                             @RequestBody ListCategoriesRequest listCategoriesRequest) {
        try {
            int id = listCategoriesRequest.getId();
            String token = listCategoriesRequest.getToken();
            int from = listCategoriesRequest.getFrom();
            int count = listCategoriesRequest.getCount();
            int lang = Constants.LANG_ZH_CN;

            if (null != userLang) {
                if (userLang.equals("en-US")) {
                    lang = Constants.LANG_EN;
                } else if (userLang.equals("zh-TW")) {
                    lang = Constants.LANG_TW_CN;
                } else {
                    lang = Constants.LANG_ZH_CN;
                }
            }

            CategoriesResponse response = validateToken(id, token, CategoriesResponse.class);
            if (response.getStatus().getCode() == Constants.ERROR_CODE_AUTH_FAILURE) {
                return response;
            }

            List<Category> categoryList = indexingLogic.listCategories(lang, from, count);

            if (categoryList != null) {
                response.getStatus().setCode(Constants.ERROR_CODE_SUCCESS);
                response.setEntity(categoryList);
            } else {
                response.getStatus().setCode(Constants.ERROR_CODE_NETWORK_ERROR);
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return getExceptionResponse(CategoriesResponse.class);
        }
    }

}
