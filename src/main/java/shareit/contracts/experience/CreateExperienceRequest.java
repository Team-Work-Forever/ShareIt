package shareit.contracts.experience;

import java.util.Date;

import shareit.data.Experience;

public class CreateExperienceRequest {
    
    private String talentName;
    private String title;
    private String name;
    private int qtyWorkers;
    private int qtyManegers;
    private String desc;
    private Date startDate;
    private Date finalDate;
    
    public CreateExperienceRequest(String talentName, String title, String name,
        String desc, Date startDate, Date finalDate) {
        this.talentName = talentName;
        this.title = title;
        this.desc = desc;
        this.startDate = startDate;
        this.finalDate = finalDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQtyWorkers() {
        return qtyWorkers;
    }

    public void setQtyWorkers(int qtyWorkers) {
        this.qtyWorkers = qtyWorkers;
    }

    public int getQtyManegers() {
        return qtyManegers;
    }

    public void setQtyManegers(int qtyManegers) {
        this.qtyManegers = qtyManegers;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    public String getTalentName() {
        return talentName;
    }

    public void setTalentName(String talentName) {
        this.talentName = talentName;
    }

    public Experience toExperience() {

        if (finalDate == null) {

            return new Experience(
                title, 
                name, 
                startDate, 
                desc
            );

        }

        return new Experience(
            title, 
            name, 
            startDate, 
            finalDate, 
            desc
        );

    }

}