package net.irext.server.service.mapper;

import net.irext.server.service.model.StbOperator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Filename:       OperatorMapper.java
 * Revised:        Date: 2019-06-21
 * Revision:       Revision: 1.0
 * <p>
 * Description:    OperatorMapper
 * <p>
 * Revision log:
 * 2019-06-21: created by strawmanbobi
 */
@Mapper
public interface OperatorMapper {
    @Select("SELECT * FROM stb_operator WHERE city_code = #{cityCode}")
    @ResultMap("BaseResultMap")
    List<StbOperator> listOperators(String cityCode);
}
