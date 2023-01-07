package shareit.contracts.talent;

import shareit.data.Talent;

public class CreateTalentRequest {
    
    private String name;

    private float pricePerHour;

    private boolean isPublic;

    public CreateTalentRequest(String name, float pricePerHour, boolean isPublic) {
        this.name = name;
        this.pricePerHour = pricePerHour;
        this.isPublic = isPublic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(float pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Talent toTalent() {

        return new Talent(
            name, 
            pricePerHour, 
            isPublic
        );

    }

}
