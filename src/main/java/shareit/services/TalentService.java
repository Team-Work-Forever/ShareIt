package shareit.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import shareit.data.ExperienceLine;
import shareit.data.JobOffer;
import shareit.data.Privilege;
import shareit.data.ProfArea;
import shareit.data.ProfAreaLine;
import shareit.data.Skill;
import shareit.data.SkillLine;
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

    /**
     * Create Talent, validating it's attributes
     * @param request CreateTalentRequest
     * @return Talent
     * @throws Exception
     */
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

    /**
     * Get Talent by Id
     * @param id Given Talent Id
     * @return Talent
     */
    public Talent getTalentById(int id) {

        var authUser = authenticationService.getAuthenticatedUser();

        Talent validateTalent = authUser.getTalentById(id);

        return validateTalent;

    }

    /**
     * Get All Talents from the system
     * @return Collection
     */
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

    /**
     * Get All talents with an defined order
     * @param comparator Given Comparator
     * @param selectedSkills Given Skills/Integer
     * @return Collection
     */
    public Collection<Talent> getAllTalentsByOrder(Comparator<Talent> comparator, Map<Skill, Integer> selectedSkills) {

        int i;
        Collection<Talent> reallyAllTalents = getReallyAllTalents();
        List<Talent> selectedTalents = new ArrayList<>();

        for (Talent talent : reallyAllTalents) {
                
            i = 0;

            for (Skill skill : selectedSkills.keySet()) {
                if (talent.containsSkill(skill.getSkillId())) {
                    if(getHoursOfExp(talent, skill.getSkillId()) >= selectedSkills.get(skill)) {
                        i++;
                    }
                }
            }

            if (i == selectedSkills.size()) {
                selectedTalents.add(talent);
            }

        }
        
        selectedTalents.sort(comparator);

        return selectedTalents;

    }

    /**
     * Get All Talents that are Public
     * @return Collection
     */
    public Collection<Talent> getAllTalentsPublic() {

        Collection<IdentityUser> members = memberService.getAllMembers();
        Collection<Talent> talents = new ArrayList<>();

        for (IdentityUser identityUser : members) {
            for (Talent talent : identityUser.getTalents()) {
                if(talent.getIsPublic() == true) talents.add(talent);     
            }
        }

        return talents;

    }

    /**
     * Get All Talents from user
     * @return Collection
     */
    public Collection<Talent> getAllTalents() {
        var authUser = authenticationService.getAuthenticatedUser();

        return authUser.getTalents();
    }

    /**
     * Remove Talent
     * @param id Given Talent Id
     * @return true if Talent is removed
     * @throws TalentException
     */
    public boolean removeTalent(int id) throws TalentException {
        
        Talent talent;
        Collection<Skill> skills;
        Collection<ProfArea> profAreas;

        var authUser = authenticationService.getAuthenticatedUser();

        talent = getTalentById(id);
        skills = talent.getSkillSet();
        profAreas = talent.getProfAreaSet(); 
        
        if (!talent.getExperiences().isEmpty())
            throw new TalentException("You can't remove this talent, because there are experiences associated!");
        
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

    /**
     * Update Talent
     * @param id Given Talent
     * @param request CreateTalentRequest
     * @return true if Talent is updated
     * @throws Exception
     */
    public boolean updateTalent(int id, @Validated CreateTalentRequest request) throws Exception {

        Map<Skill, Integer> selectedSkills = new HashMap<>();
        Map<ProfArea, Integer> selectedProfAreas = new HashMap<>();

        var authUser = authenticationService.getAuthenticatedUser();
        Talent currentTalent = authUser.getTalentById(id);

        var erros = validatorTalent.validate(request);

        if (!erros.isEmpty()) {
            throw new TalentException(erros.iterator().next().getMessage());
        }
        
        Talent updatedTalent = request.toTalent();
        updatedTalent.setTalentId(currentTalent.getTalentId());

        authUser.removeTalent(currentTalent.getTalentId());
        
        updatedTalent = createTalent(new CreateTalentRequest(
            updatedTalent.getName(), 
            updatedTalent.getPricePerHour(), 
            updatedTalent.getIsPublic()
        ));

        // Associate All Skills once more again!
        for (SkillLine skillLine : currentTalent.getSkills()) {
            selectedSkills.put(skillLine.getSkill(), skillLine.getYearOfExp());
        }

        associateSkills(new TalentAssociationSkill(
            updatedTalent, 
            selectedSkills
        ));
        
        for (ProfAreaLine profAreaLine : currentTalent.getProfAreas()) {
            selectedProfAreas.put(profAreaLine.getProfArea(), profAreaLine.getYearOfExp());
        }

        associateProfAreas(new TalentAssociationProfArea(
            updatedTalent,
            selectedProfAreas
        ));

        globalRepository.commit();

        return true;

    }

    /**
     * Get hours of experience in each talent
     * @param talent Given Talent
     * @param id Given Skill Id
     * @return total of hours
     */
    public int getHoursOfExp(Talent talent, int id) {

        for (SkillLine skillLine : talent.getSkills()) {

            if (skillLine.getSkill().getSkillId() == id)
                return skillLine.getYearOfExp();
        }

        throw new TalentException("Skill not found in this talent!");
    }

    /**
     * Associate Skill to Talent
     * @param request Given TalentAssociationSkill
     * @throws Exception
     */
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

    /**
     * Disassociate Skill
     * @param talentDisassociateSkill TalentDisassociate
     * @throws Exception
     */
    public void disassociateSkills(@Validated TalentDisassociateSkill talentDisassociateSkill) throws Exception {

        var errors = validatorDisassociateSkill.validate(talentDisassociateSkill);

        if (!errors.isEmpty()) {
            throw new TalentException(errors.iterator().next().getMessage());
        }

        var authUser = authenticationService.getAuthenticatedUser();

        Talent talent = authUser.getTalentById(talentDisassociateSkill.getTalent().getTalentId());
        
        var skill = talentDisassociateSkill.getSkill();
        
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

    /**
     * Associate ProfAreas
     * @param request Given TalentAssociationProfArea
     * @throws Exception
     */
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

    /**
     * Disassociate ProfArea
     * @param request TalentDisassociateProf
     * @throws Exception
     */
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

    /**
     * Create Experience, validating it's attributes
     * @param request CreateExperienceRequest
     * @return Experience
     * @throws Exception
     */
    public Experience createExperience(@Validated CreateExperienceRequest request) throws Exception {

        Experience experience;
        Talent talent;
        IdentityUser authUser = authenticationService.getAuthenticatedUser();

        var errors = validatorCreateExperience.validate(request);

        if (!errors.isEmpty()) {
            throw new ExperienceException(errors.iterator().next().getMessage());
        }

        experience = request.toExperience();
        experience.addClient(authUser, Privilege.OWNER);

        talent = authUser.getTalentById(request.getTalent().getTalentId());

        talent.addExperience(experience);

        globalRepository.updateIdentityUserByEmail(authUser.getEmail(), authUser);
        globalRepository.commit();

        return experience;

    }

    /**
     * Get Experience By JobOffer
     * @param id Given JobOffer Id
     * @return Experience
     */
    public Experience getExperienceByJobOfferId(int id) {

        for (Talent talent : getReallyAllTalents()) {
            for (Experience experience : talent.getExperiences()) {
                if (experience.getJobOfferById(id).isPresent()) {
                    return experience;
                }
            }
        }

        throw new ExperienceException("There is no experience with that ID");

    }

    /**
     * Get Experience
     * @param id Given Experience Id
     * @return Experience
     */
    public Experience getExperienceById(int id) {

        for (Talent talent : getReallyAllTalents()) {
            if (talent.containsExperience(id)) {
                return talent.getExperienceById(id).get();
            }
        }
        
        throw new ExperienceException("Id does not exists!");

    }

    /**
     * Update Experience
     * @param request CreateExperienceRequest
     * @param id Given Experience Id
     */
    public void updateExperience(CreateExperienceRequest request, int id) {

        var errors = validatorCreateExperience.validate(request);

        if (!errors.isEmpty()) {
            throw new ExperienceException(errors.iterator().next().getMessage());
        }

        try {

            var currentExperience = getExperienceById(id);

            removeExperienceById(id);
            var experience = createExperience(request);

            // Associate again all jobOffers
            for (JobOffer jobOffer : currentExperience.getJobOffers()) {
                experience.addJobOffer(jobOffer);
            }

            // Associate again all clients
            for (ExperienceLine expl:  currentExperience.getExperienceLines()) {
                if (!expl.getClient().getEmail().equals(authenticationService.getAuthenticatedUser().getEmail())) {
                    experience.addClient(expl.getClient(), expl.getPrivilege());
                }
            }

            globalRepository.commit();

        } catch (Exception e) {
            throw new JobOfferException(e.getMessage());
        }

    }

    /**
     * Remove Experience
     * @param id Given Experience Id
     */
    public void removeExperienceById(int id) {
        
        var authUser = authenticationService.getAuthenticatedUser();
        Collection<Talent> talents = authUser.getTalents();

        try {
            
            for (Talent talent : talents) {
                if (talent.containsExperience(id)) {
                    
                    if ((talent.getExperienceById(id).get().getAllClients().size() > 1))
                        throw new ExperienceException("You can't remove this experience, because there are clients associated!");
                    
                    if (!talent.getExperienceById(id).get().getJobOffers().isEmpty())
                        throw new  ExperienceException("You can't remove this experience, because there are job offers associated!");
    
                    talent.removeExperienceById(getExperienceById(id).getExperienceId());
                }
            }

        } catch (Exception e) {
            throw new TalentException(e.getMessage());
        }

    }

    // JobOffer

    /**
     * Get JobOffer
     * @param id Given JobOffer Id
     * @return Optional
     * @throws IOException
     */
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

    /**
     * Get Creator JobOffer
     * @param id Given JobOffer Id
     * @return IdentityUser
     */
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

    /**
     * Remove JobOffer
     * @param jobOffer Given JobOffer
     * @return
     */
    public boolean removeJobOffer(JobOffer jobOffer) {

        boolean result = false;

        Collection<IdentityUser> allMembers = memberService.getAllMembers();

        for (IdentityUser member : allMembers) {
            for (Talent talent : member.getTalents()) {
                for (Experience experience : talent.getExperiences()) {
                    if (experience.containsJobOffer(jobOffer.getJobOfferId())) {
                        result = experience.removeJobOfferById(jobOffer.getJobOfferId());
                    }
                }
            }
        }

        return result;

    }

    /**
     * Move Client from JobOffer to Experience
     * @param currentExperience Given Experience
     * @param currentJobOffer Given JobOffer
     * @param client Given Client
     * @throws Exception
     */
    public void moveClientFromJobOfferToExperience(Experience currentExperience, JobOffer currentJobOffer, IdentityUser client) throws Exception {

        if (currentExperience.containsClient(client.getEmail())) {
            throw new ExperienceException("This Member already is associated to this experience!");
        }
        
        currentExperience.addClient(client, Privilege.WORKER);
        client.associateExperience(currentExperience, Privilege.WORKER);
        currentJobOffer.removeClient(client);
        client.disassociateJobOffer(currentJobOffer);

        globalRepository.commit();

    }

    /**
     * Get Talent by Experience
     * @param id Given Experience Id 
     * @return Talent
     */
    public Talent getTalentByExperienceId(int id) {

        for (Talent talent : getReallyAllTalents()) {
            for (Experience experience : talent.getExperiences()) {
                if (experience.getExperienceId() == id) {
                    return talent;
                }
            }
        }

        throw new TalentException("There is no talent with that experience");

    }

    /**
     * Remove Client from Experience
     * @param identityUser Given Client
     * @param experience Given Experience
     * @return true if client is removed from Experience
     * @throws Exception
     */
    public boolean removeClientFromExperience(IdentityUser identityUser, Experience experience) throws Exception {

        IdentityUser authUser = authenticationService.getAuthenticatedUser();

        if (authUser.getEmail().equals(identityUser.getEmail())) {
            throw new ExperienceException("You cannot remove yourself!");
        }

        if (!experience.getPrivilegeOfClient(identityUser.getEmail()).equals(Privilege.OWNER)) {
           
            if (!experience.removeClient(identityUser.getEmail())) {
                throw new ExperienceException("Error Removing user");
            }

            identityUser.removeExperienceById(experience.getExperienceId());
            
            return true;

        } else {
            throw new ExperienceException("You cannot remove the Owner from the experience!");
        }

    }

    /**
     * Alter Privilege from User in Experience
     * @param experience Given Experience
     * @param privilege Given Privilege
     * @param user Given User
     */
    public void experienceAlterPrivilege(Experience experience, Privilege privilege, IdentityUser user) {

        IdentityUser authUser = authenticationService.getAuthenticatedUser();

        if (authUser.getEmail().equals(user.getEmail())) {
            throw new ExperienceException("You cannot alter your Privilege!");
        }

        experience.ChangeClientPrivilege(user.getEmail(), privilege);

    }

}
