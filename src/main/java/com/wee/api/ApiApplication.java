package com.wee.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@SpringBootApplication
@ComponentScan(basePackages= {"com.wee"})
@EnableJpaRepositories(basePackages= {"com.wee"})
@EntityScan(basePackages= {"com.wee"})
@MapperScan(basePackages= {"com.wee.mybatis.mapper"})
@Slf4j
@EnableAsync
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean("processExecutor")
	public Executor actionExecutor(){
		log.info("Preparing thread executor for actions");
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(50);
		executor.setMaxPoolSize(50);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("actionAsyncThread-");
		executor.setKeepAliveSeconds(20);
		executor.initialize();
		log.info("Thread pool executor for actions prepared");
		return executor;
	}

}
