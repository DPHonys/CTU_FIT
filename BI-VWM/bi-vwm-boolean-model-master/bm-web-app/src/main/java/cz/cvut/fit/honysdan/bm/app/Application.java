package cz.cvut.fit.honysdan.bm.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"cz.cvut.fit.honysdan.bm.app", "cz.cvut.fit.honysdan.bm.db.utils.service"})
@EntityScan("cz.cvut.fit.honysdan.bm.db.utils.entity")
@EnableJpaRepositories("cz.cvut.fit.honysdan.bm.db.utils.repository")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
