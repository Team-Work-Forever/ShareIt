package shareit.contracts.member;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import shareit.data.Privilege;
import shareit.helper.Invitation;

public class InviteMemberRequest {

    private Object invitationType;

    private LocalDate expiredDate;

    @Email(message = "Please provide a valid email")
    private String emailTo;
    
    private String emailFrom;

    private Privilege privilege;

    public InviteMemberRequest(Object invitationType, String emailTo) {

        expiredDate = LocalDate.now().plusDays(30); // More 30 Days

        this.invitationType = invitationType;
        this.emailTo = emailTo;
    }

    public InviteMemberRequest(Object invitationType, String emailTo, Privilege privilege) {

        expiredDate = LocalDate.now().plusDays(30); // More 30 Days

        this.invitationType = invitationType;
        this.emailTo = emailTo;
        this.privilege = privilege;
    }

    public Object getObject() {
        return invitationType;
    }

    public void setObject(Object invitationType) {
        this.invitationType = invitationType;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public Invitation toInvitation() {

        return new Invitation(
            invitationType, 
            expiredDate, 
            emailFrom, 
            emailTo, 
            privilege
        );
    }

}
