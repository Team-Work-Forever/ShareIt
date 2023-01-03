package shareit.errors;

public class MemberFailedInvitation extends RuntimeException {
    
    public MemberFailedInvitation() {
        super("Invitation not submited!");
    }

    public MemberFailedInvitation(String message) {
        super(message);
    }

}
