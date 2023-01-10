package shareit.helper;

import org.springframework.beans.factory.annotation.Autowired;

import shareit.data.auth.Role;
import shareit.controllers.AdminController;
import shareit.controllers.ControllerBase;
import shareit.controllers.DashBoardController;
import shareit.controllers.LoginController;
import shareit.services.AuthenticationService;

public class RouteManager {
    
    @Autowired
    private AuthenticationService authenticationService;

    private Object args;

    /**
     * Controllers Authentication Routes
     * @return Controller
     */
    public Class<? extends ControllerBase> authRoute() {
        
        if (!authenticationService.isAuthenticated())
            return LoginController.class;

        switch (authenticationService.getAuthenticatedUser().getRole()) {
            case Role.USER:
            case Role.COMPANYINSTITUTION:
                return DashBoardController.class;
            case Role.USERMANAGER:
            case Role.ADMIN:
                return AdminController.class;
            default:
                return LoginController.class;
        }

    }

    /**
     * Pass through arguments between routing
     * @param controller Given Controller
     * @param args Given Arguments of any type
     * @return Controller
     */
    public Class<? extends ControllerBase> argumentRoute(Class<? extends ControllerBase> controller, Object args) {
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