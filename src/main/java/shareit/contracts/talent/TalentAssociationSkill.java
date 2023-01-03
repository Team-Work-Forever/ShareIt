package shareit.contracts.talent;

import java.util.Collection;

import jakarta.validation.constraints.NotEmpty;
import shareit.data.Skill;

public class TalentAssociationSkill {
    
    @NotEmpty
    private String nameTalent;

    private Collection<Skill> skills;

    private int yearOfExp;

    public TalentAssociationSkill(String nameTalent, Collection<Skill> skills, int yearOfExp) {
        this.nameTalent = nameTalent;
        this.skills = skills;
        this.yearOfExp = yearOfExp;
    }

    public String getNameTalent() {
        return nameTalent;
    }

    public void setNameTalent(String nameTalent) {
        this.nameTalent = nameTalent;
    }

    public Collection<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Collection<Skill> skills) {
        this.skills = skills;
    }
        
    public int getYearOfExp() {
        return yearOfExp;
    }

    public void setYearOfExp(int yearOfExp) {
        this.yearOfExp = yearOfExp;
    }

}
