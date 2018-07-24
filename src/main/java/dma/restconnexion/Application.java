package dma.restconnexion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application{
	@Bean
	protected InfoConnexionDAO infoConnexionDAO() {
		return new InfoConnexionDAO();
	}
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
