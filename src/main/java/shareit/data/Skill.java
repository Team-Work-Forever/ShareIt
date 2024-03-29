package shareit.data;

import java.io.Serializable;

import shareit.helper.AutoIncrement;
import shareit.helper.CSVSerializable;

public class Skill implements Serializable, CSVSerializable {

    private int id;
    private String name;
    private String desc;
    private int qtyProf;

    public Skill(String name, String desc) {
        this.name = name;
        this.desc = desc;

        id = AutoIncrement.getIncrementSkill();
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

    @Override
    public String[] serialize() {
        return new String[] {
            this.name
        };
    }

}
