package com.lendico.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@EnableCircuitBreaker
@EnableCaching
@SpringBootApplication
public class LoanServiceApp {

	public static void main(String[] args) {

		SpringApplication.run(LoanServiceApp.class, args);
	}
}
