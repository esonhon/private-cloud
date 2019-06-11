package net.irext.server.service;

import net.irext.server.service.model.RemoteIndex;
import org.apache.ibatis.type.MappedTypes;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Filename:       SpringBootMybatisApplication
 * Revised:        Date: 2019-06-10
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext private server application
 * <p>
 * Revision log:
 * 2019-06-10: created by strawmanbobi
 */

@MappedTypes(RemoteIndex.class)
@MapperScan("net.irext.server.service.mapper")
@SpringBootApplication
public class IRPrivateServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IRPrivateServerApplication.class, args);
    }

}
