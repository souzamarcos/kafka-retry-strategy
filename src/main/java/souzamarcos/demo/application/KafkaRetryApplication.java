package souzamarcos.demo.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("souzamarcos.demo")
@EnableScheduling
public class KafkaRetryApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaRetryApplication.class, args);
	}

}
