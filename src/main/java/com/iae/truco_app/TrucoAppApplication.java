package com.iae.truco_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.iae.truco_app.repository")
@ComponentScan(basePackages = "com.iae.truco_app")
public class TrucoAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(TrucoAppApplication.class, args);
	}
}
