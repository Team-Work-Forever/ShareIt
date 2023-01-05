package shareit.errors;

public class InviteNotFoundException extends RuntimeException {
    
    public InviteNotFoundException() {
        super("This invite does not exists!");
    }

}
