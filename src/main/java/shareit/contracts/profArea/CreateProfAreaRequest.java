package shareit.contracts.profArea;

import jakarta.validation.constraints.NotEmpty;
import shareit.data.ProfArea;

public class CreateProfAreaRequest {
    
    @NotEmpty(message = "Please provide a valid name")
    private String name;

    @NotEmpty(message = "Please provide a valid description")
    private String description;

    public CreateProfAreaRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProfArea toProfArea() {

        return new ProfArea(
            name, 
            description
        );

    }

}
