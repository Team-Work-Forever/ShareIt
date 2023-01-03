package shareit.shareit.services;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.crypto.password.PasswordEncoder;

import shareit.repository.GlobalRepository;
import shareit.services.AuthenticationService;

@DisplayName("Authentication Service")
public class AuthenticationServiceTest {
    
    @InjectMocks
    private AuthenticationService authenticationService;

    @InjectMocks
    private GlobalRepository globalRepository;

    @InjectMocks
    private PasswordEncoder passwordEncoder;

    @Test
    public void shouldAuthenticate() {

        try {

            boolean result = authenticationService.login(null);

            assertTrue(result);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

}
