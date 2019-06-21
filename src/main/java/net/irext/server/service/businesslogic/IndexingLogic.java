package net.irext.server.service.businesslogic;

import net.irext.server.service.Constants;
import net.irext.server.service.mapper.*;
import net.irext.server.service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Filename:       IndexingLogic
 * Revised:        Date: 2019-06-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext private server indexing logic
 * <p>
 * Revision log:
 * 2019-06-08: created by strawmanbobi
 */
@Controller
public class IndexingLogic {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private OperatorMapper operatorMapper;

    @Autowired
    private RemoteIndexMapper remoteIndexMapper;

    public RemoteIndex getRemoteIndex(int indexId) {
        List<RemoteIndex> remoteIndexList = remoteIndexMapper.getRemoteIndexById(indexId);
        if (null != remoteIndexList && remoteIndexList.size() > 0) {
            return remoteIndexList.get(0);
        }
        return null;
    }

    public List<Category> listCategories(int lang, int from, int count) {
        List<Category> categoryList = categoryMapper.listCategories(from, count);
        if (lang == Constants.LANG_EN) {
            for (Category category : categoryList) {
                category.setName(category.getNameEn());
            }
        } else if (lang == Constants.LANG_TW_CN) {
            for (Category category : categoryList) {
                category.setName(category.getNameTw());
            }
        }
        return categoryList;
    }

    public List<Brand> listBrands(int lang, int categoryId, int from, int count) {
        List<Brand> brandList = brandMapper.listBrands(categoryId, from, count);
        if (lang == Constants.LANG_EN) {
            for (Brand brand : brandList) {
                brand.setName(brand.getNameEn());
            }
        } else if (lang == Constants.LANG_TW_CN) {
            for (Brand brand : brandList) {
                brand.setName(brand.getNameTw());
            }
        }
        return brandList;
    }

    public List<City> listProvinces() {
        return cityMapper.listProvinces();
    }

    public List<City> listCities(String provincePrefix) {
        return cityMapper.listCities(provincePrefix);
    }

    public List<StbOperator> listOperators(String cityCode) {
        return operatorMapper.listOperators(cityCode);
    }
}
