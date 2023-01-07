package shareit.data;

import java.io.Serializable;

public class ProfArea implements Serializable {

    private String name;
    private String description;
    private int qtyProf;

    public ProfArea(String name, String description) {
        this.name = name;
        this.description = description;
        this.qtyProf = 0;
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
        return ToStringLow() + "\tQty Professionals: " + Integer.toString(qtyProf) + "\t\t -- Professional Area";
    }

    public String ToStringLow() {
        return "\tName: " + this.name + "\tDescription: " + description;
    }

}
