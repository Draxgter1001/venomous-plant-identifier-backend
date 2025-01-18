package com.example.taf.VPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VpiApplication {

	public static void main(String[] args) {

		System.getenv();

		SpringApplication.run(VpiApplication.class, args);
	}

}
