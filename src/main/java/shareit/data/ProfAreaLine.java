package shareit.data;

import java.io.Serializable;

public class ProfAreaLine implements Serializable {

    private ProfArea profArea;
    private Talent talent;
    private int yearOfExp;

    public ProfAreaLine(ProfArea profArea, Talent talent, int yearOfExp) {
        this.profArea = profArea;
        this.talent = talent;
        this.yearOfExp = yearOfExp;
    }
    
    public int getYearOfExp() {
        return yearOfExp;
    }

    public void setYearOfExp(int yearOfExp) {
        this.yearOfExp = yearOfExp;
    }

    public ProfArea getProfArea() {
        return profArea;
    }

    public Talent getTalent() {
        return talent;
    }

}
