package com.astral.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.astral"})
@MapperScan(basePackages = {"com.astral.**.mapper"})
public class AstralWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(AstralWebApplication.class, args);
	}

}
