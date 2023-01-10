package shareit.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import shareit.errors.ExperienceException;
import shareit.errors.ProfAreaException;
import shareit.errors.SkillException;
import shareit.helper.AutoIncrement;

public class Talent implements Serializable {

    private int talentId;
    private String name;
    private float pricePerHour;
    private boolean isPublic = true;
    private final Collection<Experience> experiences = new ArrayList<>();
    private final Collection<SkillLine> skills = new ArrayList<>();
    private final Collection<ProfAreaLine> profAreas = new ArrayList<>();

    public Talent(String name, float pricePerHour) {

        this.name = name;
        this.pricePerHour = pricePerHour;

        talentId = AutoIncrement.getIncrementTalents();
    }

    public Talent(String name, float pricePerHour, boolean isPublic) {
        
        this.name = name;
        this.pricePerHour = pricePerHour;
        this.isPublic = isPublic;
        
        talentId = AutoIncrement.getIncrementTalents();
        
    }

    public void setTalentId(int talentId) {
        this.talentId = talentId;
    }

    public int getTalentId() {
        return talentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(float pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Collection<SkillLine> getSkills() {
        return skills;
    }

    /**
     * Get Collection of Skills belonging to Talent
     * @return Collection
     */
    public Collection<Skill> getSkillSet() {

        Collection<Skill> skills = new ArrayList<>();

        for (SkillLine skillLine : getSkills()) {
            skills.add(skillLine.getSkill());
        }

        return skills;

    }

    /**
     * Get Collection of ProfArea belonging to Talent
     * @return
     */
    public Collection<ProfArea> getProfAreaSet() {

        Collection<ProfArea> profAreas = new ArrayList<>();

        for (ProfAreaLine ProfAreaLine : getProfAreas()) {
            profAreas.add(ProfAreaLine.getProfArea());
        }

        return profAreas;

    }

    public Collection<ProfAreaLine> getProfAreas() {
        return profAreas;
    }

    public Collection<Experience> getExperiences() {
        return experiences;
    }

    /**
     * Add Experience to Talent
     * @param experience Given Experience
     */
    public void addExperience(Experience experience) {

        boolean found = false;

        for (Experience exp : experiences) {
            if (exp.getExperienceId() == experience.getExperienceId()) {
                found = true;
            }
        }

        if (found)
            throw new ExperienceException("Already exists a experience with that ID!");

        experiences.add(experience);

    }

    /**
     * Verify if Talent contains Experience
     * @param id Given Experience Id
     * @return true if Talent contains Experience
     */
    public boolean containsExperience(int id) {
        return getExperienceById(id).isPresent();
    }

    /**
     * Get Experience from Talent
     * @param id Given Experience Id
     * @return Optional
     * @throws ExperienceException
     */
    public Optional<Experience> getExperienceById(int id) throws ExperienceException {

        return experiences
                .stream()
                    .filter(exp -> exp.getExperienceId() == id)
                    .findAny();

    }

    /**
     * Remove Experience from talent
     * @param id Given Experience Id
     * @return true if experience is removed successfully
     */
    public boolean removeExperienceById(int id) {

        Iterator<Experience> it = experiences.iterator();

        while(it.hasNext()) {

            Experience experience = it.next();

            if (experience.getExperienceId() == id) {
                it.remove();
                return true;
            }

        } 

        return false;

    }

    /**
     * Add Skill to Talent
     * @param skill Given Skill
     * @param qtyYearExp Given Quantity Years Of Experience
     * @throws SkillException
     */
    public void addSkill(Skill skill, int qtyYearExp) throws SkillException {

        boolean found = false;

        for (SkillLine skl : skills) {
            if (skl.getSkill().getSkillId() == skill.getSkillId()) {
                found = true;
                break;
            }
        }

        if (found)
            throw new SkillException("This Skill already exists in the Talent!");
        
        skills.add(
            new SkillLine(skill, this, qtyYearExp)
        );

    }

    /**
     * Get Skill from talent
     * @param id Given Skill Id
     * @return Skill
     * @throws SkillException
     */
    public Skill getSkillById(int id) throws SkillException {

        for (SkillLine skl : skills) {
            if (skl.getSkill().getSkillId() == id)
            {
                return skl.getSkill();
            }
        }

        throw new SkillException("Not found any Skill with that ID!");

    }

    /**
     * Verify if Talent contains Skill
     * @param id Given Skill id
     * @return true if Talent contains Skill
     */
    public boolean containsSkill(int id) {

        return skills
            .stream()
                .filter(sl -> sl.getSkill().getSkillId() == id)
                .findAny().isPresent();

    }

    /**
     * Remove Skill from talent
     * @param id Given Skill Id
     * @return true if Skill is removed from Talent successfully
     */
    public boolean removeSkillById(int id) {

        Iterator<SkillLine> it = skills.iterator();

        while(it.hasNext()) {

            var skill = it.next().getSkill();

            if (skill.getSkillId() == id)
            {
                it.remove();
                return true;
            }

        }

        return false;

    }

    /**
     * Add ProfArea to Talent
     * @param profArea Given ProfArea
     * @param qtyYearExp Given Quantity Year Of Experience
     */
    public void addProfArea(ProfArea profArea, int qtyYearExp) {

        boolean found = false;

        for (ProfAreaLine pf : profAreas) {
            if (pf.getProfArea().getProfAreaId() == profArea.getProfAreaId()) {
                found = true;
                break;
            }
        }

        if (found)
            throw new ProfAreaException("This Professional Area already exists in the Job Offer!");
        
        profAreas.add(
            new ProfAreaLine(profArea, this, qtyYearExp)
        );

    }

    /**
     * Get ProfArea from talent
     * @param id Given ProfArea Id
     * @return ProfArea
     */
    public ProfArea getProfAreaById(int id) {

        for (ProfAreaLine pf : profAreas) {
            if (pf.getProfArea().getProfAreaId() == id)
            {
                return pf.getProfArea();
            }
        }

        throw new ProfAreaException("Not found any Professional Area with that ID!");

    }

    /**
     * Verify if Talent contains ProfArea
     * @param id Given ProfArea Id
     * @return true if Talent contains ProfArea
     */
    public boolean containsProfAreaById(int id) {
        return profAreas
            .stream()
                .filter(sl -> sl.getProfArea().getProfAreaId() == id)
                .findAny().isPresent();
    }

    /**
     * Remove ProfArea from Talent
     * @param id Given ProfArea Id
     * @return true if ProfArea is removed successfully
     */
    public boolean removeProfAreaById(int id) {

        Iterator<ProfAreaLine> it = profAreas.iterator();

        while(it.hasNext()) {

            var profArea = it.next().getProfArea();

            if (profArea.getProfAreaId() == id)
            {
                it.remove();
                return true;
            }

        }

        return false;

    }

    @Override
    public String toString() {
        
        Collection<Skill> skills = new ArrayList<>();
        Collection<ProfArea> profAreas = new ArrayList<>();

        StringBuilder result = new StringBuilder();


        for (SkillLine skl : getSkills()) {
            skills.add(skl.getSkill());
        }

        for (ProfAreaLine pal : getProfAreas()) {
            profAreas.add(pal.getProfArea());
        }
        
        result.append("Talent (" + this.getTalentId() + "): " + "\tName: " + this.name + "\tPricePerHour: " + pricePerHour + "\tPerfil Visibility: " + isPublic + "\n");

        Iterator<Skill> itSkill = skills.iterator();
        Iterator<ProfArea> itProfArea = profAreas.iterator();

        while (itSkill.hasNext()) {
            Skill current = itSkill.next();
            result.append(current.toString() + " \n");
        }

        while (itProfArea.hasNext()) {
            ProfArea current = itProfArea.next();
            result.append(current.toString() + " \n");
        }

        return result.toString();

    }

}
