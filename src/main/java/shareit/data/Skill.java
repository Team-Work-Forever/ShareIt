package shareit.data;

import java.io.Serializable;

public class Skill implements Serializable {

    private static int increment = 1;

    private int id;
    private String name;
    private String desc;
    private int qtyProf;

    public Skill(String name, String desc) {

        this.id = increment;

        this.name = name;
        this.desc = desc;

        increment++;
    }

    public int getSkillId() {
        return id;
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
        return "Skill (" + this.getSkillId() + "): " + "\tName: " + this.name + "\tDescription: " + desc + "\tQty Professionals: " + Integer.toString(qtyProf);
    }

    public String toStringJobOffer() {
        return "Skill (" + this.getSkillId() + "): " + "\tName: " + this.name + "\tDescription: " + desc;
    }

    public void incrementQtyProf() {
        this.qtyProf++;
    }

    public void reduceQtyProf() {
        this.qtyProf--;
    }

}
