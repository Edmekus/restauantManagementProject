package com.cafe_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement

public class CafeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CafeBackendApplication.class, args);
	}

}
