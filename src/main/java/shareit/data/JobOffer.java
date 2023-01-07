package shareit.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import shareit.data.auth.IdentityUser;
import shareit.errors.SkillException;
import shareit.errors.auth.IdentityException;

public class JobOffer implements Serializable {

    private static int increment = 1;
    private int jobOfferId;
    private String name;
    private int qtyHours;
    private String desc;
    private ProfArea profArea;
    private State state;
    private final Collection<IdentityUser> clients = new ArrayList<>();
    private final Collection<SkillOfferLine> skillOfferLines = new ArrayList<>();

    public JobOffer(String name, int qtyHours, String desc, ProfArea profArea) {
        
        jobOfferId = increment;

        this.name = name;
        this.qtyHours = qtyHours;
        this.desc = desc;
        this.profArea = profArea;

        this.state = State.Available;

        increment++;

    }

    public int getJobOfferId() {
        return jobOfferId;
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

    public Collection<IdentityUser> getClients() {
        return clients;
    }

    public Collection<SkillOfferLine> getSkillOfferLines() {
        return skillOfferLines;
    }

    public void addClient(IdentityUser client) throws IdentityException {

         boolean found = false;

        for (IdentityUser cl : clients) {
            if (cl.getEmail().equals(client.getEmail())) {
                found = true;
                break;
            }
        }

        if (found)
            throw new IdentityException("This User already exists in the experience!");
        
        clients.add(client);

    }

    public boolean containsClient(String email) {

        return clients
            .stream()
                .filter(client -> client.getEmail().equals(email))
                .findAny().isPresent();

    }

    public boolean removeClient(IdentityUser client) {
        
        client.disassociateJobOffer(this);

        return clients.remove(client);

    }

    public IdentityUser getClientByEmail(String email) throws IdentityException {
        
        for (IdentityUser client : clients) {
            if (client.getEmail().equals(email)) {
                return client;
            }
        }

        throw new IdentityException("Experience not found by the email: " + " " + email);

    }

    public void addSkill(Skill skill, int qtyYearNec) throws SkillException {

        boolean found = false;

        for (SkillOfferLine skl : skillOfferLines) {
            if (skl.getSkill().getSkillId() == skill.getSkillId()) {
                found = true;
                break;
            }
        }

        if (found)
            throw new SkillException("This Skill already exists in the Job Offer!");
        
        skillOfferLines.add(
            new SkillOfferLine(skill, this, qtyYearNec)
        );

    }

    public boolean removeSkillById(int id) {

        Iterator<SkillOfferLine> it = skillOfferLines.iterator();
    
        while (it.hasNext()) {
            
            var skill = it.next().getSkill();

            if (skill.getSkillId() == id)
            {
                it.remove();
                return true;
            }

        }

        return false;

    }

    public Collection<Skill> getAllSkills() {

        Collection<Skill> skills = new ArrayList<>();

        for (SkillOfferLine skl : this.skillOfferLines) {
            skills.add(skl.getSkill());
        }

        return skills;

    }

    public Skill getSkillbyId(int id) throws SkillException {
        
        for (SkillOfferLine skill : skillOfferLines) {
            if (skill.getSkill().getSkillId() == id) {
                return skill.getSkill();
            }
        }

        throw new SkillException("Skill not found by the ID: " + id);

    }

    public String getAllSkillsToString() {

        StringBuilder stringBuilder = new StringBuilder();

        for (SkillOfferLine skl : this.skillOfferLines) {
                skl.getSkill().toStringJobOffer();
                stringBuilder.append("\tSkill (" + skl.getSkill().getSkillId() + "): " + "\tName: " + skl.getSkill().getName() + "\t Qty Years Necessary: " + skl.getYearOfExpNec());
        }

        return stringBuilder.toString();

    }

    public String toString() {
        return "Job Offer (" + this.getJobOfferId() + "): \n" + 
            "\t Name: " + this.name + "\t Description: " + this.desc + "\t Qty Hours: " + this.getQtyHours() + "\n Professional Area: " + this.profArea.getName() + "\n Skill: " + getAllSkillsToString() + "\n State: " + this.state + "\n";
    }

}
