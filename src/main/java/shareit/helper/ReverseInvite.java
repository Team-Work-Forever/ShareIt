package shareit.helper;

import java.io.Serializable;

import shareit.data.Privilege;
import shareit.data.auth.IdentityUser;

public class ReverseInvite implements Serializable {
    
    private Object application;
    private IdentityUser cadidateUser;
    private Privilege privilege;

    public ReverseInvite(Object application, IdentityUser cadidateUser) {
        this.application = application;
        this.cadidateUser = cadidateUser;
    }

    public ReverseInvite(Object application, IdentityUser cadidateUser, Privilege privilege) {
        this.application = application;
        this.cadidateUser = cadidateUser;
        this.privilege = privilege;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public Object getApplication() {
        return application;
    }

    public void setApplication(Object application) {
        this.application = application;
    }

    public IdentityUser getCadidateUser() {
        return cadidateUser;
    }

    public void setCadidateUser(IdentityUser cadidateUser) {
        this.cadidateUser = cadidateUser;
    }

}
