package br.com.murilo.luizalab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LuizalabApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuizalabApplication.class, args);
	}

}
