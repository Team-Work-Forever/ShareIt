package shareit.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import shareit.errors.ExperienceException;
import shareit.errors.ProfAreaException;
import shareit.errors.SkillException;

public class Talent implements Serializable {
    
    private String name;
    private float pricePerHour;
    private boolean isPublic = true;
    private final Collection<Experience> experiences = new ArrayList<>();
    private final Collection<SkillLine> skills = new ArrayList<>();
    private final Collection<ProfAreaLine> profAreas = new ArrayList<>();

    public Talent(String name, float pricePerHour) {
        this.name = name;
        this.pricePerHour = pricePerHour;
    }

    public Talent(String name, float pricePerHour, boolean isPublic) {
        this.name = name;
        this.pricePerHour = pricePerHour;
        this.isPublic = isPublic;
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

    public Collection<Skill> getSkillSet() {

        Collection<Skill> skills = new ArrayList<>();

        for (SkillLine skillLine : getSkills()) {
            skills.add(skillLine.getSkill());
        }

        return skills;

    }

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

    public void addExperience(Experience experience) {

        boolean found = false;

        for (Experience exp : experiences) {
            if (exp.getTitle().equals(experience.getTitle())) {
                found = true;
            }
        }

        if (found)
            throw new ExperienceException("Já existe uma experiência com esse título!");

        experiences.add(experience);

    }

    public Experience getExperienceByTitle(String name) throws ExperienceException {

        for (Experience exp : experiences) {
            if(exp.getTitle().equals(name))
            {
                return exp;
            }
        }

        throw new ExperienceException("Esperiência com título " + name + " não existe!");

    }

    public boolean removeExperienceByName(String name) {

        Iterator<Experience> it = experiences.iterator();

        while(it.hasNext()) {

            var experience = it.next();

            if (experience.getName().equals(name)) {
                it.remove();
                return true;
            }

        } 

        return false;

    }

    public void addSkill(Skill skill, int qtyYearExp) throws SkillException {

        boolean found = false;

        for (SkillLine skl : skills) {
            if (skl.getSkill().getName().equals(skill.getName())) {
                found = true;
                break;
            }
        }

        if (found)
            throw new SkillException("Skill já existente na proposta!");
        
        skills.add(
            new SkillLine(skill, this, qtyYearExp)
        );

    }

    public Skill getSkillByName(String name) throws SkillException {

        for (SkillLine skl : skills) {
            if (skl.getSkill().getName().equals(name))
            {
                return skl.getSkill();
            }
        }

        throw new SkillException("Não existe nenhuma Skill deste tipo!");

    }

    public boolean containsSkill(String name) {

        return skills
            .stream()
                .filter(sl -> sl.getSkill().getName().equals(name))
                .findAny().isPresent();

    }

    public boolean removeSkillByName(String name) {

        Iterator<SkillLine> it = skills.iterator();

        while(it.hasNext()) {

            var skill = it.next().getSkill();

            if (skill.getName().equals(name))
            {
                it.remove();
                return true;
            }

        }

        return false;

    }

    public void addProfArea(ProfArea profArea, int qtyYearExp) {

        boolean found = false;

        for (ProfAreaLine pf : profAreas) {
            if (pf.getProfArea().getName().equals(profArea.getName())) {
                found = true;
                break;
            }
        }

        if (found)
            throw new ProfAreaException("Esta Área Profissional já existente na proposta!");
        
        profAreas.add(
            new ProfAreaLine(profArea, this, qtyYearExp)
        );

    }

    public ProfArea getProfAreaByName(String name) {

        for (ProfAreaLine pf : profAreas) {
            if (pf.getProfArea().getName().equals(name))
            {
                return pf.getProfArea();
            }
        }

        throw new ProfAreaException("Não existe nenhuma Área Profissional deste tipo!");

    }

    public boolean removeProfAreaByName(String name) {

        Iterator<ProfAreaLine> it = profAreas.iterator();

        while(it.hasNext()) {

            var profArea = it.next().getProfArea();

            if (profArea.getName().equals(name))
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
        
        result.append("Talent: \n" + 
        "Name: " + this.name + "\t" + " PricePerHour: " + pricePerHour + " Perfil Visibility: " + isPublic + "\n\t");

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

    public boolean containsProfArea(String name) {
        return profAreas
            .stream()
                .filter(sl -> sl.getProfArea().getName().equals(name))
                .findAny().isPresent();
    }

}
