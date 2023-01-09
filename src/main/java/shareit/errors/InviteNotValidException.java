package shareit.errors;

public class InviteNotValidException extends RuntimeException {
    
    public InviteNotValidException() {
        super("This Invite is not valid!");
    }
    
    public InviteNotValidException(String message) {
        super(message);
    }

}
