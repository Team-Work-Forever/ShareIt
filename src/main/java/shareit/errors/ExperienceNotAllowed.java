package shareit.errors;

public class ExperienceNotAllowed extends RuntimeException {
    
    public ExperienceNotAllowed() {
        super("You are not allowed to perform this action!");
    }
    
}
