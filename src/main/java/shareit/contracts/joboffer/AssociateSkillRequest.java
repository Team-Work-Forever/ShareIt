package shareit.contracts.joboffer;

import java.util.Map;

import shareit.data.Skill;

public class AssociateSkillRequest {
    
    private int jobOfferId;
    private Map<Skill, Integer> skills;

    public AssociateSkillRequest(int jobOfferId, Map<Skill, Integer> skills) {
        this.jobOfferId = jobOfferId;
        this.skills = skills;
    }

    public int getJobOfferId() {
        return jobOfferId;
    }

    public void setJobOfferId(int jobOfferId) {
        this.jobOfferId = jobOfferId;
    }

    public Map<Skill, Integer> getSkills() {
        return skills;
    }

    public void setSkills(Map<Skill, Integer> skills) {
        this.skills = skills;
    }

}
