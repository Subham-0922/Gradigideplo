package com.gradigi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.gradigi.controllers.UserController;
import com.gradigi.sorting.LoggingFilter;

@SpringBootApplication
public class GradigiApplication {
	
	@Autowired
	public UserController uc;
	public static void main(String[] args) {
		SpringApplication.run(GradigiApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<LoggingFilter> loggingFilterRegistration() {

		FilterRegistrationBean<LoggingFilter> registration = new FilterRegistrationBean<>();

		registration.setFilter(new LoggingFilter());

		registration.addUrlPatterns("/*");

		return registration;
	}
}