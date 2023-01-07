package shareit.data;

import java.io.Serializable;

public class ProfArea implements Serializable {

    private static int increment = 1;

    private int id;
    private String name;
    private String description;
    private int qtyProf;

    public ProfArea(String name, String description) {

        this.id = increment;

        this.name = name;
        this.description = description;
        this.qtyProf = 0;

        increment++;
    }

    public int getProfAreaId() {
        return id;
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

    public void incrementQtyProf() {
        this.qtyProf++;
    }

    public void reduceQtyProf() {
        this.qtyProf--;
    }

    @Override
    public String toString() {
        return ToStringLow() + "\tQty Professionals: " + Integer.toString(qtyProf);
    }

    public String ToStringLow() {
        return "Prof. Area (" + this.getProfAreaId() + "): " + "\tName: " + this.name + "\tDescription: " + description;
    }

}
