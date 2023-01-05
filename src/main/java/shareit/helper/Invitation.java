package shareit.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import shareit.data.Privilege;

public class Invitation implements Serializable {
    
    private static int increment = 1;

    private int id;
    private Object invitationType;
    private boolean accepted;
    private Date expire;
    private String emailFrom;
    private String emailTo;
    private Privilege privilege;

    public Invitation(Object invitationType, boolean accepted, Date expire, String emailFrom, String emailTo) {

        increment++;
        id = increment;

        this.invitationType = invitationType;
        this.accepted = accepted;
        this.expire = expire;
        this.emailFrom = emailFrom;
        this.emailTo = emailTo;
    }

    public Invitation(Object invitationType, boolean accepted, Date expire, String emailFrom, String emailTo, Privilege privilege) {
        
        increment++;
        id = increment;

        this.invitationType = invitationType;
        this.accepted = accepted;
        this.expire = expire;
        this.emailFrom = emailFrom;
        this.emailTo = emailTo;
        this.privilege = privilege;
    }

    public Object getObject() {
        return invitationType;
    }

    public void setObject(Object invitationType) {
        this.invitationType = invitationType;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
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

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Invitation: " + 
            "\nID: " + id + 
            "\nStatus: " + accepted +
            "\nEmail From: " + emailFrom +
            "\nEmail To: " + emailTo +
            "\nExpire Time: "+ expire;
    }


}
