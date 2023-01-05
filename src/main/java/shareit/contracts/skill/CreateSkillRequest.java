package shareit.contracts.skill;

import jakarta.validation.constraints.NotEmpty;
import shareit.data.Skill;

public class CreateSkillRequest {
    
    @NotEmpty(message = "Please provide a valid name")
    private String name;

    @NotEmpty(message = "Please provide a valid description")
    private String desc;

    public CreateSkillRequest(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Skill toSkill() {

        return new Skill(
            name, 
            desc
        );

    }

}
