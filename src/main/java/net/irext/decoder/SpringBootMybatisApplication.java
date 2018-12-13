package net.irext.decoder;

import net.irext.decoder.model.CollectRemote;
import org.apache.ibatis.type.MappedTypes;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Filename:       SpringBootMybatisApplication
 * Revised:        Date: 2018-12-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext Code Collector Application
 * <p>
 * Revision log:
 * 2018-12-08: created by strawmanbobi
 */
@MappedTypes(CollectRemote.class)
@MapperScan("net.irext.decoder.mybatis.springbootmybatis.mapper")
@SpringBootApplication
public class SpringBootMybatisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMybatisApplication.class, args);
	}
}