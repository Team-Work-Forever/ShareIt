package shareit.contracts.talent;

import java.util.Map;

import shareit.data.ProfArea;
import shareit.data.Talent;

public class TalentAssociationProfArea {
    
    private Talent talent;
    private Map<ProfArea, Integer> profAreas;

    public TalentAssociationProfArea(Talent talent, Map<ProfArea, Integer> profAreas) {
        this.talent = talent;
        this.profAreas = profAreas;
    }

    public Talent getTalent() {
        return talent;
    }

    public void setTalent(Talent talent) {
        this.talent = talent;
    }

    public Map<ProfArea, Integer> getProfAreas() {
        return profAreas;
    }

    public void setProfAreas(Map<ProfArea, Integer> profAreas) {
        this.profAreas = profAreas;
    }

}
