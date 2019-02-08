package net.irext.decode.service.mapper;

import net.irext.decode.service.model.RemoteIndex;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Filename:       RemoteIndexMapper.java
 * Revised:        Date: 2018-12-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    CollectRemote Mybatis Mapper
 * <p>
 * Revision log:
 * 2018-12-08: created by strawmanbobi
 */
@Mapper
public interface RemoteIndexMapper {

    @Select("SELECT * FROM remote_index WHERE id = #{id}")
    @ResultMap("BaseResultMap")
    List<RemoteIndex> getRemoteIndexById(int id);
}
