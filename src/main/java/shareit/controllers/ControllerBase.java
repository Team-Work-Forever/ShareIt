package shareit.controllers;

import java.io.IOException;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;

import shareit.data.auth.IdentityUser;
import shareit.data.auth.Role;
import shareit.services.AuthenticationService;

public abstract class ControllerBase {
    
    @Autowired
    private AuthenticationService authenticationService;

    public abstract void display() throws IOException;
    
    public void authorize() throws AuthenticationException {
        IdentityUser authuser = authenticationService.getAuthenticatedUser();

        if (authuser.getRole().equals(Role.USER) || authuser.getRole().equals(Role.COMPANYINSTITUTION))
            throw new AuthenticationException("Unauthorized!");
    
    }

    public void logout() throws Exception {
        authenticationService.logout();
    }

}
