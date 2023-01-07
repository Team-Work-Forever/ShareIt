package shareit.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import shareit.errors.JobOfferException;
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

    @Override
    public void display() throws IOException {
        
        int index = 0;

        clear();

        do {
            
            try {
                
                do {
                    
                    clear();

                    index = menu("***************** Menu *****************", new String[] {
                        "Select Talent",
                        "Create Talent",
                        "List Talents",
                        "Update Talent",
                        "Remove Talent",
                        "List JobOffers Available"
                    }, authenticationService.getAuthenticatedUser().getName());
                    
                } while (index <= 0 && index >= 6);

                switch (index) {
                    case 1:
                        selectTalent();
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
                        updateTalent();

                        waitForKeyEnter();
                        break;
                    case 5:
                        removeTalent();

                        waitForKeyEnter();                     
                        break;
                    case 6:
                        clear();

                        listAllJobOffers();

                        waitForKeyEnter();

                        if (repitAction("Do you wanna to engage into a Job Offer")) {
                            engageIntoJobOffer();
                        }

                        waitForKeyEnter();
                    break;
                }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();

    }

    private void selectTalent() throws IOException {

        clear();

        if (listAllTalents() == -1)
        {
            return;
        }

        String talentName = textField("Chose one Talent by his name");

        navigationHelper.navigateTo(
            routeManager.argumentRoute(
                ExperienceController.class, 
                talentName
        ));

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
            
            talentService.createTalent(new CreateTalentRequest(
                name, 
                priceHour.isEmpty() ? 0 : Float.parseFloat(priceHour), 
                isPublic == "t" ? true : false 
            ));

            associateSkill(selectedSkill, name);

            associateProfArea(selectedProfArea, name);

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private void associateSkill(Collection<Skill> selectedSkill, String name) throws IOException, Exception {
        
        Map<Skill,Integer> selectedSkills = new HashMap<>();
        
        clear();

        listAllSkills();

        String[] skillsNames = comboBox("Chose Skills with commas(,) to associate with the new talent");
        
        try {
        
            for (String skillName : skillsNames) {

                String expYears = textField("Skill: " + skillName + "\n\tInsert years of experience: ");

                selectedSkills.put(
                    skillService.getSkillByName(skillName),
                    expYears.isEmpty() ? 0 : Integer.parseInt(expYears)
                );

            }
            
        } catch (Exception e) {
                printError(e.getMessage());
        }

        talentService.associateSkills(new TalentAssociationSkill(
            name, 
            selectedSkills
        ));
    
    }

    private void associateProfArea(Map<ProfArea, Integer> selectedProfArea, String name) throws IOException, Exception {
        
        clear();

        listAllProfAreas();

        Map<String,Integer> profAreas = comboBox("Chose Professional Areas with commas(,). And separeted with (:) the years of experience to associate with the new talent", ":");

        for (String profAreaName : profAreas.keySet()) {
            var profArea = profAreaService.getProfAreaByName(profAreaName);

            selectedProfArea.put(profArea, profAreas.get(profAreaName));
        }

        talentService.associateProfAreas(new TalentAssociationProfArea(
            name, 
            selectedProfArea
        ));

    }
 
    private int listAllTalents() throws IOException {

        Collection<Talent> talents = talentService.getAllTalents();

        if (talents.size() <= 0) {
            printInfo("There is no talents yet!");
            return -1;
        }

        for (Talent talent : talents) {
            System.out.println();
            printInfo(talent.toString());
        }

        return 0;

    }
    
    private void updateTalent() throws IOException {

        clear();

        if (listAllTalents() == -1) {
            return;
        }

        String talentName = textField("Talent Name");

        try {

            talentService.removeTalent(talentName);

        } catch (Exception e) {
            
            printError(e.getMessage());

            if (repitAction("Do you wanna repit?")) {
                removeTalent();
            }

        }

    }

    private void removeTalent() throws IOException {

        clear();

        if (listAllTalents() == -1) {
            return;
        }

        String talentName = textField("Talent Name");

        try {

            talentService.removeTalent(talentName);
        
            printSuccess("Talent was removed!");

        } catch (Exception e) {
            
            printError(e.getMessage());

            if (repitAction("Do you wanna repit?")) {
                removeTalent();
            }

        }

    }

    private void listAllSkills() throws IOException {
        for (Skill skill : skillService.getAll()) {
            printInfo(skill.toString());
        }
    }

    private void listAllProfAreas() throws IOException {
        for (ProfArea profArea : profAreaService.getAll()) {
            printInfo(profArea.toString());
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
                
                JobOffer jobOffer = talentService.getJobOfferById(Integer.parseInt(id));

                // TODO: Compute date to the invites
                var result = memberService.inviteMember(
                    new InviteMemberRequest(
                        jobOffer, 
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

}
