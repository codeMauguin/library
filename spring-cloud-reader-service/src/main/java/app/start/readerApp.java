package app.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan("app")
@MapperScan("app.dao")
@EnableFeignClients("app.inject")
@EnableTransactionManagement
public class readerApp {
	public static void main(String[] args) {
		SpringApplication.run(readerApp.class, args);
	}
	
}
