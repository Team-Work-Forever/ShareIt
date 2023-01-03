package shareit.contracts.talent;

public class TalentRequest {
    
    private String name;

    private float pricePerHour;

    private boolean isPublic;

    public TalentRequest(String name, float pricePerHour, boolean isPublic) {
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

}
