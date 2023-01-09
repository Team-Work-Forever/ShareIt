package shareit.contracts.experience;

import java.time.LocalDate;

import jakarta.validation.constraints.NotEmpty;
import shareit.data.Experience;
import shareit.data.Talent;

public class CreateExperienceRequest {
    
    private Talent talent;

    @NotEmpty(message = "Please provide an title")
    private String title;

    @NotEmpty(message = "Please provide an name")
    private String name;

    private int qtyWorkers;

    private int qtyManegers;
    
    @NotEmpty(message = "Please provide an description")
    private String desc;

    private LocalDate startDate;
    
    private LocalDate finalDate;
    
    public CreateExperienceRequest(Talent talent, String title, String name,
        String desc, LocalDate startDate, LocalDate finalDate) {
        this.talent = talent;
        this.title = title;
        this.name = name;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(LocalDate finalDate) {
        this.finalDate = finalDate;
    }

    public Talent getTalent() {
        return talent;
    }

    public void setTalent(Talent talent) {
        this.talent = talent;
    }

    public static CreateExperienceRequest toCreateExperienceRequest(Experience experience) {

        return new CreateExperienceRequest(
            null, 
            experience.getTitle(), 
            experience.getName(), 
            experience.getDesc(), 
            experience.getStartDate(), 
            experience.getFinalDate()
        );

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
