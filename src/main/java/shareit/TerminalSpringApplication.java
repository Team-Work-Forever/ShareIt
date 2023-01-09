package shareit;

import java.io.IOException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shareit.contracts.auth.RegisterRequest;
import shareit.data.ProfArea;
import shareit.data.Skill;
import shareit.data.auth.Role;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.repository.GlobalRepository;
import shareit.services.AuthenticationService;
import shareit.utils.DatePattern;
import shareit.utils.ScreenUtils;


@Configuration
@SpringBootApplication
public class TerminalSpringApplication implements CommandLineRunner {

    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    @Bean
    public NavigationHelper navigationHelper() {
        return new NavigationHelper();
    }

    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    @Bean
    public GlobalRepository globalRepository() throws Exception {
        return new GlobalRepository();
    }

    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService();
    }

    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    @Bean
    public RouteManager routeManager() {
        return new RouteManager();
    }

    @Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws IOException {

        try {
            
            if (authenticationService().isBeforeAuthenticated())
            authenticationService().authenticateWithToken();

            seeder();

            navigationHelper().setFirstEntry(routeManager().authRoute());

        } catch (Exception e) {
            ScreenUtils.printError(e.getMessage());
        }

    }

    private void seeder() throws IOException {

        String adminEmail = "admin@gmail.com";
        String userEmail = "diogo@gmail.com";

        try {

            if (globalRepository().containsEmail(adminEmail) || globalRepository().containsEmail(userEmail)) {
                return;
            }

            authenticationService().signIn(new RegisterRequest(
                adminEmail, 
                "password", 
                "admin", 
                adminEmail, 
                DatePattern.insertDate("29-07-2003"), 
                adminEmail, 
                adminEmail, 
                adminEmail, 
                "Portugal", 
                true,
                Role.ADMIN
            ));

            authenticationService().signIn(new RegisterRequest(
                userEmail, 
                "password", 
                "C.A.V.A.S", 
                adminEmail, 
                DatePattern.insertDate("29-07-2002"), 
                adminEmail, 
                adminEmail, 
                adminEmail, 
                "Espanha", 
                true, 
                Role.USER
            ));

            authenticationService().signIn(new RegisterRequest(
                "sergio@gmail.com", 
                "password", 
                "Serjão", 
                adminEmail, 
                DatePattern.insertDate("29-07-2001"), 
                adminEmail, 
                adminEmail, 
                adminEmail, 
                "Portugal", 
                true, 
                Role.USERMANAGER
            ));
            
            authenticationService().signIn(new RegisterRequest(
                "david@gmail.com", 
                "password", 
                "ReiNaldo", 
                adminEmail, 
                DatePattern.insertDate("29-07-2000"), 
                adminEmail, 
                adminEmail, 
                adminEmail, 
                "Republica das Bananas", 
                true,
                Role.USER
            ));

            // Generate Skills

            globalRepository().createSkill(new Skill(
                "skill", 
                "description"
            ));
            
            // Generate Professional Areas

            globalRepository().createProfArea(new ProfArea(
                "profarea", 
                "description"
            ));

            ScreenUtils.clear();

            System.out.println("Generating an default admin...");
            System.out.println("Admin Generated!");
            System.out.println("Email: " + adminEmail + "Password: " + "password");

            ScreenUtils.waitForKeyEnter();

        } catch (Exception e) {
            ScreenUtils.printError("An Error as occorred");
            Runtime.getRuntime().exit(0);
        }

    }

}
