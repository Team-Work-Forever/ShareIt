package shareit.data;

import java.io.Serializable;

public class SkillLine implements Serializable {

    private Talent talent;
    private Skill skill;
    private int yearOfExp;

    public SkillLine(Skill skill, Talent talent, int yearOfExp) {
        this.skill = skill;
        this.talent = talent;
        this.yearOfExp = yearOfExp;
    }

    public Skill getSkill() {
        return skill;
    }

    public Talent getTalent() {
        return talent;
    }

    public int getYearOfExp() {
        return yearOfExp;
    }

}
