package shareit.helper;

import org.springframework.beans.factory.annotation.Autowired;

import shareit.data.auth.Role;
import shareit.controllers.DashBoardController;
import shareit.controllers.LoginController;
import shareit.services.AuthenticationService;

public class RouteManager {
    
    @Autowired
    private AuthenticationService authenticationService;

    private Object args;

    public Class<?> authRoute() {
        
        if (!authenticationService.isAuthenticated())
            return LoginController.class;

        switch (authenticationService.getAuthenticatedUser().getRole()) {
            case Role.USER:
            case Role.COMPANYINSTITUTION:
                return DashBoardController.class;
            case Role.USERMANAGER:
            case Role.ADMIN:
                return DashBoardController.class;
            default:
                return LoginController.class;
        }

    }

    public Class<?> argumentRoute(Class<?> controller, Object args) {
        this.args = args;
        return controller;
    }

    public Object getArgs() {
        return args;
    }

    public void setArgs(Object args) {
        this.args = args;
    }

}