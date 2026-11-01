package com.example.chatConnectSpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChatConnectSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatConnectSpringApplication.class, args);
	}

}
