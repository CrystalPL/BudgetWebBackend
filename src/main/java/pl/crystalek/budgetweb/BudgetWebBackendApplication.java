package pl.crystalek.budgetweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pl.crystalek.budgetweb.user.CustomUserDetailsService;

@SpringBootApplication
public class BudgetWebBackendApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(BudgetWebBackendApplication.class, args);
        final CustomUserDetailsService bean = context.getBean(CustomUserDetailsService.class);
    }

}
