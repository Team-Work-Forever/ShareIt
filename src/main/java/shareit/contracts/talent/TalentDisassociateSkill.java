package shareit.contracts.talent;

import shareit.data.Skill;

public class TalentDisassociateSkill {
    
    private String talentName;
    private Skill skill;

    public TalentDisassociateSkill(String talentName, Skill skill) {
        this.talentName = talentName;
        this.skill = skill;
    }

    public String getTalentName() {
        return talentName;
    }

    public void setTalentName(String talentName) {
        this.talentName = talentName;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

}   
