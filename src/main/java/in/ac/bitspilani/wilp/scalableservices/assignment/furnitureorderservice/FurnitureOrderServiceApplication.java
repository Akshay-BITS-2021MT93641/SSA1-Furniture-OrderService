package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FurnitureOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FurnitureOrderServiceApplication.class, args);
	}

}
