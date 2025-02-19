package itmo.dev.owner_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "itmo.dev.owner_microservice.repositories")
@EntityScan(basePackages = "itmo.dev.common.models")
@SpringBootApplication
public class OwnerMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OwnerMicroserviceApplication.class, args);
    }

}
