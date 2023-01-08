package shareit.data.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

import shareit.data.Experience;
import shareit.data.ExperienceLine;
import shareit.data.JobOffer;
import shareit.data.Privilege;
import shareit.data.Skill;
import shareit.data.Talent;
import shareit.errors.TalentException;

public class IdentityUser implements Serializable {
    
    private String email;
    private String password;
    private String name;
    private String lastName;
    private Date bornDate;
    private String street;
    private String postCode;
    private String locality;
    private String country;
    private float moneyPerHour;
    private boolean isPublic;
    private String role;

    private Collection<Talent> talents = new ArrayList<>();
    private Collection<Skill> mySkills = new ArrayList<>();
    private Collection<ExperienceLine> invitedExperiences = new ArrayList<>();
    private Collection<JobOffer> invitedJobOffers = new ArrayList<>();

    public IdentityUser(String email, String password, String name, String lastName, Date bornDate, String street, String postCode, String locality,
        String country, float moneyPerHour, boolean isPublic, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.bornDate = bornDate;
        this.street = street;
        this.postCode = postCode;
        this.locality = locality;
        this.country = country;
        this.moneyPerHour = moneyPerHour;
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

    public float getMoneyPerHour() {
        return moneyPerHour;
    }

    public void setMoneyPerHour(float moneyPerHour) {
        this.moneyPerHour = moneyPerHour;
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

    public Date getBornDate() {
        return bornDate;
    }

    public void setBornDate(Date bornDate) {
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
            if (tal.getName().equals(talento.getName())) {
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

        throw new TalentException("Talento com nome " + id + " não existe!");

    }

    public void removeTalent(int id) throws TalentException {

        Talent talent = getTalentById(id);

        talents.remove(talent);

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
        return mySkills;
    }

    public void setMySkills(Collection<Skill> mySkills) {
        this.mySkills = mySkills;
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
