package app.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DruidConfig {
	private DruidDataSource druid;
	
	public DruidDataSource getDruid() {
		return druid;
	}
	
	public void setDruid(DruidDataSource druid) {
		this.druid = druid;
	}

// ConfigurationProperties可以直接把应用配置的spring.datasource.druid属性开头的值注入到DruidDataSource中
	
	@Bean
	public DruidDataSource druidDataSource() {
		return druid;
	}
	
}