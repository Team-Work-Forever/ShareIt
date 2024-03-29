package shareit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.auth.AuthenticationRequest;
import shareit.contracts.auth.RegisterRequest;
import shareit.data.auth.Role;
import shareit.services.AuthenticationService;
import shareit.utils.DatePattern;
import shareit.errors.auth.AuthenticationException;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;

import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.menu;

import java.io.IOException;

@Controller
public class LoginController extends ControllerBase {

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public void display() throws IOException {
        
        int index = 0;

        do {
            
            do {
            
                clear();

                index = menu("***************** Menu *****************", new String[] {
                    "Sign In",
                    "Register"
                }, "anonymous");
    
            } while (index <= 0 && index >= 2);
    
            switch (index) {
                case 1:
                    authenticate();
                    break;
                case 2:
                    signIn();
                    break;
            }
    
        } while (index != 0);

        System.exit(0);

    }

    // Case 1
    private void authenticate() throws IOException {

        clear();

        System.out.println("***************** Login *****************\n");
        
        String email = textField("Email");
        String password = textField("Password");

        try {

            authenticationService.login(new AuthenticationRequest(
                email, 
                password
            ));

            navigationHelper.navigateTo(routeManager.authRoute());
            
        } catch (AuthenticationException e) {
            printError(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // Case 2
    private void signIn() throws IOException {

        String email, password, confirm_password, 
        name, lastName, bornDate, street, postCode, 
        locality, country, isPublic;
        
        clear();

        try {

            email = textField("Email");

            do {

                password = textField("Password");
                confirm_password = textField("Confirm Password");

            } while (!password.equals(confirm_password));

            name = textField("First Name");
            lastName = textField("Last Name");


            bornDate = textField("Born Date (dd-MM-yyyy)");

            if (bornDate.isEmpty()) {
                throw new IllegalArgumentException("Please provide an valid date with the format dd-MM-yyyy");
            }

            street = textField("Street");
            
            do {

                postCode = textField("Post-Code");

            } while (postCode.length() != 8);

            locality = textField("Locality");
            country = textField("Country");
            isPublic = textField("Visibility Perfil (t/f)");

            authenticationService.signIn(new RegisterRequest(
                email, 
                password, 
                name, 
                lastName, 
                DatePattern.insertDate(bornDate), 
                street, 
                postCode, 
                locality, 
                country, 
                isPublic == "t" ? true : false,
                Role.USER
            ));

        } catch (AuthenticationException e) {
            printError(e.getMessage());
        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

}
