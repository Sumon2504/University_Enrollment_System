package com.cognizant.uams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class UamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UamsApplication.class, args);
	}

}
