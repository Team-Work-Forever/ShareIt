package shareit.contracts.joboffer;

import shareit.data.JobOffer;
import shareit.data.ProfArea;
import shareit.data.State;

public class CreateJobOfferRequest {
    
    private String talentName;
    private String experienceTitle;
    private String name;
    private int qtyHours;
    private String desc;
    private ProfArea profArea;
    private State state;
    
    public CreateJobOfferRequest(String talentName, String experienceTitle, String name, int qtyHours, String desc, ProfArea profArea) {
        this.talentName = talentName;
        this.experienceTitle = experienceTitle;
        this.name = name;
        this.qtyHours = qtyHours;
        this.desc = desc;
        this.profArea = profArea;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQtyHours() {
        return qtyHours;
    }

    public void setQtyHours(int qtyHours) {
        this.qtyHours = qtyHours;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ProfArea getProfArea() {
        return profArea;
    }

    public void setProfArea(ProfArea profArea) {
        this.profArea = profArea;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getExperienceTile() {
        return experienceTitle;
    }

    public void setexperienceTitle(String experienceTitle) {
        this.experienceTitle = experienceTitle;
    }

    public String getTalentName() {
        return talentName;
    }

    public void setTalentName(String talentName) {
        this.talentName = talentName;
    }

    public JobOffer toJobOffer() {
        
        return new JobOffer(
            name, 
            qtyHours, 
            desc, 
            profArea
        );

    }

}

