package shareit.services;

import java.util.Collection;

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

        var errors = validatorCreateJobOffer.validate(request);

        if (!errors.isEmpty()) {
            throw new JobOfferException(errors.iterator().next().getMessage());
        }

        jobOffer = new JobOffer(
            request.getName(),
            request.getQtyHours(),
            request.getDesc(),
            request.getProfArea()
        );

        var authUser = authenticationService.getAuthenticatedUser();

        Experience experience = authUser.getTalentoByName(request.getTalentName())
            .getExperienceByTitle(request.getExperienceTile());

        experience.addJobOffer(jobOffer);

        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

        return jobOffer;

    }

    public JobOffer updateJobOffer(@Validated request) {

        var errors = validatorCreateJobOffer.validate(request);

        if (!errors.isEmpty()) {
            throw new JobOfferException(errors.iterator().next().getMessage());
        }

    }

    public Collection<IdentityUser> getAllClients(int jobOfferId) {

        var jobOffer = talentService.getJobOfferById(jobOfferId);
        
        return jobOffer.getClients();

    }

    public Collection<Skill> getAllSkills(int jobOfferId) {

        var jobOffer = talentService.getJobOfferById(jobOfferId);
        
        return jobOffer.getAllSkills();

    }

    public void associateClient(IdentityUser client, int jobOfferId) throws Exception {

        var jobOffer = talentService.getJobOfferById(jobOfferId);

        jobOffer.addClient(client);
        globalRepository.commit();

    }

    public boolean associateSkill(@Validated AssociateSkillRequest request) throws Exception {

        var errors = validatorSkill.validate(request);

        if (!errors.isEmpty()) {
            throw new JobOfferException(errors.iterator().next().getMessage());
        }

        var jobOffer = talentService.getJobOfferById(request.getJobOfferId());

        for (Skill skill : request.getSkills().keySet()) {
            
            jobOffer.addSkill(
                skill, 
                request.getSkills().get(skill)
            );

        }

        globalRepository.commit();

        return true;

    }

}
