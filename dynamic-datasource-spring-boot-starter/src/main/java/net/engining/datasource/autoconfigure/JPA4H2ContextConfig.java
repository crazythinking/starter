package net.engining.datasource.autoconfigure;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

/**
 * JPA 针对H2数据库的context配置，通常用于Test
 * @author Administrator
 *
 */
@Configuration
@Profile({"autotest"})
public class JPA4H2ContextConfig {
	
	/**
	 * h2 tcp server, 方便使用工具访问h2
	 * @return Server
	 * @throws SQLException SQLException
	 */
	@Bean(name="h2tcp", initMethod="start", destroyMethod="stop")
	public Server h2tcp() throws SQLException{
		
		return Server.createTcpServer("-tcp","-tcpAllowOthers","-tcpPort","49151");
		
	}
	
}
