package shareit.contracts.skill;

import jakarta.validation.constraints.NotEmpty;

public class CreateSkillRequest {
    
    @NotEmpty
    private String name;

    @NotEmpty
    private String desc;

    private int qtyProf;

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

    public int getQtyProf() {
        return qtyProf;
    }

    public void setQtyProf(int qtyProf) {
        this.qtyProf = qtyProf;
    }

}
