package pl.crystalek.budgetweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pl.crystalek.budgetweb.household.role.permission.RolePermissionService;

@SpringBootApplication
public class BudgetWebBackendApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(BudgetWebBackendApplication.class, args);
        final RolePermissionService bean = context.getBean(RolePermissionService.class);
    }

}
