package shareit.shareit.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import shareit.TerminalSpringApplication;
import shareit.services.AuthenticationService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TerminalSpringApplication.class)
@DisplayName("Authentication Service")
public class AuthenticationServiceTest {
    
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    public void shouldAuthenticate() {

        try {

           authenticationService.login(null);

        } catch (Exception e) {
            System.out.println("--- Error ---");
        }


    }

}
