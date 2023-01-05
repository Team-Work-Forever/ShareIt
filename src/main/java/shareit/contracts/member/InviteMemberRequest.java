package shareit.contracts.member;

import java.util.Date;

import jakarta.validation.constraints.Email;
import shareit.data.Privilege;
import shareit.helper.Invitation;

public class InviteMemberRequest {

    private Object invitationType;
    private Date expire;

    @Email(message = "Please provide a valid email")
    private String emailTo;
    
    private String emailFrom;

    private Privilege privilege;

    public InviteMemberRequest(Object invitationType, Date expire, String emailTo) {

        if (expire == null)
            expire = new Date();
        else 
            this.expire = expire;

        this.invitationType = invitationType;
        this.emailTo = emailTo;
    }

    public InviteMemberRequest(Object invitationType, Date expire, String emailTo, Privilege privilege) {

        if (expire == null)
            expire = new Date();
        else 
            this.expire = expire;

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

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
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
            false, 
            expire, 
            emailFrom,
            emailTo,
            privilege
        );

    }

}
