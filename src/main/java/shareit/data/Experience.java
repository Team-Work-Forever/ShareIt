package shareit.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import shareit.data.auth.IdentityUser;
import shareit.errors.ExperienceException;
import shareit.errors.JobOfferException;
import shareit.errors.auth.IdentityException;

public class Experience implements Serializable {

    private String title;
    private String name;
    private int qtyWorkers;
    private int qtyManegers;
    private String desc;
    private Date startDate;
    private Date finalDate;
    private final Collection<ExperienceLine> experienceLines = new ArrayList<>();
    private final Collection<JobOffer> jobOffers = new ArrayList<>();
    
    public Experience(String title, String name, Date startDate, String desc) {
        this.title = title;
        this.name = name;
        this.startDate = startDate;
        this.desc = desc;
    }

    public Experience(String title, String name, Date startDate, Date finalDate, String desc) {
        this.title = title;
        this.name = name;
        this.startDate = startDate;
        this.finalDate = finalDate;
        this.desc = desc;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getQtyWorkers() {
        return qtyWorkers;
    }

    public int getQtyManegers() {
        return qtyManegers;
    }

    public Collection<ExperienceLine> getExperienceLines() {
        return experienceLines;
    }

    public Collection<JobOffer> getJobOffers() {
        return jobOffers;
    }

    public void addClient(IdentityUser client, Privilege privilege) throws IdentityException {

        boolean found = false;

        for (ExperienceLine expl : experienceLines) {
            if (expl.getClient().getEmail().equals(client.getEmail())) {
                found = true;
                break;
            }
        }

        if (found)
            throw new IdentityException("Este Cliente já se encontra registado nesta Experiência!");
        
        experienceLines.add(
            new ExperienceLine(client, this, privilege)
        );

        if (privilege.equals(Privilege.Worker))
            qtyWorkers++;
        else qtyManegers++;
        
    }

    public boolean ChangeClientPrivilege(String email, Privilege privilege) {

        Iterator<ExperienceLine> it = experienceLines.iterator();

        while (it.hasNext()) {
            
            var expLine = it.next();
            var client = expLine.getClient();

            if (client.getEmail().equals(email))
            {
                if (expLine.getPrivilege().equals(privilege)) 
                    throw new ExperienceException("Cliente já detêm este privilégio!");

                else {
                    if (privilege.equals(Privilege.Worker)) {
                        expLine.setPrivilege(Privilege.Worker);
                        qtyWorkers++;
                        qtyManegers--;
                    };

                    if (privilege.equals(Privilege.Manager)) {
                        expLine.setPrivilege(Privilege.Manager);
                        qtyWorkers--;
                        qtyManegers++;
                    };

                    return true;
                }
            }

        }

        return false;

    }

    public IdentityUser getClientByEmail(String email) throws IdentityException {

        for (ExperienceLine expl : experienceLines) {
        if (expl.getExperience().getClientByEmail(email) != null) {
                return expl.getClient();
            }
        }

        throw new IdentityException("Was not found any user with the email: " + email);

    }

    public boolean containsClient(String email) {

        return experienceLines
            .stream()
            .filter(exl -> exl.getExperience().getClientByEmail(email) != null)
                .findAny().isPresent();

    }

    public boolean removeClient(String email) {

        Iterator<ExperienceLine> it = experienceLines.iterator();

        while (it.hasNext()) {
            
            var expLine = it.next();
            var client = expLine.getClient();

            if (client.getEmail().equals(email))
            {
                it.remove();
                
                if (expLine.getPrivilege().equals(Privilege.Worker))
                            qtyWorkers--;
                else qtyManegers--;

                return true;
            }

        }

        return false;

    }

    public Collection<IdentityUser> getClientManagers() {
        
        Collection<IdentityUser> clients = new HashSet<>();

        for (ExperienceLine expl : experienceLines) {
            if (expl.getPrivilege() == Privilege.Manager) {
                clients.add(expl.getClient());
            }
        }

        return clients;

    }

    public Collection<IdentityUser> getClientWorkers() {
        
        Collection<IdentityUser> clients = new HashSet<>();

        for (ExperienceLine expl : experienceLines) {
            if (expl.getPrivilege() == Privilege.Worker) {
                clients.add(expl.getClient());
            }
        }

        return clients;

    }

    public void addJobOffer(JobOffer jobOffer) {
        jobOffers.add(jobOffer);
    }

    public JobOffer getJobOfferById(int id) throws JobOfferException {

        for (JobOffer jobOffer : jobOffers) {
            if (jobOffer.getJobOfferId() == id) {
                return jobOffer;
            }
        }

        throw new JobOfferException("Não existe nenhuma oferta de trabalho com o id: " + id);

    }

    public boolean removeJobOfferByName(String name) {

        Iterator<JobOffer> it = jobOffers.iterator();
    
        while (it.hasNext()) {
            
            var jobOffer = it.next();

            if (jobOffer.getName().equals(name))
            {
                it.remove();
                return true;
            }

        }

        return false;

    }

    @Override
    public String toString() {
        return "Experience: \n" + 
            "Title: " + this.title + "\t" + 
            "Name: " + this.name + "\t" + 
            "Description: " + desc + "\t" +
            "Qty Works: " + Integer.toString(qtyWorkers) + "\t" +
            "Qty Managers: " + Integer.toString(qtyManegers) + "\t" +
            "Start Date: " + startDate.toString() + "\t" +
            "Final Date: " + finalDate.toString();
    }

}
