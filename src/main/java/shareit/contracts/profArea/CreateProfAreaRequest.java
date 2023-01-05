package shareit.contracts.profArea;

import shareit.data.ProfArea;

public class CreateProfAreaRequest {
    
    private String name;
    private String description;
    private int qtyProf;
    
    public CreateProfAreaRequest(String name, String description, int qtyProf) {
        this.name = name;
        this.description = description;
        this.qtyProf = qtyProf;
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

    public int getQtyProf() {
        return qtyProf;
    }

    public void setQtyProf(int qtyProf) {
        this.qtyProf = qtyProf;
    }

    public ProfArea toProfArea() {

        return new ProfArea(
            name, 
            description, 
            qtyProf
        );

    }

}
