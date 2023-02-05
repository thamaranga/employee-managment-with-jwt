package com.hasitha.employeemanagment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/*
*exclude = SecurityAutoConfiguration.class means don't  auto configure
* spring  security. We can achieve the same by adding below property
* into application.properties file also as below.
* spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
* */
//@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@SpringBootApplication

public class EmployeeManagmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagmentApplication.class, args);
	}

}
