package shareit.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.bufferInput;
import static shareit.utils.ScreenUtils.comboBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.talent.TalentAssociationSkill;
import shareit.contracts.talent.CreateTalentRequest;
import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.ProfArea;
import shareit.data.Skill;
import shareit.data.State;
import shareit.data.Talent;
import shareit.data.auth.IdentityUser;
import shareit.errors.TalentException;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
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
                    });
                    
                } while (index <= 0 && index >= 6);

                switch (index) {
                    case 1:
                        selectTalent();
                        break;
                    case 2:
                        createTalent();
                        break;
                    case 3:
                        listAllTalents();

                        waitForKeyEnter();
                        break;
                    case 4:
                        updateTalent();
                        break;
                    case 5:
                        removeTalent();                        
                        break;
                    case 6:
                        listAllJobOffers();

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

        listAllTalents();

        printInfo("Chose one Talent by his name");
        String talentName = bufferInput.readLine();

        navigationHelper.navigateTo(
            routeManager.argumentRoute(
                ExperienceController.class, 
                talentName
        ));

    }

    private void createTalent() throws IOException {

        Collection<Skill> selectedSkill = new ArrayList<>();
        Collection<ProfArea> selectedProfArea = new ArrayList<>();

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

            clear();

            listAllSkills();

            String[] skillsNames = comboBox("Chose Skills with commas(,) to associate with the new talent");

            for (String skillName : skillsNames) {

                var skill = skillService.getSkillByName(skillName);

                selectedSkill.add(skill);

            }

            talentService.associateSkills(new TalentAssociationSkill(
                name, 
                selectedSkill, 
                10
            ));

            clear();

            listAllProfAreas();

            String[] profAreas = comboBox("Chose Profissional Areas with commas(,) to associate with the new talent");

            for (String profAreaName : profAreas) {
                var profArea = profAreaService.getProfAreaByName(profAreaName);

                selectedProfArea.add(profArea);
            }

            // talentService.associateProfAreas(new TalentAssociationProfArea(
            //     name, 
            //     selectedProfArea, 
            //     0
            // ));

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }
 
    private void listAllTalents() throws IOException {

        int i = 1;

        for (Talent talent : talentService.getAllTalents()) {
            System.out.println();
            printInfo(i  + " - " + talent.toString());
            i++;
        }

    }
    
    private void updateTalent() {

        

    }

    private void removeTalent() throws IOException {

        clear();

        String talentName = textField("Talent Name:");

        try {
            talentService.removeTalent(talentName);
        } catch (TalentException e) {
            printError(e.getMessage());
        }

        printInfo("Talent was removed!");

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

    private void listAllJobOffers() {

        clear();

        Collection<IdentityUser> users = memberService.getAllMembers();

        for (IdentityUser user : users) {
            for (Talent talent : user.getTalents()) {
                for (Experience experience : talent.getExperiences()) {
                    for (JobOffer jobOffer : experience.getJobOffers()) {
                    
                        if (jobOffer.getState() == State.Changed || jobOffer.getState() == State.Available)
                            jobOffer.toString();
                    
                    }
                }
            }
        }

    }

}
