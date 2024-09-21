package pl.crystalek.budgetweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;

@SpringBootApplication
public class BudgetWebBackendApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(BudgetWebBackendApplication.class, args);
        System.out.println(System.getProperty("user.dir"));
        System.out.println(new File(System.getProperty("user.dir") + "/avatars").mkdir());
    }
}
