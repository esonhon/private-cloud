package net.irext.server.service.mapper;

import net.irext.server.service.model.StbOperator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Filename:       StbOperatorMapper.java
 * Revised:        Date: 2019-06-21
 * Revision:       Revision: 1.0
 * <p>
 * Description:    StbOperatorMapper
 * <p>
 * Revision log:
 * 2019-06-21: created by strawmanbobi
 */
@Mapper
public interface StbOperatorMapper {
    @Select("SELECT * FROM stb_operator WHERE city_code = #{cityCode}")
    @ResultMap("BaseResultMap")
    List<StbOperator> listOperators(String cityCode);
}
