package shareit.data;

import java.io.Serializable;

public class SkillOfferLine implements Serializable {

    private Skill skill;
    private JobOffer jobOffer;
    private int yearOfExpNec = 0;

    public SkillOfferLine(Skill skill, JobOffer jobOffer, int yearOfExpNec) {
        this.skill = skill;
        this.jobOffer = jobOffer;
        this.yearOfExpNec = yearOfExpNec;
    }
    
    public int getYearOfExpNec() {
        return yearOfExpNec;
    }

    public void setYearOfExpNec(int yearOfExpNec) {
        this.yearOfExpNec = yearOfExpNec;
    }

    public Skill getSkill() {
        return skill;
    }

    public JobOffer getJobOffer() {
        return jobOffer;
    }

}
