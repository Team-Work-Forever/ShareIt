package shareit.contracts.talent;

import shareit.data.ProfArea;
import shareit.data.Talent;

public class TalentDisassociateProf {
    
    private Talent talent;
    private ProfArea profArea;

    public TalentDisassociateProf(Talent talent, ProfArea profArea) {
        this.talent = talent;
        this.profArea = profArea;
    }

    public Talent getTalent() {
        return talent;
    }

    public void setTalent(Talent talent) {
        this.talent = talent;
    }

    public ProfArea getProfArea() {
        return profArea;
    }

    public void setProfArea(ProfArea profArea) {
        this.profArea = profArea;
    }

}   
