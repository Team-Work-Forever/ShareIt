package shareit.data.auth;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import shareit.data.Experience;
import shareit.data.ExperienceLine;
import shareit.data.JobOffer;
import shareit.data.Privilege;
import shareit.data.Skill;
import shareit.data.SkillLine;
import shareit.data.Talent;
import shareit.errors.TalentException;
import shareit.errors.auth.IdentityException;

public class IdentityUser implements Serializable {
    
    private String email;
    private String password;
    private String name;
    private String lastName;
    private LocalDate bornDate;
    private String street;
    private String postCode;
    private String locality;
    private String country;
    private boolean isPublic;
    private String role;

    private Collection<Talent> talents = new ArrayList<>();
    private Collection<ExperienceLine> invitedExperiences = new ArrayList<>();
    private Collection<JobOffer> invitedJobOffers = new ArrayList<>();

    public IdentityUser(String email, String password, String name, String lastName, LocalDate bornDate, String street, String postCode, String locality,
        String country, boolean isPublic, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.bornDate = bornDate;
        this.street = street;
        this.postCode = postCode;
        this.locality = locality;
        this.country = country;
        this.isPublic = isPublic;
        this.role = role;
    }

    public IdentityUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
      
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBornDate() {
        return bornDate;
    }

    public void setBornDate(LocalDate bornDate) {
        this.bornDate = bornDate;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
    
    public Collection<Talent> getTalents() {
        return talents;
    }

    public void setTalents(Collection<Talent> talents) {
        this.talents = talents;
    }

    public void addTalent(Talent talento) {

        boolean found = false;

        for (Talent tal : talents) {
            if (tal.getTalentId() == talento.getTalentId()) {
                found = true;
            }
        }

        if (found)
            throw new TalentException("You already associated this talent!");

        talents.add(talento);

    }

    public Talent getTalentById(int id) {

        for (Talent talento : talents) {
            if(talento.getTalentId() == id)
            {
                return talento;
            }
        }

        throw new TalentException("Talent with the id " + id + " not found!");

    }

    public boolean removeInviteExperienceById(int id) {

        for (ExperienceLine experienceLine : invitedExperiences) {
            if (experienceLine.getExperience().getExperienceId() == id) {
                return experienceLine.getExperience().removeClient(this.getEmail());
            }
        }

        throw new IdentityException("Error removing client from invited experience!");

    }

    public void removeTalent(int id) throws TalentException {

        Talent talent = getTalentById(id);

        talents.remove(talent);

    }

    public boolean removeExperienceById(int id) {

        for (Talent talent : getTalents()) {
            for (Experience experience : talent.getExperiences()) {
                if (experience.getExperienceId() == id) {
                    return talent.removeExperienceById(id);
                }
            }
        }

        throw new IdentityException("Experience not found!");

    }

    public boolean containsTalent(int id) {

        try {

            getTalentById(id);

            return true;
            
        } catch (TalentException e) {
            return false;
        }

    }

    // Invited Experiences

    public Collection<ExperienceLine> getInvitedExperiences() {
        return invitedExperiences;
    }

    public void setInvitedExperiences(Collection<ExperienceLine> invitedExperiences) {
        this.invitedExperiences = invitedExperiences;
    }

    public Collection<Experience> getExperiences() {

        return invitedExperiences
        .stream()
            .map(experienceLine -> experienceLine.getExperience())
            .toList();
                
    }

    public Optional<Experience> getInviteExperience(String name) {

        return invitedExperiences
        .stream()
            .filter(experienceLine -> experienceLine
                .getExperience().getName().equals(name))
                .map(experienceLine -> experienceLine.getExperience())
                .findAny();

    }

    public boolean associateExperience(Experience experience, Privilege privilege) {

        return invitedExperiences.add(new ExperienceLine(
            this, 
            experience, 
            privilege
        ));

    }

    public boolean disassociateExperience(Experience experience) {

        if (!getInviteExperience(experience.getName()).isPresent()) {
            return false;
        }

        Iterator<ExperienceLine> it = invitedExperiences.iterator();

        while(it.hasNext()) {

           Experience experienceFound = it.next().getExperience();
           
            if (experienceFound.getName().equals(experience.getName())) {
                it.remove();
            }

        }

        return true;

    }

    // Invited JobOffer

    public Collection<JobOffer> getJobOffers() {
        return invitedJobOffers;
    }

    public Optional<JobOffer> getInviteJobOffer(int id) {

        return invitedJobOffers
            .stream()
                .filter(jobOffer -> jobOffer.getJobOfferId() == id)
                .findAny();

    }

    public boolean associateJobOffer(JobOffer jobOffer) {
        return invitedJobOffers.add(jobOffer);
    }

    public boolean disassociateJobOffer(JobOffer jobOffer) {

        if (!getInviteJobOffer(jobOffer.getJobOfferId()).isPresent()) {
            return false;
        }

        return invitedJobOffers.remove(jobOffer);

    }

    // Skills

    public Collection<Skill> getMySkills() {
        
        Collection<Skill> skills = new ArrayList<>();

        for (Talent talent : getTalents()) {
            for (SkillLine skillLine : talent.getSkills()) {
                    if (!skills.contains(skillLine.getSkill())) {
                        skills.add(skillLine.getSkill());
                    }
            }
        }

        return skills;

    }

    public Collection<Skill> getAllSkillsByTalentId(int id) {

        Collection<Skill> skills = new ArrayList<>();

        for (Talent talent : talents) {
                skills.addAll(talent.getSkillSet());
        }

        return skills;

    }

    @Override
    public String toString() {
        return "Member: " + "\n" + 
            "\tEmail: " + email + "\n" + 
            "\tName: " + name + "\tLastName: " + lastName
            ;
    }

}
