package shareit.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import shareit.contracts.joboffer.AssociateSkillRequest;
import shareit.contracts.joboffer.CreateJobOfferRequest;
import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.Skill;
import shareit.data.State;
import shareit.data.Talent;
import shareit.data.auth.IdentityUser;
import shareit.errors.JobOfferException;
import shareit.repository.GlobalRepository;
import shareit.validator.BeanValidator;

@Service
public class JobOfferService {
    
    private final BeanValidator<AssociateSkillRequest> validatorSkill = new BeanValidator<>();

    @Autowired
    private TalentService talentService;
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private GlobalRepository globalRepository;

    private final BeanValidator<CreateJobOfferRequest> validatorCreateJobOffer = new BeanValidator<>();

    /**
     * Create JobOffer, validating all it's attributes
     * @param request Given CreateJobOfferRequest
     * @return
     * @throws Exception
     */
    public JobOffer createJobOffer(@Validated CreateJobOfferRequest request) throws Exception {

        JobOffer jobOffer;
        Experience experience;
        IdentityUser authUser = authenticationService.getAuthenticatedUser();

        var errors = validatorCreateJobOffer.validate(request);

        if (!errors.isEmpty()) {
            throw new JobOfferException(errors.iterator().next().getMessage());
        }
        
        jobOffer = request.toJobOffer();
        jobOffer.addClient(authUser);

        experience = request.getExperience();

        experience.addJobOffer(jobOffer);

        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

        return jobOffer;

    }

    /**
     * Remove JobOffer
     * @param jobOffer Given JobOffer
     * @return true if JobOffer is removed successfully
     */
    public boolean removeJobOffer(JobOffer jobOffer) {
        return talentService.removeJobOffer(jobOffer);
    }

    /**
     * Update JobOffer, validating all it's attributes
     * @param newJobOffer Given new JobOffer
     * @param id Given Original JobOffer Id
     * @return true if original JobOffer is updated
     * @throws IOException
     */
    public boolean updateJobOffer(JobOffer newJobOffer, int id) throws IOException {

        Optional<JobOffer> validateJobOffer = talentService.getJobOfferById(id);

        if (!validateJobOffer.isPresent()) {
            throw new JobOfferException("Job Offer not found by the id: " + id);
        }

        try {

            talentService.removeJobOffer(validateJobOffer.get());

            newJobOffer = createJobOffer(
                new CreateJobOfferRequest(
                    talentService.getExperienceByJobOfferId(id),
                    newJobOffer.getName(), 
                    newJobOffer.getQtyHours(), 
                    newJobOffer.getDesc(),
                    newJobOffer.getProfArea()
                )
            );

            newJobOffer.setState(State.Changed);

            globalRepository.commit();

        } catch (Exception e) {
            throw new JobOfferException(e.getMessage());
        }

        return true;

    }

    /**
     * Disassociate Client from Job Offer 
     * @param jobOffer Given JobOffer
     * @param client Given CLient
     * @return true if client is disassociated from JobOffer
     */
    public boolean disassociateJobOffer(JobOffer jobOffer, IdentityUser client) {
        client.disassociateJobOffer(jobOffer);
        return jobOffer.removeClient(client);
    }

    /**
     * Get All JobOffers 
     * @return Collection
     */
    public Collection<JobOffer> getAllJobOffers() {

        Collection<IdentityUser> allMembers = memberService.getAllMembers();
        Collection<JobOffer> jobOffers = new ArrayList<>();

        for (IdentityUser user : allMembers) {
            for (Talent talent : user.getTalents()) {
                for (Experience experience : talent.getExperiences()) {
                    for (JobOffer jobOffer : experience.getJobOffers()) {
                        jobOffers.add(jobOffer);
                    }
                }
            }
        }

        return jobOffers;

    }

    /**
     * Get All Clients from JobOffer
     * @param jobOfferId Given JobOffer Id
     * @return Collection
     * @throws IOException
     */
    public Collection<IdentityUser> getAllClients(int jobOfferId) throws IOException {

        Optional<JobOffer> jobOfferFound = talentService.getJobOfferById(jobOfferId);

        if (!jobOfferFound.isPresent()) {
            throw new JobOfferException("No Job Offer was found!");
        }
        
        return jobOfferFound.get().getClients();

    }

    /**
     * Associate Client to JobOffer
     * @param client Given Client
     * @param jobOfferId Given JobOffer Id
     * @throws Exception
     */
    public void associateClient(IdentityUser client, int jobOfferId) throws Exception {

        Optional<JobOffer> jobOfferFound = talentService.getJobOfferById(jobOfferId);;

        if (!jobOfferFound.isPresent()) {
            throw new JobOfferException("No Job Offer was Found!");
        }

        jobOfferFound.get().addClient(client);
        globalRepository.commit();

    }

    /**
     * Get All Skills from JobOffer
     * @param jobOfferId JobOffer Id
     * @return Collection
     * @throws IOException
     */
    public Collection<Skill> getAllSkills(int jobOfferId) throws IOException {

        Optional<JobOffer> jobOfferFound = talentService.getJobOfferById(jobOfferId);
        
        if (!jobOfferFound.isPresent()) {
            throw new JobOfferException("No Job Offer was Found!");
        }

        return jobOfferFound.get().getAllSkills();

    }

    /**
     * Associate Skill to JobOffer
     * @param request Given AssociateSkillRequest
     * @return true if Skill is associated to JobOffer successfully
     * @throws Exception
     */
    public boolean associateSkill(@Validated AssociateSkillRequest request) throws Exception {

        var errors = validatorSkill.validate(request);

        if (!errors.isEmpty()) {
            throw new JobOfferException(errors.iterator().next().getMessage());
        }

        Optional<JobOffer> jobOfferFound = talentService.getJobOfferById(request.getJobOfferId());;

        if (!jobOfferFound.isPresent()) {
            throw new JobOfferException("No Job Offer was found!");
        }

        for (Skill skill : request.getSkills().keySet()) {
            
            jobOfferFound.get().addSkill(
                skill, 
                request.getSkills().get(skill)
            );

        }

        globalRepository.commit();

        return true;

    }

}
