package shareit.services;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import shareit.contracts.experience.CreateExperienceRequest;
import shareit.contracts.talent.TalentAssociationProfArea;
import shareit.contracts.talent.TalentAssociationSkill;
import shareit.contracts.talent.TalentDisassociateProf;
import shareit.contracts.talent.TalentDisassociateSkill;
import shareit.contracts.talent.CreateTalentRequest;
import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.ProfArea;
import shareit.data.Skill;
import shareit.data.Talent;
import shareit.data.auth.IdentityUser;
import shareit.errors.ExperienceException;
import shareit.errors.JobOfferException;
import shareit.errors.TalentException;
import shareit.errors.auth.IdentityException;
import shareit.validator.BeanValidator;
import shareit.repository.GlobalRepository;
import shareit.utils.ScreenUtils;

@Service
public class TalentService {
    
    private final BeanValidator<CreateTalentRequest> validatorTalent = new BeanValidator<>();
    private final BeanValidator<TalentAssociationSkill> validatorSkill = new BeanValidator<>();
    private final BeanValidator<TalentAssociationProfArea> validatorProfArea = new BeanValidator<>();
    private final BeanValidator<CreateExperienceRequest> validatorCreateExperience = new BeanValidator<>();
    private final BeanValidator<TalentDisassociateProf> validatorDisassociateProf = new BeanValidator<>();
    private final BeanValidator<TalentDisassociateSkill> validatorDisassociateSkill = new BeanValidator<>();

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private ProfAreaService profAreaService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private GlobalRepository globalRepository;

    public boolean createTalent(@Validated CreateTalentRequest request) throws Exception {

        Talent talent;

        var errors = validatorTalent.validate(request);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        // Get Auth user
        var authUser = authenticationService.getAuthenticatedUser();

        // Get Talent Info
        talent = request.toTalent();

        // Associate talent to user
        authUser.addTalent(talent);

        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

        return true;

    }

    public Talent getTalentByName(String name) {

        var authUser = authenticationService.getAuthenticatedUser();

        Talent validateTalent = authUser.getTalentByName(name);

        return validateTalent;

    }

    public Collection<Talent> getAllTalents() {
        var authUser = authenticationService.getAuthenticatedUser();

        return authUser.getTalents();
    }

    public boolean removeTalent(String name) throws TalentException {
        
        Talent talent;
        Collection<Skill> skills;
        Collection<ProfArea> profAreas;

        var authUser = authenticationService.getAuthenticatedUser();

        talent = getTalentByName(name);
        skills = talent.getSkillSet();
        profAreas = talent.getProfAreaSet();       
        
        for (ProfArea profArea : profAreas) {
            
            if (talent.containsProfArea(profArea.getName())) {

                try {

                    disassociateProfAreas(new TalentDisassociateProf(
                        talent.getName(), 
                        profArea
                    ));

                } catch (Exception e) {
                    e.getMessage();
                }
                
            }
            
        }

        for (Skill skill : skills) {

            if (talent.containsSkill(skill.getName())) {

				try {

					disassociateSkills(new TalentDisassociateSkill(
                        talent.getName(), 
                        skill
                    ));

				} catch (Exception e) {
					e.getMessage();
				}
            }
        }

        authUser.removeTalent(name);       
        
        return true;

    }

    public void associateSkills(@Validated TalentAssociationSkill request) throws Exception {

        var errors = validatorSkill.validate(request);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        var authUser = authenticationService.getAuthenticatedUser();

        Talent talent = authUser.getTalentByName(request.getNameTalent());
        
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

    public void disassociateSkills(@Validated TalentDisassociateSkill request) throws Exception {

        var errors = validatorDisassociateSkill.validate(request);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        var authUser = authenticationService.getAuthenticatedUser();

        Talent talent = authUser.getTalentByName(request.getTalentName());
        
        var skill = request.getSkill();
        
        talent.removeSkillByName(skill.getName());
            
        boolean talentFound = authUser.getTalents()
            .stream()
                .filter(t -> t.containsSkill(skill.getName()))
                .findAny().isPresent();

        if (!talentFound)
            skillService.getSkillByName(skill.getName()).reduceQtyProf();
      
        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

    }

    public void associateProfAreas(@Validated TalentAssociationProfArea request) throws Exception {

        var errors = validatorProfArea.validate(request);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        var authUser = authenticationService.getAuthenticatedUser();

        Talent talent = authUser.getTalentByName(request.getTalentName());

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

    public void disassociateProfAreas(@Validated TalentDisassociateProf request) throws Exception {

        var errors = validatorDisassociateProf.validate(request);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        var authUser = authenticationService.getAuthenticatedUser();

        Talent talent = authUser.getTalentByName(request.getTalentName());
        
        talent.removeProfAreaByName(request.getProfArea().getName());

        boolean talentFound = authUser.getTalents()
        .stream()
            .filter(t -> t.containsProfArea(request.getProfArea().getName()))
            .findAny().isPresent();

        if (!talentFound)
            profAreaService.getProfAreaByName(request.getProfArea().getName()).reduceQtyProf();
        
        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

    }

    public void createExperience(@Validated CreateExperienceRequest request) throws Exception {

        Experience experience;
        Talent talent;

        var errors = validatorCreateExperience.validate(request);

        if (!errors.isEmpty()) {
            throw new ExperienceException(errors.iterator().next().getMessage());
        }

        experience = request.toExperience();

        var authUser = authenticationService.getAuthenticatedUser();
        talent = authUser.getTalentByName(request.getTalentName());

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

    // JobOffer

    public JobOffer getJobOfferById(int id) throws IOException {

        Collection<IdentityUser> allMembers = memberService.getAllMembers();;

        try {
            
            for (IdentityUser identityUser : allMembers) {
                for (Talent talent : identityUser.getTalents()) {
                    for (Experience experience : talent.getExperiences())
                    {   
                        return experience.getJobOfferById(id).get();
                    }
                }
            }

        } catch (Exception e) {
           e.printStackTrace();
           ScreenUtils.waitForKeyEnter();
        }

        throw new JobOfferException("Was not found any Job Offer with that id!");

    }

    public IdentityUser getCreatorJobOffer(int id) {

        for (IdentityUser user : memberService.getAllMembers()) {
            for (Talent talent : user.getTalents()) {
                for (Experience experience : talent.getExperiences()) {
                    if (experience.getJobOfferById(id).isPresent()) {
                        return user;
                    }
                }
            }
        }

        throw new IdentityException("Was not found any user!");

    }

}
