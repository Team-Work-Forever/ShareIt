package shareit.contracts.talent;

import shareit.data.Skill;
import shareit.data.Talent;

public class TalentDisassociateSkill {
    
    private Talent talent;
    private Skill skill;

    public TalentDisassociateSkill(Talent talent, Skill skill) {
        this.talent = talent;
        this.skill = skill;
    }

    public Talent getTalent() {
        return talent;
    }

    public void setTalent(Talent talent) {
        this.talent = talent;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

}   
