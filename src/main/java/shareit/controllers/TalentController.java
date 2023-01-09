package shareit.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.comboBox;
import static shareit.utils.ScreenUtils.printSuccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.talent.TalentAssociationSkill;
import shareit.contracts.member.InviteMemberRequest;
import shareit.contracts.talent.CreateTalentRequest;
import shareit.contracts.talent.TalentAssociationProfArea;
import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.ProfArea;
import shareit.data.Skill;
import shareit.data.State;
import shareit.data.Talent;
import shareit.data.auth.IdentityUser;
import shareit.errors.ExperienceException;
import shareit.errors.JobOfferException;
import shareit.errors.TalentException;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.AuthenticationService;
import shareit.services.MemberService;
import shareit.services.ProfAreaService;
import shareit.services.SkillService;
import shareit.services.TalentService;

@Controller
public class TalentController extends ControllerBase {

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ProfAreaService profAreaService;

    @Autowired
    private TalentService talentService;

    private IdentityUser authUser;

    @Override
    public void display() throws IOException {
        
        int index = 0;

        clear();

        do {
            
            try {
                
                authUser = authenticationService.getAuthenticatedUser();

                do {
                    
                    clear();

                    index = menu("***************** Menu *****************", new String[] {
                        "Select Talent",
                        "Create Talent",
                        "List Talents",
                        "Remove Talent",
                        "List JobOffers Available",
                        "Enter Associated Experience",
                        "Enter Associated JobOffer",
                        "Seach Talents",
                        "Manage Talents"
                    }, authUser.getName());

                } while (index <= 0 && index >= 6);

                switch (index) {
                    case 1:
                        selectTalent(ExperienceController.class);

                        waitForKeyEnter();
                        break;
                    case 2:
                        createTalent();
                        break;
                    case 3:
                        clear();
                        
                        listAllTalents();

                        waitForKeyEnter();
                        break;
                    case 4:
                        removeTalent();

                        waitForKeyEnter();                     
                        break;
                    case 5:
                        clear();

                        listAllJobOffers();

                        waitForKeyEnter();

                        if (repeatAction("Do you wanna to engage into a Job Offer")) {
                            engageIntoJobOffer();
                        }

                        waitForKeyEnter();
                    break;
                    case 6:
                        enterInvitedExperience();

                        waitForKeyEnter();
                        break;
                    case 7:
                        enterInvitedJobOffer();

                        waitForKeyEnter();
                        break;
                    case 8:
                        seachTalentsOrder(Comparator.comparing(Talent::getName));

                        waitForKeyEnter();
                        break;
                    case 9:
                        selectTalent(ManageTalentController.class);

                        waitForKeyEnter();
                        break;
                }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();

    }

    private void enterInvitedJobOffer() throws IOException {

        clear();

        Collection<JobOffer> associatedJobOffers = authUser.getJobOffers();

        if (associatedJobOffers.isEmpty()) {
            printInfo("There is no Associated JobOffers yet!");
            return;
        }

        associatedJobOffers.forEach(jobOffer -> {

            try {
                printInfo(jobOffer.toString());
            } catch (IOException e) {
                System.out.println("--- Error ---");
            }

        });

        String jobOfferId = textField("Chose an JobOffer by id");

        try {

            if (jobOfferId.isEmpty()) {
                throw new ExperienceException("Please provide an id");
            }
            
            navigationHelper.navigateTo(routeManager.argumentRoute(
                MemberController.class, 
                Integer.parseInt(jobOfferId)
            ));
            
        } catch (NumberFormatException e) { 
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                enterInvitedExperience();
            }

        }catch (Exception e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                enterInvitedExperience();
            }

        }

    }

    private void enterInvitedExperience() throws IOException {

        clear();

        Collection<Experience> associatedExperiences = authUser.getExperiences();

        if (associatedExperiences.isEmpty()) {
            printInfo("There is no Associated Experiences yet!");
            return;
        }

        associatedExperiences.forEach(experience -> {

            try {
                printInfo(experience.toString());
            } catch (IOException e) {
                System.out.println("--- Error ---");
            }

        });

        String experienceId = textField("Chose an Experience by id");

        try {

            if (experienceId.isEmpty()) {
                throw new ExperienceException("Please provide an id");
            }
            
            navigationHelper.navigateTo(routeManager.argumentRoute(
                JobOfferController.class, 
                Integer.parseInt(experienceId)
            ));
            
        } catch (NumberFormatException e) { 
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                enterInvitedExperience();
            }

        }catch (Exception e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                enterInvitedExperience();
            }

        }

    }

    private void selectTalent(Class<? extends ControllerBase> controller) throws IOException {

        clear();

        if (listAllTalents() == -1)
        {
            return;
        }

        try {
            
            String talentName = textField("Chose one Talent by his ID");

            if (talentName.isEmpty())
                throw new TalentException("Please provide an id");

            navigationHelper.navigateTo(
                routeManager.argumentRoute(
                    controller, 
                    Integer.parseInt(talentName)
            ));


        } catch (NumberFormatException e) {
            
            printError(e.getMessage());
            
            if (repeatAction("Do you wanna repeat?")) {
                selectTalent(controller);
            };

        } catch (Exception e) {
            
            printError(e.getMessage());
            
            if (repeatAction("Do you wanna repeat?")) {
                selectTalent(controller);
            };

        }

    }

    private void createTalent() throws IOException {

        Collection<Skill> selectedSkill = new ArrayList<>();
        Map<ProfArea, Integer> selectedProfArea = new HashMap<>();

        clear();

        try {

            System.out.println("Talent Info:");

            String name = textField("Name");
            String priceHour = textField("PricePerHour (default : 0)");
            String isPublic = textField("Public/Private (t|f)");
            
            Talent talent = talentService.createTalent(new CreateTalentRequest(
                name,
                priceHour.isEmpty() ? 0 : Float.parseFloat(priceHour), 
                isPublic.equals("t") ? true : false 
            ));

            if (!skillService.getAll().isEmpty())
                associateSkill(selectedSkill, talent);

            if (!profAreaService.getAll().isEmpty())
                associateProfArea(selectedProfArea, talent);

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }
 
    private int listAllTalents() throws IOException {

        Collection<Talent> talents = talentService.getAllTalents();

        if (talents.isEmpty()) {
            printInfo("There is no talents yet!");
            return -1;
        }

        for (Talent talent : talents) {
            System.out.println();
            printInfo(talent.toString());
        }

        return 0;

    }

    private void removeTalent() throws IOException {

        clear();

        if (listAllTalents() == -1) {
            return;
        }

        String talentName = textField("Talent Id");

        try {
            
            if (talentName.isEmpty())
                throw new TalentException("Please provide an id");

            talentService.removeTalent(Integer.parseInt(talentName));
        
            printSuccess("Talent was removed!");

        } catch (NumberFormatException e) {

            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                removeTalent();
            }

        } catch (Exception e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                removeTalent();
            }

        }

    }

    private void listAllSkills() throws IOException {
        for (Skill skill : skillService.getAll()) {
            printInfo(skill.toString());
        }
    }

    private void listAllJobOffers() throws IOException {

        Collection<IdentityUser> users = memberService.getAllMembers();

        for (IdentityUser user : users) {
            for (Talent talent : user.getTalents()) {
                for (Experience experience : talent.getExperiences()) {
                    for (JobOffer jobOffer : experience.getJobOffers()) {
                        if (jobOffer.getState() == State.Changed || jobOffer.getState() == State.Available)
                            printInfo(jobOffer.toString());
                    }
                }
            }
        }

    }

    private void engageIntoJobOffer() throws IOException {

        listAllJobOffers();

        String[] jobOffers = comboBox("Chose JobOffer with commas(,)");

        try {
            
            for (int i = 0; i < jobOffers.length; i++) {
        
                if (jobOffers[i].isEmpty() || !jobOffers[i].chars().allMatch(Character::isDigit))
                        throw new JobOfferException("JobOffer not valid!");
                        
                var id = jobOffers[i];
                
                Optional<JobOffer> jobOfferFound = talentService.getJobOfferById(Integer.parseInt(id));

                if (!jobOfferFound.isPresent())
                    throw new JobOfferException("No JobOffer was found!");

                // TODO: Compute date to the invites
                var result = memberService.inviteMember(
                    new InviteMemberRequest(
                        jobOfferFound.get(), 
                        new Date(),
                        talentService.getCreatorJobOffer(Integer.parseInt(jobOffers[i])).getEmail()
                    )
                );

                if (result)
                    printSuccess("You have applied to the jobOffer");
            
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    public void seachTalentsOrder(Comparator<Talent> comparator) throws Exception {

        Collection<Talent> reallyAllTalents = talentService.getAllTalentsPublic();
        Map<Skill,Integer> selectedSkills = new HashMap<>();
        List<Talent> selectedTalents = new ArrayList<>();
        Collection<IdentityUser> users = memberService.getAllMembers();

        clear();

        if (reallyAllTalents.isEmpty()) {
            printInfo("There is no talents yet!");
            return;
        }

        try {

            listAllSkills();

            String[] skillNames = comboBox("Chose Skills by ID separeted by commas(,)");

            for (String name : skillNames) {
                
                if (name.isEmpty())
                    continue;

                clear();

                String necYears = textField("Skill: " + name + "\tInsert years of experience required");

                selectedSkills.put(
                    skillService.getSkillById(Integer.parseInt(name)),
                    necYears.isEmpty() ? 0 : Integer.parseInt(necYears)
                );

            }

            clear();
 
            selectedTalents = ((List<Talent>)talentService.getAllTalentsByOrder(
                Comparator.comparing(Talent::getName), 
                selectedSkills
            ));

            if (selectedTalents.isEmpty()) {
                printInfo("There is no talents with that skills!");
                return;
            }

            selectedTalents.forEach(talent -> {
                
                for (IdentityUser user : users) {
                    if (user.containsTalent(talent.getTalentId())) {
                        try {
                            printInfo(user.toString());
                            System.out.println();
                            printInfo(talent.toString());
                        } catch (Exception e) {
                            System.out.println("--- Error ---");
                        }
                    }
                }
  
            });

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private void associateProfArea(Map<ProfArea, Integer> selectedProfArea, Talent talent) throws IOException, Exception {
        
        clear();

        listAllProfAreas();

        String[] profAreas = comboBox("Chose Professional Areas ID between commas(,): ");

        clear();

        try {

            for (String profAreaName : profAreas) {

                if (profAreaName.isEmpty())
                    continue;

                String expYears = textField("Prof. Area: " + profAreaName + "\tInsert years of experience");

                selectedProfArea.put(
                    profAreaService.getProfAreaById(Integer.parseInt(profAreaName)),
                    expYears.isEmpty() ? 0 : Integer.parseInt(expYears));

            }

            talentService.associateProfAreas(new TalentAssociationProfArea(
                talent,
                selectedProfArea
            ));

        } catch (Exception e) {
            
            printError(e.getMessage());

            if (repeatAction("Do You wanna repeat?")) {
                associateProfArea(selectedProfArea, talent);
            }

        }

    }
    
    private void associateSkill(Collection<Skill> selectedSkill, Talent talent) throws IOException, Exception {
    
        Map<Skill,Integer> selectedSkills = new HashMap<>();
        
        clear();

        listAllSkills();

        String[] skillsNames = comboBox("Chose Skills ID between commas(,) to associate them to the new talent");
        
        clear();

        try {
        
            for (String skillName : skillsNames) {

                if (skillName.isEmpty())
                    continue;

                String expYears = textField("Skill: " + skillName + "\tInsert years of experience");

                selectedSkills.put(
                    skillService.getSkillById(Integer.parseInt(skillName)),
                    expYears.isEmpty() ? 0 : Integer.parseInt(expYears)
                );

            }
            
        } catch (Exception e) {
                printError(e.getMessage());
        }

        talentService.associateSkills(new TalentAssociationSkill(
            talent, 
            selectedSkills
        ));
    
    }

    private void listAllProfAreas() throws IOException {
        for (ProfArea profArea : profAreaService.getAll()) {
            printInfo(profArea.toString());
        }
    }

}
