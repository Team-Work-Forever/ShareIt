package shareit.helper;

import java.io.Serializable;
import java.time.LocalDate;

import shareit.data.Privilege;
import shareit.utils.DatePattern;

public class Invitation implements Serializable {
    
    private int id;
    private Object invitationType;
    private LocalDate expire;
    private String emailFrom;
    private String emailTo;
    private Privilege privilege;

    public Invitation(Object invitationType, LocalDate expire, String emailFrom, String emailTo) {

        this.invitationType = invitationType;
        this.expire = expire;
        this.emailFrom = emailFrom;
        this.emailTo = emailTo;

        id = AutoIncrement.getIncrementInvitation();
    }

    public Invitation(Object invitationType, LocalDate expire, String emailFrom, String emailTo, Privilege privilege) {
        this.invitationType = invitationType;
        this.expire = expire;
        this.emailFrom = emailFrom;
        this.emailTo = emailTo;
        this.privilege = privilege;
        id = AutoIncrement.getIncrementInvitation();
    }

    public Object getObject() {
        return invitationType;
    }

    public void setObject(Object invitationType) {
        this.invitationType = invitationType;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public LocalDate getExpire() {
        return expire;
    }

    public void setExpire(LocalDate expire) {
        this.expire = expire;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public Object getInvitationType() {
        return invitationType;
    }

    public void setInvitationType(Object invitationType) {
        this.invitationType = invitationType;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Invitation: " + 
            "\nID: " + id + 
            "\nEmail From: " + emailFrom +
            "\nEmail To: " + emailTo +
            "\nExpire Time: "+ DatePattern.convertDate(expire);
    }


}
