package com.deliverytech.delivery.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelloController {
	
	@GetMapping("/")
	public String hello(){
		return "Olá mundo do Spring Boot!!";
	}
	
	@GetMapping("/api/status")
	public String status() {
		return "Aplicação funcionando!!";
	}
}