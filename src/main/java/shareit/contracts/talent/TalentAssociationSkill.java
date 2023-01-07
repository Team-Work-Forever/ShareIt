package shareit.contracts.talent;

import java.util.Map;

import shareit.data.Skill;
import shareit.data.Talent;

public class TalentAssociationSkill {
    
    private Talent talent;

    private Map<Skill,Integer> skills;

    public TalentAssociationSkill(Talent talent, Map<Skill,Integer> skills) {
        this.talent = talent;
        this.skills = skills;
    }

    public Talent getTalent() {
        return talent;
    }

    public void setTalent(Talent talent) {
        this.talent = talent;
    }

    public Map<Skill, Integer> getSkills() {
        return skills;
    }

    public void setSkills(Map<Skill, Integer> skills) {
        this.skills = skills;
    }

}
