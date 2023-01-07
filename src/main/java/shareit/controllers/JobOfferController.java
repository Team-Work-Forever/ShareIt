package shareit.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.textField;
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
import shareit.errors.ExperienceNotAllowed;
import shareit.errors.InviteNotFoundException;
import shareit.errors.InviteNotValidException;
import shareit.errors.JobOfferException;
import shareit.errors.ProfAreaException;
import shareit.helper.Invitation;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.Authentication;
import shareit.services.ExperienceService;
import shareit.services.InviteService;
import shareit.services.JobOfferService;
import shareit.services.MemberService;
import shareit.services.ProfAreaService;
import shareit.services.SkillService;
import shareit.services.TalentService;

@Controller
public class JobOfferController extends ControllerBase {

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private JobOfferService jobOfferService;

    @Autowired
    private Authentication authenticationService;

    @Autowired
    private ProfAreaService profAreaService;
   
    @Autowired
    private SkillService skillService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TalentService talentService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private ExperienceService experienceService;

    private Experience currentExperience;

    // TODO: Remove Experience Service
    @Override
    public void display() throws IOException {
        
        int index = 0;
        
        do {
            
            try {
                
                currentExperience = talentService.getExperienceById(((int)routeManager.getArgs()));

                IdentityUser authenticatedUser = authenticationService.getAuthenticatedUser();

                do {
                    
                    clear();

                    index = menu("***************** Experience Menu *****************", new String[] {
                        "Select JobOffers",
                        "Create JobOffers",
                        "List JobOffers",
                        "Update JobOffers",
                        "Remove JobOffers",
                        "Envite User",
                        "List All Members",
                        "See Applications"
                    }, authenticatedUser.getName());
                    
                } while (index <= 0 && index >= 5);

                switch (index) {

                    case 1:
                        selectJobOffer();

                        waitForKeyEnter();
                        break;
                    case 2:

                        authorizeAction(authenticatedUser);

                        createJobOffer();
                        break;
                    case 3:
                        listJobOffer();

                        waitForKeyEnter();
                        break;
                    case 4:

                        authorizeAction(authenticatedUser);

                        updateJobOffer();

                        waitForKeyEnter();
                        break;
                    case 5:

                        authorizeAction(authenticatedUser);

                        removeJobOffer();

                        waitForKeyEnter();
                        break;
                    case 6:

                        authorizeAction(authenticatedUser);

                        inviteUser();

                        waitForKeyEnter();
                        break;
                    case 7:
                        listMembers();

                        waitForKeyEnter();
                        break;
                    case 8:

                        authorizeAction(authenticatedUser);

                        manageApplications();

                        waitForKeyEnter();
                        break;
                    }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();

    }
    
    private int listMembers() throws IOException {

        clear();

        Collection<IdentityUser> allClients = currentExperience.getAllClients();

        if (allClients.isEmpty()) {
            printInfo("There is no Clients in this Experience!");
            return -1;
        }

        allClients.forEach(client -> {
            
            try {
                printInfo(client.toString());
            } catch (IOException e) {
                System.out.println("--- Error ---");
            }

        });

        return 0;

    }

    private void manageApplications() throws IOException {

        clear();

        Collection<Invitation> inviteInBox = memberService.getInviteInBox(authenticationService.getAuthenticatedUser().getEmail());;

        if (inviteInBox.size() <= 0) {
            printInfo("There is no Applications!");
            return;
        }

        inviteInBox.forEach((invite) -> {
                
            try {
                printInfo(invite.toString());
            } catch (IOException e) {
                System.out.println("--- Error ---");
            }

        });

        try {

            String[] invites = comboBox("Chose Invite by id separeted by commas(,)");

            for (int i = 0; i < invites.length; i++) {
                
                if (invites[i].isEmpty() || !invites[i].chars().allMatch(Character::isDigit))
                    throw new InviteNotValidException();

                Optional<Invitation> invite = inviteService.getInviteById(Integer.parseInt(invites[i]));

                if (!invite.isPresent())
                    throw new InviteNotFoundException();

                var result = memberService.acceptInvite(
                    invite.get()
                );

                if (result)
                    printSuccess("New Client Accepted!");
                    
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }


    }

    private void inviteUser() throws Exception {

        clear();

        Collection<IdentityUser> possibleMembers = memberService.getPossibleMembers();

        if (possibleMembers.isEmpty()) {
            printInfo("There is no other members");
            return;
        }

        String[] emails = comboBox("Chose Member separeted by commas(,) to invite members");

        try {
            
            for (String email : emails) {
            
                clear();

                memberService.inviteMember(new InviteMemberRequest(
                    currentExperience, 
                    new Date(), // Expire Date
                    email,
                    Privilege.WORKER
                ));
    
                printSuccess("Invite send with success to user with email "+ email);
    
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private void selectJobOffer() throws IOException {

        clear();

        if (listJobOffer() == -1) {
            return;
        }

        try {

            String jobOfferId = textField("Chose one JobOffer by his id");

            if (jobOfferId.isEmpty())
                return;
                
            navigationHelper.navigateTo(
            routeManager.argumentRoute(
                MemberController.class, 
                Integer.parseInt(jobOfferId)
            ));

        } catch (NumberFormatException e) {

            printError("Please provide a valid number!");

            if (repeatAction("Do you wanna repeat?")) {
                selectJobOffer();
            }

        } catch (Exception e) {

            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                selectJobOffer();
            }

        }

    }

    private void createJobOffer() throws IOException {

        Map<Skill,Integer> selectedSkills = new HashMap<>();

        clear();

        try {

            clear();

            listAllProfAreas();
            
            String profAreaName = textField("Chose one Professional Area by his ID");

            if (profAreaName.isEmpty()) 
                throw new ProfAreaException("Please provide an ID");

            if (profAreaService.getProfAreaById(Integer.parseInt(profAreaName)) == null) {
                throw new ProfAreaException("Please provide a valid ProfArea");
            }

            clear();

            listAllSkills();
        
            String[] skillNames = comboBox("Chose Skills by ID separeted by commas(,)");

            clear();

            try {
                    
                for (String name : skillNames) {
                    
                    if (name.isEmpty())
                        continue;

                    String expYears = textField("Skill: " + name + "\tInsert years of experience required");

                    selectedSkills.put(
                        skillService.getSkillById(Integer.parseInt(name)),
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
                currentExperience,
                name,
                qtyHours.isEmpty() ? 0 : Integer.parseInt(qtyHours),
                description,
                profAreaService.getProfAreaById(Integer.parseInt(profAreaName))
            ));

            jobOfferService.associateSkill(new AssociateSkillRequest(
                jobOffer.getJobOfferId(),
                selectedSkills
            ));

            printSuccess("The skills are associated!");
            
        } catch (NumberFormatException e) {
            printError(e.getMessage());
        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private int listJobOffer() throws IOException {

        clear();

        Collection<JobOffer> jobOffers = currentExperience.getJobOffers();

        if (jobOffers.size() <= 0) {
            printInfo("There is no JobOffer available");
            return -1;
        }

        try {
            
            for (JobOffer jobOffer : jobOffers) {
                printInfo(jobOffer.toString());
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

        return 0;

    }

    // TODO: Update JobOffer
    private void updateJobOffer() {


    }

    private void removeJobOffer() throws IOException {

        clear();

        if (listJobOffer() == -1) {
            return;
        }

        String jobOfferId = textField("Job Offer Id");

        if (jobOfferId.isEmpty()) {
            printInfo("Please provide an id!");

            if (!repeatAction("Do you wanna exit?")) {
                removeJobOffer();
            }

        }

        try {
            experienceService.removeJobOfferById(
                Integer.parseInt(jobOfferId)
            );
        } catch (JobOfferException e) {
            printError(e.getMessage());
        }

        printSuccess("Job Offer was removed!");

        waitForKeyEnter();

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
    
    private void authorizeAction(IdentityUser client) {
        
        if (currentExperience.isWorker(client.getEmail())) {
            throw new ExperienceNotAllowed();
        }

    }

}
