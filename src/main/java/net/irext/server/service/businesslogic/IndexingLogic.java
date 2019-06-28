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

    private CategoryMapper categoryMapper;

    private BrandMapper brandMapper;

    private CityMapper cityMapper;

    private StbOperatorMapper stbOperatorMapper;

    private RemoteIndexMapper remoteIndexMapper;

    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Autowired
    public void setBrandMapper(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    @Autowired
    public void setCityMapper(CityMapper cityMapper) {
        this.cityMapper = cityMapper;
    }

    @Autowired
    public void setStbOperatorMapper(StbOperatorMapper stbOperatorMapper) {
        this.stbOperatorMapper = stbOperatorMapper;
    }

    @Autowired
    public void setRemoteIndexMapper(RemoteIndexMapper remoteIndexMapper) {
        this.remoteIndexMapper = remoteIndexMapper;
    }

    private static final String IR_BIN_FILE_PREFIX = "irda_";
    private static final String IR_BIN_FILE_SUFFIX = ".bin";

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
        String provincePrefixText = provincePrefix + "__00";
        return cityMapper.listCities(provincePrefixText);
    }

    public List<StbOperator> listOperators(String cityCode) {
        return stbOperatorMapper.listOperators(cityCode);
    }

    public List<RemoteIndex> listRemoteIndexes(int categoryId, int brandId, String cityCode, int from, int count) {
        List<RemoteIndex> remoteIndexList;
        if (categoryId == Constants.CategoryID.STB.getValue()) {
            remoteIndexList = remoteIndexMapper.listRemoteIndexByCity(cityCode, from, count);
        } else {
            remoteIndexList = remoteIndexMapper.listRemoteIndexByBrand(categoryId, brandId, from, count);
        }
        return remoteIndexList;
    }
}
