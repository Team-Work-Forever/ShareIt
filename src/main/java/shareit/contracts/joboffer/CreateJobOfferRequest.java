package shareit.contracts.joboffer;

import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.ProfArea;
import shareit.data.State;

public class CreateJobOfferRequest {
    
    private Experience experience;
    private String name;
    private int qtyHours;
    private String desc;
    private ProfArea profArea;
    private State state;
    
    public CreateJobOfferRequest(Experience experience, String name, int qtyHours, String desc, ProfArea profArea) {
        this.experience = experience;
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

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
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

