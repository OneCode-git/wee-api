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

import java.lang.management.ManagementFactory;
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
		log.info("Thread count : {}", ManagementFactory.getThreadMXBean().getThreadCount());
		log.info("Thread peak count : {}", ManagementFactory.getThreadMXBean().getPeakThreadCount());
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	public ThreadPoolTaskExecutor actionExecutor(){
		log.info("Preparing thread executor for actions");
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(100);
//		executor.setAllowCoreThreadTimeOut(true);
//		executor.setKeepAliveSeconds(20);
		executor.setThreadNamePrefix("actionAsyncThread-");
		executor.initialize();
		log.info("Thread pool executor for actions prepared");
		return executor;
	}

}
