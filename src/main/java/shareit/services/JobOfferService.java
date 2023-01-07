package shareit.services;

import java.io.IOException;
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
    private GlobalRepository globalRepository;

    private final BeanValidator<CreateJobOfferRequest> validatorCreateJobOffer = new BeanValidator<>();

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

    public Collection<IdentityUser> getAllClients(int jobOfferId) throws IOException {

        Optional<JobOffer> jobOfferFound = talentService.getJobOfferById(jobOfferId);

        if (!jobOfferFound.isPresent()) {
            throw new JobOfferException("No JobOffer was found!");
        }
        
        return jobOfferFound.get().getClients();

    }

    public Collection<Skill> getAllSkills(int jobOfferId) throws IOException {

        Optional<JobOffer> jobOfferFound = talentService.getJobOfferById(jobOfferId);
        
        if (!jobOfferFound.isPresent()) {
            throw new JobOfferException("No JobOffer was Found!");
        }

        return jobOfferFound.get().getAllSkills();

    }

    public void associateClient(IdentityUser client, int jobOfferId) throws Exception {

        Optional<JobOffer> jobOfferFound = talentService.getJobOfferById(jobOfferId);;

        if (!jobOfferFound.isPresent()) {
            throw new JobOfferException("No JobOffer was Found!");
        }

        jobOfferFound.get().addClient(client);
        globalRepository.commit();

    }

    public boolean associateSkill(@Validated AssociateSkillRequest request) throws Exception {

        var errors = validatorSkill.validate(request);

        if (!errors.isEmpty()) {
            throw new JobOfferException(errors.iterator().next().getMessage());
        }

        Optional<JobOffer> jobOfferFound = talentService.getJobOfferById(request.getJobOfferId());;

        if (!jobOfferFound.isPresent()) {
            throw new JobOfferException("No JobOffer was found!");
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

    public boolean disassociateJobOffer(JobOffer jobOffer, IdentityUser client) {
        client.disassociateJobOffer(jobOffer);
        return jobOffer.removeClient(client);
    }

}
