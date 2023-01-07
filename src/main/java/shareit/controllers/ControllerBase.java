package shareit.controllers;

import java.io.IOException;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;

import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.textField;

import shareit.data.auth.IdentityUser;
import shareit.data.auth.Role;
import shareit.services.AuthenticationService;
import shareit.utils.ScreenUtils;

public abstract class ControllerBase {
    
    @Autowired
    private AuthenticationService authenticationService;

    public abstract void display() throws IOException;
    
    public boolean repeatAction(String message) throws IOException {

        String choice;

        clear();
    
        System.out.print(ScreenUtils.PURPLE);
        choice = textField(message + " (default t| f)");
        ScreenUtils.resetColors();

        return choice.isEmpty() ? true : ( choice.equals("t") ? true : false );

    }

    public void authorize() throws AuthenticationException {
        IdentityUser authuser = authenticationService.getAuthenticatedUser();

        if (authuser.getRole().equals(Role.USER) || authuser.getRole().equals(Role.COMPANYINSTITUTION))
            throw new AuthenticationException("Unauthorized!");
    
    }

    public void logout() throws Exception {
        authenticationService.logout();
    }

}
