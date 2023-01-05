package shareit.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.bufferInput;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.printSuccess;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.comboBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.joboffer.AssociateSkillRequest;
import shareit.contracts.joboffer.CreateJobOfferRequest;
import shareit.contracts.member.InviteMemberRequest;
import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.Privilege;
import shareit.data.ProfArea;
import shareit.data.Skill;
import shareit.data.auth.IdentityUser;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.ExperienceService;
import shareit.services.JobOfferService;
import shareit.services.MemberService;
import shareit.services.ProfAreaService;
import shareit.services.SkillService;

@Controller
public class JobOfferController extends ControllerBase {

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private JobOfferService jobOfferService;

    @Autowired
    private ExperienceService experienceService;

    @Autowired
    private ProfAreaService profAreaService;
   
    @Autowired
    private SkillService skillService;

    @Autowired
    private MemberService memberService;

    private Experience currentExperience;
    private String talentName;

    @Override
    public void display() throws IOException {
        
        int index = 0;

        currentExperience = experienceService.getExperienceByTitle(((String[])routeManager.getArgs())[0]);
        talentName = ((String[])routeManager.getArgs())[1];

        do {
            
            try {
                
                do {
                    
                    clear();

                    index = menu("***************** Experience Menu *****************", new String[] {
                        "Select JobOffers",
                        "Create JobOffers",
                        "List JobOffers",
                        "Update JobOffers",
                        "Remove JobOffers",
                        "Envite User"
                    });
                    
                } while (index <= 0 && index >= 5);

                switch (index) {

                    case 1:
                        selectJobOffer();
                        break;
                    case 2:
                        createJobOffer();
                        break;
                    case 3:
                        listJobOffer();

                        waitForKeyEnter();
                        break;
                    case 4:
                        updateJobOffer();
                        break;
                    case 5:
                        removeJobOffer();
                        break;
                    case 6:
                        inviteUser();
                        break;
                    }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();

    }
    
    private void inviteUser() throws Exception {

        clear();

        listAllMembers();

        String[] emails = comboBox("Chose Member separeted by commas(,) to invite members");

        try {
            
            for (String email : emails) {
            
                clear();

                printSuccess("Email: " + email);
    
                memberService.inviteMember(new InviteMemberRequest(
                    currentExperience, 
                    new Date(), // Expire Date
                    email,
                    Privilege.Manager
                ));
    
                printSuccess("Invite send with success to user with email "+ email);
    
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private void listAllMembers() throws IOException {
        for (IdentityUser user : memberService.getAllMembers()) {
            printInfo(user.toString());
        }
    }

    private void selectJobOffer() throws IOException {

        clear();

        listJobOffer();

        printInfo("Chose one JobOffer by his id");
        String jobOfferId = bufferInput.readLine();

        if (jobOfferId.isEmpty())
            return;

        navigationHelper.navigateTo(
            routeManager.argumentRoute(
                MemberController.class, 
                Integer.parseInt(jobOfferId)
        ));

    }

    private void createJobOffer() throws IOException {

        Map<Skill,Integer> selectedSkills = new HashMap<>();

        clear();

        try {

            listAllProfAreas();
            
            printInfo("Chose one Professional Area by his name");
            String profAreaName = bufferInput.readLine();

            listAllSkills();
        
            String[] skillNames = comboBox("Chose Skills by name separeted by commas(,)");

            try {
                    
                for (String name : skillNames) {
                    printInfo("Skill: " + name + " Insert years of experience required: ");
                    String expYears = bufferInput.readLine();

                    selectedSkills.put(
                        skillService.getSkillByName(name),
                        expYears.isEmpty() ? 0 : Integer.parseInt(expYears)
                    );

                }

            } catch (Exception e) {
                printError(e.getMessage());
            }

            clear();

            System.out.println("JobOffer Info:");

            String name = textField("Name");
            String qtyHours = textField("QtyHours (default : 0)");
            String description = textField("Description (default)");

            var jobOffer = jobOfferService.createJobOffer(new CreateJobOfferRequest(
                talentName, 
                currentExperience.getTitle(),
                name,
                qtyHours.isEmpty() ? 0 : Integer.parseInt(qtyHours),
                description,
                profAreaService.getProfAreaByName(profAreaName)
            ));

            jobOfferService.associateSkill(new AssociateSkillRequest(
                jobOffer.getJobOfferId(),
                selectedSkills
            ));

            printSuccess("The skills are associated!");
            
        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private void listJobOffer() throws IOException {

        clear();

        try {
            
            for (JobOffer jobOffer : currentExperience.getJobOffers()) {
                printInfo(jobOffer.toString());
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    // TODO: fazer mais tarde
    private void updateJobOffer() {


    }

    // TODO: fazer mais tarde
    private void removeJobOffer() {



    }

    private void listAllProfAreas() throws IOException {
        for (ProfArea profArea : profAreaService.getAll()) {
            printInfo(profArea.toString());
        }
    }

    private void listAllSkills() throws IOException {
        for (Skill skill : skillService.getAll()) {
            printInfo(skill.toStringJobOffer());
        }
    }
    
}
