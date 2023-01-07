package shareit.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

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

    // Talent

    public Talent createTalent(@Validated CreateTalentRequest request) throws Exception {

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

        return talent;

    }

    public Talent getTalentById(int id) {

        var authUser = authenticationService.getAuthenticatedUser();

        Talent validateTalent = authUser.getTalentById(id);

        return validateTalent;

    }

    public Collection<Talent> getReallyAllTalents() {

        Collection<IdentityUser> members = memberService.getAllMembers();
        Collection<Talent> talents = new ArrayList<>();

        for (IdentityUser identityUser : members) {
            for (Talent talent : identityUser.getTalents()) {
                talents.add(talent);     
            }
        }

        return talents;

    }

    public Collection<Talent> getAllTalents() {
        var authUser = authenticationService.getAuthenticatedUser();

        return authUser.getTalents();
    }

    public boolean removeTalent(int id) throws TalentException {
        
        Talent talent;
        Collection<Skill> skills;
        Collection<ProfArea> profAreas;

        var authUser = authenticationService.getAuthenticatedUser();

        talent = getTalentById(id);
        skills = talent.getSkillSet();
        profAreas = talent.getProfAreaSet();       
        
        for (ProfArea profArea : profAreas) {
            
           if  (talent.containsProfAreaById(profArea.getProfAreaId())) {

                try {

                    disassociateProfAreas(new TalentDisassociateProf(
                        talent, 
                        profArea
                    ));

                } catch (Exception e) {
                    e.getMessage();
                }
                
            }
            
        }

        for (Skill skill : skills) {

            if (talent.containsSkill(skill.getSkillId())) {

				try {

					disassociateSkills(new TalentDisassociateSkill(
                        talent, 
                        skill
                    ));

				} catch (Exception e) {
					e.getMessage();
				}
            }
        }

        authUser.removeTalent(id);       
        
        return true;

    }

    public boolean updateTalent(int id, @Validated CreateTalentRequest request) throws Exception {

        var authUser = authenticationService.getAuthenticatedUser();
        Talent currentTalent = authUser.getTalentById(id);

        var erros = validatorTalent.validate(request);

        if (!erros.isEmpty()) {
            throw new TalentException(erros.iterator().next().getMessage());
        }

        Talent updatedTalent = request.toTalent();

        authUser.removeTalent(currentTalent.getTalentId());
        authUser.addTalent(updatedTalent);

        globalRepository.commit();

        return true;

    }

    public void associateSkills(@Validated TalentAssociationSkill request) throws Exception {

        var errors = validatorSkill.validate(request);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        var authUser = authenticationService.getAuthenticatedUser();

        Talent talent = authUser.getTalentById(request.getTalent().getTalentId());
        
        for (Skill skill : request.getSkills().keySet()) {
            
            boolean talentFound = authUser.getTalents()
                .stream()
                    .filter(t -> t.containsSkill(skill.getSkillId()))
                    .findAny().isPresent();

            if (!talentFound)
                skillService.getSkillById(skill.getSkillId()).incrementQtyProf();

            talent.addSkill(skill, request.getSkills().get(skill));
                
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

        Talent talent = authUser.getTalentById(request.getTalent().getTalentId());
        
        var skill = request.getSkill();
        
        talent.removeSkillById(skill.getSkillId());
            
        boolean talentFound = authUser.getTalents()
            .stream()
                .filter(t -> t.containsSkill(skill.getSkillId()))
                .findAny().isPresent();

        if (!talentFound)
            skillService.getSkillById(skill.getSkillId()).reduceQtyProf();
      
        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

    }

    public void associateProfAreas(@Validated TalentAssociationProfArea request) throws Exception {

        var errors = validatorProfArea.validate(request);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        var authUser = authenticationService.getAuthenticatedUser();

        Talent talent = authUser.getTalentById(request.getTalent().getTalentId());

        for (ProfArea profArea : request.getProfAreas().keySet()) {

            boolean talentFound = authUser.getTalents()
            .stream()
                .filter(t -> t.containsProfAreaById(profArea.getProfAreaId()))
                .findAny().isPresent();

            if (!talentFound)
                profAreaService.getProfAreaById(profArea.getProfAreaId()).incrementQtyProf();

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

        Talent talent = authUser.getTalentById(request.getTalent().getTalentId());
        
        talent.removeProfAreaById(request.getProfArea().getProfAreaId());

        boolean talentFound = authUser.getTalents()
        .stream()
            .filter(t -> t.containsProfAreaById(request.getProfArea().getProfAreaId()))
            .findAny().isPresent();

        if (!talentFound)
            profAreaService.getProfAreaById(request.getProfArea().getProfAreaId()).reduceQtyProf();
        
        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

    }

    //Experience

    public void createExperience(@Validated CreateExperienceRequest request) throws Exception {

        Experience experience;
        Talent talent;

        var errors = validatorCreateExperience.validate(request);

        if (!errors.isEmpty()) {
            throw new ExperienceException(errors.iterator().next().getMessage());
        }

        experience = request.toExperience();

        var authUser = authenticationService.getAuthenticatedUser();
        talent = authUser.getTalentById(request.getTalent().getTalentId());

        talent.addExperience(experience);

        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

    }

    public Experience getExperienceById(int id) {

        for (Talent talento : getReallyAllTalents()) {
            return talento.getExperienceById(id);
        }

        throw new ExperienceException("There is no shit with that name");

    }

        public void removeExperienceById(int id) {
        
        var authUser = authenticationService.getAuthenticatedUser();
        Collection<Talent> talents = authUser.getTalents();

        try {
            
            for (Talent talent : talents) {
                talent.removeExperienceById(getExperienceById(id).getExperienceId());
            }

        } catch (Exception e) {
            throw new TalentException(e.getMessage());
        }

    }

    // JobOffer

    public Optional<JobOffer> getJobOfferById(int id) throws IOException {

        Collection<IdentityUser> allMembers = memberService.getAllMembers();;

        try {
            
            for (IdentityUser identityUser : allMembers) {
                for (Talent talent : identityUser.getTalents()) {
                    for (Experience experience : talent.getExperiences())
                    {   
                        return experience.getJobOfferById(id);
                    }
                }
            }

        } catch (Exception e) {
           System.out.println("--- Error ---");
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
