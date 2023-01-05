package shareit.contracts.talent;

import shareit.data.ProfArea;

public class TalentDisassociateProf {
    
    private String talentName;
    private ProfArea profArea;

    public TalentDisassociateProf(String talentName, ProfArea profArea) {
        this.talentName = talentName;
        this.profArea = profArea;
    }

    public String getTalentName() {
        return talentName;
    }

    public void setTalentName(String talentName) {
        this.talentName = talentName;
    }

    public ProfArea getProfArea() {
        return profArea;
    }

    public void setProfArea(ProfArea profArea) {
        this.profArea = profArea;
    }

}   
