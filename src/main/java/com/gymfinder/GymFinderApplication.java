package com.gymfinder;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GymFinderApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("SMTP_USERNAME", dotenv.get("SMTP_USERNAME"));
		System.setProperty("SMTP_PASSWORD", dotenv.get("SMTP_PASSWORD"));

		SpringApplication.run(GymFinderApplication.class, args);
	}

}
