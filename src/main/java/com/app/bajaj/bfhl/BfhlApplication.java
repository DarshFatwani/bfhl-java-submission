package com.app.bajaj.bfhl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class BfhlApplication {

	public static void main(String[] args) {
		SpringApplication.run(BfhlApplication.class, args);
	}

}
