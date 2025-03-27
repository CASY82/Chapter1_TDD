package io.hhplus.tdd.infrastructure;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockConfig {
	
	@Bean
	public ReentrantLock lock() {
		return new ReentrantLock();
	}

}
