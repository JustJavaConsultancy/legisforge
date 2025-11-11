package com.justjava.legisForge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.justjava.legisForge.keycloak")
@SpringBootApplication
public class LegisForgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LegisForgeApplication.class, args);
	}

}
