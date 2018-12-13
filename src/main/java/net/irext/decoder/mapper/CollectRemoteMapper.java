package net.irext.decoder.mapper;

import net.irext.decoder.model.CollectRemote;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;

/**
 * Filename:       CollectRemoteMapper.java
 * Revised:        Date: 2018-12-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    CollectRemote Mybatis Mapper
 * <p>
 * Revision log:
 * 2018-12-08: created by strawmanbobi
 */
@Mapper
public interface CollectRemoteMapper {

    @Select("select * from collect_remote")
    List<CollectRemote> listCollectRemotes();

    @Insert("INSERT INTO collect_remote(category_id, category_name, brand_id, brand_name, city_code, city_name, " +
            "operator_id, operator_name, remote_map, protocol, remote) " +
            "values(#{categoryId}, #{categoryName}, #{brandId}, #{brandName}, #{cityCode}, #{cityName}, " +
            "#{operatorId}, #{operatorName}, #{remoteMap}, #{protocol}, #{remote})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id",
            before = false, resultType = Integer.class)
    void createCollectRemote(CollectRemote collectRemote);
}
