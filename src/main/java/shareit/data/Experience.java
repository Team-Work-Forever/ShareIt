package shareit.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

import shareit.data.auth.IdentityUser;
import shareit.errors.ExperienceException;
import shareit.errors.JobOfferException;
import shareit.errors.auth.IdentityException;
import shareit.helper.AutoIncrement;
import shareit.utils.DatePattern;

public class Experience implements Serializable {

    private int id;
    private String title;
    private String name;
    private int qtyWorkers;
    private int qtyManegers;
    private String desc;
    private LocalDate startDate;
    private LocalDate finalDate;
    private final Collection<ExperienceLine> experienceLines = new ArrayList<>();
    private final Collection<JobOffer> jobOffers = new ArrayList<>();
    
    public Experience(String title, String name, LocalDate startDate, String desc) {
        
        this.title = title;
        this.name = name;
        this.startDate = startDate;
        this.desc = desc;

        id = AutoIncrement.getIncrementExperiences();
    }

    public Experience(String title, String name, LocalDate startDate, LocalDate finalDate, String desc) {
        
        this.title = title;
        this.name = name;
        this.startDate = startDate;
        this.finalDate = finalDate;
        this.desc = desc;

        id = AutoIncrement.getIncrementExperiences();
        
    }

    public int getExperienceId() {
        return id;
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

    /**
     * Add an client to experience
     * @param client Given Client
     * @param privilege Given Privilege (Worker/Manager)
     * @throws IdentityException
     */
    public void addClient(IdentityUser client, Privilege privilege) throws IdentityException {

        boolean found = false;

        for (ExperienceLine expl : experienceLines) {
            if (expl.getClient().getEmail().equals(client.getEmail())) {
                found = true;
                break;
            }
        }

        if (found)
            throw new IdentityException("This User already exists in the experience!");
        
        experienceLines.add(
            new ExperienceLine(client, this, privilege)
        );

        if (privilege.equals(Privilege.WORKER))
            qtyWorkers++;
        else qtyManegers++;
        
    }

    /**
     * Change Privilege from client of an experience
     * @param email Given Email from Client
     * @param privilege Given Privilege (worker/Manager)
     * @return true if client's privilege is changed successfully
     */
    public boolean ChangeClientPrivilege(String email, Privilege privilege) {

        Iterator<ExperienceLine> it = experienceLines.iterator();

        while (it.hasNext()) {
            
            var expLine = it.next();
            var client = expLine.getClient();

            if (client.getEmail().equals(email))
            {
                if (expLine.getPrivilege().equals(privilege)) 
                    throw new ExperienceException("User already hold this privilege!");

                else {
                    if (privilege.equals(Privilege.WORKER)) {
                        expLine.setPrivilege(Privilege.WORKER);
                        qtyWorkers++;
                        qtyManegers--;
                    };

                    if (privilege.equals(Privilege.MANAGER)) {
                        expLine.setPrivilege(Privilege.MANAGER);
                        qtyWorkers--;
                        qtyManegers++;
                    };

                    return true;
                }
            }

        }

        return false;

    }

    /**
     * Get Privilege of an Given Client
     * @param email Given Email from Client
     * @return Privilege
     */
    public Privilege getPrivilegeOfClient(String email) {

        for (ExperienceLine expl : experienceLines) {
            if (expl.getClient().getEmail().equals(email)) {
                return expl.getPrivilege();
            }
        }

        throw new IdentityException("Was not found any user with the email: " + email);

    }

    /**
     * Gets Client by Given Email
     * @param email Given Client by Email
     * @return IdentityUser
     * @throws IdentityException
     */
    public IdentityUser getClientByEmail(String email) throws IdentityException {

        for (ExperienceLine expl : experienceLines) {
            for (IdentityUser client : expl.getExperience().getAllClients()) {
                if (client.getEmail().equals(email)) {
                    return client;
                }
            }
        }

        throw new IdentityException("Was not found any user with the email: " + email);

    }

    /**
     * Verify if Experience contains Client
     * @param email Given Client Email
     * @return true if exists
     */
    public boolean containsClient(String email) {

        try {
            
            for (ExperienceLine experienceLine : experienceLines) {
                if (experienceLine.getExperience().getClientByEmail(email) != null) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Verify if Experience contains Job Offer
     * @param id Given Job Offer id
     * @return true if Job Offer exists
     */
    public boolean containsJobOffer(int id) {

        return jobOffers
                .stream()
                    .filter(jobOffer -> jobOffer.getJobOfferId() == id)
                    .findAny().isPresent();

    }

    /**
     * Remove Client from experience
     * @param email Given Client Email
     * @return true if Client is removed successfully
     */
    public boolean removeClient(String email) {

        Iterator<ExperienceLine> it = experienceLines.iterator();

        while (it.hasNext()) {
            
            var expLine = it.next();
            var client = expLine.getClient();

            if (client.getEmail().equals(email))
            {
                it.remove();
                
                if (expLine.getPrivilege().equals(Privilege.WORKER))
                    qtyWorkers--;
                else qtyManegers--;

                return true;
            }

        }

        return false;

    }

    /**
     * Gets All Clients from experience
     * @return Collection
     */
    public Collection<IdentityUser> getAllClients() {

        Collection<IdentityUser> clients = new HashSet<>();

        for (ExperienceLine expl : experienceLines) {
            clients.add(expl.getClient());
        }

        return clients;

    }


    /**
     * Get The Owner from the experience
     * @return IdentityUser
     */
    public IdentityUser getOwner() {

        Optional<IdentityUser> client = experienceLines
                                                .stream()
                                                    .filter(el -> el.getPrivilege().equals(Privilege.OWNER))          
                                                    .map(el -> el.getClient())
                                                    .findAny();

        if (!client.isPresent())
            throw new IdentityException("No Client own this experience");

        return client.get();

    }

    /**
     * Get all clients Managers
     * @return Collection
     */
    public Collection<IdentityUser> getClientManagers() {
        
        Collection<IdentityUser> clients = new HashSet<>();

        for (ExperienceLine expl : experienceLines) {
            if (expl.getPrivilege() == Privilege.MANAGER) {
                clients.add(expl.getClient());
            }
        }

        return clients;

    }

    /**
     * Verify if Belonging Client is Manager
     * @param email Given Client Email
     * @return true if CLient is Manager
     */
    public boolean isManager(String email) {

        return getClientManagers()
            .stream()
                .filter(client -> client.getEmail().equals(email))
                .findAny().isPresent();

    }

    /**
     * Verify if Belonging Client is Worker
     * @param email Given Client Email
     * @return true if CLient is Worker
     */
    public boolean isWorker(String email) {

        return getClientWorkers()
            .stream()
                .filter(client -> client.getEmail().equals(email))
                .findAny().isPresent();

    }

    /**
     * Get All workers
     * @return Collection
     */
    public Collection<IdentityUser> getClientWorkers() {
        
        Collection<IdentityUser> clients = new HashSet<>();

        for (ExperienceLine expl : experienceLines) {
            if (expl.getPrivilege() == Privilege.WORKER) {
                clients.add(expl.getClient());
            }
        }

        return clients;

    }


    /**
     * Add Job Offer to experience
     * @param jobOffer
     */
    public void addJobOffer(JobOffer jobOffer) {
        jobOffers.add(jobOffer);
    }

    /**
     * Get Belonging JobOffer by id
     * @param id Given JobOffer Id
     * @return Optional
     * @throws JobOfferException
     */
    public Optional<JobOffer> getJobOfferById(int id) throws JobOfferException {

        return jobOffers
            .stream()
                .filter(jobOffer -> jobOffer.getJobOfferId() == id)
                .findAny();

    }

    /**
     * Removes Job Offer by it's id
     * @param id Given Job Offer Id
     * @return true if Job Offer is removed successfully
     */
    public boolean removeJobOfferById(int id) {

        Iterator<JobOffer> it = jobOffers.iterator();

        while (it.hasNext()) {
            
            var jobOffer = it.next();

            if (jobOffer.getJobOfferId() == id)
            {
                it.remove();
                return true;
            }

        }

        return false;

    }

    @Override
    public String toString() {
        return "Experience (" + id + "): \n" + 
            "Title: " + this.title + "\t" + 
            "Name: " + this.name + "\t" + 
            "Description: " + desc + "\n" +
            "Qty Works: " + Integer.toString(qtyWorkers) + "\t" +
            "Qty Managers: " + Integer.toString(qtyManegers) + "\n" +
            "Start Date: " + DatePattern.convertDate(startDate) + "\t" +
            "Final Date: " + DatePattern.convertDate(finalDate);
    }

}
