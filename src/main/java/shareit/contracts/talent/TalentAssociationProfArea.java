package shareit.contracts.talent;

import java.util.Map;

import shareit.data.ProfArea;

public class TalentAssociationProfArea {
    
    private String talentName;
    private Map<ProfArea, Integer> profAreas;

    public TalentAssociationProfArea(String talentName, Map<ProfArea, Integer> profAreas) {
        this.talentName = talentName;
        this.profAreas = profAreas;
    }

    public String getTalentName() {
        return talentName;
    }

    public void setTalentName(String talentName) {
        this.talentName = talentName;
    }

    public Map<ProfArea, Integer> getProfAreas() {
        return profAreas;
    }

    public void setProfAreas(Map<ProfArea, Integer> profAreas) {
        this.profAreas = profAreas;
    }

}
