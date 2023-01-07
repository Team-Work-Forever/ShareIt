package shareit.contracts.talent;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import shareit.data.Skill;

public class TalentAssociationSkill {
    
    @NotEmpty
    private String nameTalent;

    private Map<Skill,Integer> skills;

    public TalentAssociationSkill(String nameTalent, Map<Skill,Integer> skills) {
        this.nameTalent = nameTalent;
        this.skills = skills;
    }

    public String getNameTalent() {
        return nameTalent;
    }

    public void setNameTalent(String nameTalent) {
        this.nameTalent = nameTalent;
    }

    public Map<Skill, Integer> getSkills() {
        return skills;
    }

    public void setSkills(Map<Skill, Integer> skills) {
        this.skills = skills;
    }

}
