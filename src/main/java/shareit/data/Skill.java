package shareit.data;

import java.io.Serializable;

public class Skill implements Serializable {

    private String name;
    private String desc;
    private int qtyProf;

    public Skill(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getQtyProf() {
        return qtyProf;
    }

    public void setQtyProf(int qtyProf) {
        this.qtyProf = qtyProf;
    }

    @Override
    public String toString() {
        return "Skill: \n" + 
            "Name: " + this.name + "\t" + " Description: " + desc + " Qty Profissionals: " + Integer.toString(qtyProf);
    }

    public String toStringJobOffer() {
        return "\n\tName: " + this.name + "\t Description: " + desc;
    }

    public void incrementQtyProf() {
        this.qtyProf++;
    }

    public void reduceQtyProf() {
        this.qtyProf--;
    }

}
