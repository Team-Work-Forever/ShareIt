package shareit.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.printSuccess;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.comboBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.Privilege;
import shareit.data.auth.IdentityUser;
import shareit.errors.ExperienceException;
import shareit.errors.JobOfferException;
import shareit.errors.auth.IdentityException;
import shareit.helper.RouteManager;
import shareit.helper.NavigationHelper;
import shareit.services.Authentication;
import shareit.services.MemberService;
import shareit.services.TalentService;


@Controller
public class ManageClientsController extends ControllerBase {

    @Autowired
    private Authentication authenticationService;

    @Autowired
    private NavigationHelper navigationHelper;
    
    @Autowired
    private RouteManager routeManager;
    
    @Autowired
    private MemberService memberService;

    @Autowired
    private TalentService talentService;

    private Experience currentExperience;

    @Override
    public void display() throws IOException {
        
        int index = 0;

        do {
            
            try {

                IdentityUser authUser = authenticationService.getAuthenticatedUser();
                currentExperience = ((Experience)routeManager.getArgs());

                do {
                    
                    clear();

                    index = menu("***************** Manage Clients Menu *****************", new String[] {
                        "List All Members",
                        "Alter Privilege",
                        "Remove Member",
                        "Add Client From Job Offer"
                    }, authUser.getName());
                    
                } while (index <= 0 && index >= 8);

                switch (index) {
                    case 1:
                        listMembers();

                        waitForKeyEnter();
                        break;
                    case 2:
                        alterPrivilege();

                        waitForKeyEnter();
                        break;
                    case 3:
                        removeMember();

                        waitForKeyEnter();
                        break;
                    case 4:

                        joinMemberFromJobOffer();

                        waitForKeyEnter();
                        break;
                }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();

    }

    private void joinMemberFromJobOffer() throws IOException {

        clear();

        if (listAllJobOffers() == -1) {
            return;
        }

        try {
            
            String jobOfferID = textField("Chose one Job Offer by his ID");

            if (jobOfferID.isEmpty()) {
                throw new JobOfferException("Please provide an Job OfferID");
            }
    
            Optional<JobOffer> jobOfferFound = currentExperience.getJobOfferById(Integer.parseInt(jobOfferID));

            if (!jobOfferFound.isPresent()) {
                throw new JobOfferException("There is no Job Offer with that id!");
            }

            clear();

            jobOfferFound.get().getClients().forEach(client -> {

                try {
                    printInfo(client.toString());
                } catch (Exception e) {
                    System.out.println("-- Error --");
                }

            });

            String[] emails = comboBox("Please provide the emails from users you want to add!");

            for (String email : emails) {
                
                if (email.isEmpty()) {
                    throw new IdentityException("Please provide an email");
                }

                try {
                    talentService.moveClientFromExperienceToJobOffer(
                        currentExperience,
                        jobOfferFound.get(), 
                        memberService.getMemberByEmail(email)
                    );
                } catch (ExperienceException e) {
                    printError(e.getMessage());
                    waitForKeyEnter();
                }

            }

            printSuccess("Member(s) added!");

        } catch (NumberFormatException e) {
           
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                joinMemberFromJobOffer();
            }

        } catch (Exception e) {
           
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                joinMemberFromJobOffer();
            }

        }

    }

    private int listAllJobOffers() throws IOException {
        
        clear();

        Collection<JobOffer> jobOffers = currentExperience.getJobOffers();

        if (jobOffers.isEmpty()) {
            printInfo("There is no Job Offer yet!");
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
                System.out.println("\t Privilege: " + currentExperience.getPrivilegeOfClient(client.getEmail()));
            } catch (IOException e) {
                System.out.println("--- Error ---");
            }

        });

        return 0;

    }

    private int listAllPossibleMembers() throws IOException {

        clear();

        Collection<IdentityUser> allClients = currentExperience.getAllClients()
                .stream()
                    .filter(member -> !member
                    .getEmail().equals(authenticationService.getAuthenticatedUser().getEmail()))
                    .toList();

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

	private void removeMember() throws IOException {
        
        String email;

        clear();

        if (listAllPossibleMembers() == -1)
        {
            return;
        }

        email = textField("Chose a Member by his email");

        if (email.isEmpty()) {
            printError("Please provide an email");
            return;
        }

        IdentityUser client = memberService.getMemberByEmail(email);

        try {

            talentService.removeClientFromExperience(client, currentExperience);

            printSuccess("Client was removed!");

        } catch (Exception e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                removeMember();
            }

        }

    }

    private void alterPrivilege() throws IOException {

        Privilege privilege;

        clear();
        
        if (listAllPossibleMembers() == -1) {
            return;
        }

        String email = textField("Chose an user by his email");
        String role = textField("Chose an role ( MANAGER(1) | WORKER(2) )");

        try {
            
            if (role.isEmpty())
                throw new ExperienceException("Please provide an role");

            int roleId = Integer.parseInt(role);

            if (roleId < 1 && roleId > 2) {
                throw new ExperienceException("Please provide the roles available");
            }

            switch (roleId) {
                case 1:
                    privilege = Privilege.MANAGER;
                    break;
                default:
                    privilege = Privilege.WORKER;
                    break;
            }

            IdentityUser client = memberService.getMemberByEmail(email);

            talentService.experienceAlterPrivilege(currentExperience, privilege, client);

        } catch (NumberFormatException e) {
            
            exitError(e.getMessage());

        } catch (Exception e) {
            
            exitError(e.getMessage());

        }
    }

    private void exitError(String value) throws IOException {

        printError(value);

        if (repeatAction("Do you wanna repeat?")) {
            alterPrivilege();
        }

    }
} 
    
    

