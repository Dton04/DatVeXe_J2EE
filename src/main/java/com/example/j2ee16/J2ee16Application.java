package com.example.j2ee16;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class J2ee16Application {

	public static void main(String[] args) {
		SpringApplication.run(J2ee16Application.class, args);
	}

}
