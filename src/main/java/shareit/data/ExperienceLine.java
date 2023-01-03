package shareit.data;

import java.io.Serializable;

import shareit.data.auth.IdentityUser;

public class ExperienceLine implements Serializable {

    private IdentityUser client;
    private Experience experience;
    private Privilege privilege;

    public ExperienceLine(IdentityUser client, Experience experience, Privilege privilege) {
        this.client = client;
        this.experience = experience;
        this.privilege = privilege;
    } 
    
    public IdentityUser getClient() {
        return client;
    }
    
    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public Experience getExperience() {
        return experience;
    }

}
