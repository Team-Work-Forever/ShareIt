package shareit.contracts.member;

import jakarta.validation.constraints.Email;
import shareit.data.Experience;

public class InviteMemberRequest {
 
    @Email
    private String email;

    private Experience experience;

    public InviteMemberRequest(@Email String email, Experience experience) {
        this.email = email;
        this.experience = experience;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Experience getexperience() {
        return experience;
    }

    public void setexperience(Experience experience) {
        this.experience = experience;
    }


}
