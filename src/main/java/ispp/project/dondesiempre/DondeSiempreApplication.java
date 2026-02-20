package ispp.project.dondesiempre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "DondeSiempre API", version = "1.0", description = "API documentation for DondeSiempre"))
@SpringBootApplication
public class DondeSiempreApplication {

	public static void main(String[] args) {
		SpringApplication.run(DondeSiempreApplication.class, args);
	}

}
