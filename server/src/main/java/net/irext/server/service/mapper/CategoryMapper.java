package net.irext.server.service.mapper;

import net.irext.server.service.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Filename:       CategoryMapper.java
 * Revised:        Date: 2019-06-12
 * Revision:       Revision: 1.0
 * <p>
 * Description:    CategoryMapper
 * <p>
 * Revision log:
 * 2019-06-12: created by strawmanbobi
 */
@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM category WHERE status = 1 ORDER BY id LIMIT #{from}, #{count}")
    @ResultMap("BaseResultMap")
    List<Category> listCategories(int from, int count);
}
