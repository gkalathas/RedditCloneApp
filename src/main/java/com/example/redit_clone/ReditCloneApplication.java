package com.example.redit_clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ReditCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReditCloneApplication.class, args);
	}

}
