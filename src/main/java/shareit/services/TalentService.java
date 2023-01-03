package shareit.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import shareit.contracts.experience.CreateExperienceRequest;
import shareit.contracts.talent.TalentAssociationProfArea;
import shareit.contracts.talent.TalentAssociationSkill;
import shareit.contracts.talent.TalentRequest;
import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.ProfArea;
import shareit.data.Skill;
import shareit.data.Talent;
import shareit.errors.ExperienceException;
import shareit.errors.JobOfferException;
import shareit.errors.TalentException;
import shareit.validator.BeanValidator;
import shareit.repository.GlobalRepository;

@Service
public class TalentService {
    
    private final BeanValidator<TalentRequest> validatorTalent = new BeanValidator<>();
    private final BeanValidator<TalentAssociationSkill> validatorSkill = new BeanValidator<>();
    private final BeanValidator<TalentAssociationProfArea> validatorProfArea = new BeanValidator<>();
    private final BeanValidator<CreateExperienceRequest> validatorCreateExperience = new BeanValidator<>();

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private ProfAreaService profAreaService;

    @Autowired
    private GlobalRepository globalRepository;

    public boolean createTalent(@Validated TalentRequest talentRequest) throws Exception {

        var errors = validatorTalent.validate(talentRequest);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        // Get Auth user
        var authUser = authenticationService.getAuthenticatedUser();

        // Get Talent Info
        Talent newTalent = new Talent(
            talentRequest.getName(), 
            talentRequest.getPricePerHour()
        );

        // Associate talent to user
        authUser.addTalent(newTalent);

        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

        return true;

    }

    public Talent getTalentByName(String name) {

        var authUser = authenticationService.getAuthenticatedUser();

        Talent validateTalent = authUser.getTalentoByName(name);

        return validateTalent;

    }

    public Collection<Talent> getAllTalents() {
        var authUser = authenticationService.getAuthenticatedUser();

        return authUser.getTalents();
    }

    public boolean removeTalent(String name) throws TalentException {
        
        var authUser = authenticationService.getAuthenticatedUser();
        authUser.removeTalent(name);       
        
        return true;

    }

    // TODO: Desassociar skills
    public void associateSkills(@Validated TalentAssociationSkill request) throws Exception {

        var errors = validatorSkill.validate(request);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        var authUser = authenticationService.getAuthenticatedUser();

        Talent talent = authUser.getTalentoByName(request.getNameTalent());
        
        for (Skill skill : request.getSkills()) {
            
            boolean talentFound = authUser.getTalents()
                .stream()
                    .filter(t -> t.containsSkill(skill.getName()))
                    .findAny().isPresent();

            if (!talentFound)
                skillService.getSkillByName(skill.getName()).incrementQtyProf();

            talent.addSkill(skill, request.getYearOfExp());
                
        }

        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

    }

    // TODO: Desassociar profareas
    public void associateProfAreas(@Validated TalentAssociationProfArea request) throws Exception {

        var errors = validatorProfArea.validate(request);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        var authUser = authenticationService.getAuthenticatedUser();

        Talent talent = authUser.getTalentoByName(request.getTalentName());

        for (ProfArea profArea : request.getProfAreas().keySet()) {

            boolean talentFound = authUser.getTalents()
            .stream()
                .filter(t -> t.containsProfArea(profArea.getName()))
                .findAny().isPresent();

            if (!talentFound)
                profAreaService.getProfAreaByName(profArea.getName()).incrementQtyProf();

            talent.addProfArea(profArea, request.getProfAreas().get(profArea));

        }

        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

    }

    public void createExperience(@Validated CreateExperienceRequest createExperienceRequest) throws Exception {

        var errors = validatorCreateExperience.validate(createExperienceRequest);

        if (!errors.isEmpty()) {
            throw new ExperienceException(errors.iterator().next().getMessage());
        }

        Experience experience = new Experience(
            createExperienceRequest.getTitle(), 
            createExperienceRequest.getName(), 
            createExperienceRequest.getStartDate(), 
            createExperienceRequest.getFinalDate(), 
            createExperienceRequest.getDesc()
        );

        var authUser = authenticationService.getAuthenticatedUser();
        Talent talent = authUser.getTalentoByName(createExperienceRequest.getTalentName());

        talent.addExperience(experience);

        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

    }

    public Experience getExperienceByTitle(String title) {

        var authUser = authenticationService.getAuthenticatedUser();
        Collection<Talent> talents = authUser.getTalents();

        try {
            
            for (Talent talent : talents) {
                return talent.getExperienceByTitle(title);
            }

        } catch (Exception e) {
            throw new TalentException(e.getMessage());
        }

        throw new TalentException("Was not found any Experience with that title!");        

    }

    public JobOffer getJobOfferById(int id) {

        var authUser = authenticationService.getAuthenticatedUser();
        Collection<Talent> talents = authUser.getTalents();

        try {
            
            for (Talent talent : talents) {
                for (Experience experience : talent.getExperiences())
                {   
                    return experience.getJobOfferById(id);
                }
            }

        } catch (Exception e) {
            throw new JobOfferException(e.getMessage());
        }

        throw new JobOfferException("Was not found any Job Offer with that id!");

    }

}
