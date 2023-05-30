package com.jiawa.wiki2.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.jiawa")
@MapperScan("com.jiawa.wiki2.mapper")
@SpringBootApplication
public class Wiki2Application {
	public static void main(String[] args) {
		SpringApplication.run(Wiki2Application.class, args);
	}
}
