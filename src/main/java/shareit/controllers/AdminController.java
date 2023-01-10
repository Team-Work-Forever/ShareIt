package shareit.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.Talent;
import shareit.data.auth.IdentityUser;
import shareit.data.auth.Role;
import shareit.errors.auth.AuthenticationException;
import shareit.errors.auth.IdentityException;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.Authentication;
import shareit.services.MemberService;
import shareit.services.TalentService;
import shareit.services.JobOfferService;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.textField;

@Controller
public class AdminController extends ControllerBase {

    @Autowired
    private TalentService talentService;
    
    @Autowired
    private JobOfferService jobOfferService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private Authentication authenticationService;
    
    @Override
    public void display() throws IOException {
        
        int index = 0;

        do {
            
            try {

              IdentityUser authUser = authenticationService.getAuthenticatedUser();

                do {
                    
                    clear();

                    index = menu("***************** Boss Menu *****************", new String[] {
                        "Go to DashBoard",
                        "List All Talents",
                        "List All Experiences",
                        "List All Job Offers",
                        "List All Users",
                        "Skill Menu",
                        "Professional Area Menu",
                        "Generate Report",
                        "Alter Privilege"
                    }, authUser.getName());
                    
                } while (index <= 0 && index >= 8);

                switch (index) {
                    case 0:
                        logout();
                        break;
                    case 1:
                        navigateToDashBoard();
                        
                        break;
                    case 2:
                        listAllTalents();

                        waitForKeyEnter();
                        break;
                    case 3:
                        listAllExperiences();

                        waitForKeyEnter();
                        break;
                    case 4:
                        listAllJobOffers();
                    
                        waitForKeyEnter();
                        break;
                    case 5:
                        listAllUsers();

                        waitForKeyEnter();
                        break;
                    case 6:
                        authorize(Role.ADMIN);
                        skillMenu();
                    break;
                    case 7:
                        authorize(Role.ADMIN);
                        profAreaMenu();
                    break;
                    case 8:
                        authorize(Role.ADMIN);
                        generateReport();
                        break;
                    case 9:
                        authorize(Role.ADMIN);
                        alterPrivilege();
                        break;
                }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateTo(routeManager.authRoute());

    }

    // Case 1
    private void navigateToDashBoard() throws IOException {
        navigationHelper.navigateTo(DashBoardController.class);
    }


    // Case 2
    private int listAllTalents() throws IOException {

        clear();

        Collection<Talent> talents = talentService.getReallyAllTalents();

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

    // Case 3
    private int listAllExperiences() throws IOException {

        clear();

        Collection<Talent> talents = talentService.getReallyAllTalents();
        Collection<Experience> experiences = new ArrayList<>();

        for (Talent talent : talents) {
            for (Experience experience : talent.getExperiences())
                experiences.add(experience);
        }

        if (experiences.isEmpty()) {
            printInfo("There is no experiences yet!");
            return -1;
        }

        for (Experience experience : experiences) {
            System.out.println();
            printInfo(experience.toString());
        }

        return 0;

    }

    // Case 4
    private int listAllJobOffers() throws IOException {

        clear();

        Collection<JobOffer> jobOffers = jobOfferService.getAllJobOffers();

        if (jobOffers.isEmpty()) {
            printInfo("There is no job offer yet!");
            return -1;
        }

        for (JobOffer jobOffer : jobOffers) {
            printInfo(jobOffer.toString());
        }

        return 0;

    }

    // Case 5
    private int listAllUsers() throws IOException {

        clear();

        Collection<IdentityUser> users = memberService.getAllMembers();

        for (IdentityUser user : users) {
            printInfo(user.toString());
        }

        return 0;

    }

    // Case 6
    private void skillMenu() throws IOException {
        navigationHelper.navigateTo(SkillController.class);
    }

    // Case 7
    private void profAreaMenu() throws IOException {
        navigationHelper.navigateTo(ProfAreaController.class);
    }

    // Case 8
    private void generateReport() throws IOException {
        navigationHelper.navigateTo(ReportController.class);
    }

    // Case 9
    private void alterPrivilege() throws IOException {

        clear();

        if (listAllUsers() == -1) {
            return;
        }

        String email = textField("Chose an user by his email");
        String role = textField("Chose an role ( USER(1) | COMPANY/INSTITUTION(2) | MANAGER(3) | ADMIN(4) )");

        try {
            
            if (role.isEmpty())
                throw new AuthenticationException("Please provide an Role");

            int roleId = Integer.parseInt(role);

            if (roleId < 1 && roleId > 4) {
                throw new AuthenticationException("Please provide represented role");
            }

            switch (roleId) {
                case 1:
                    role = Role.USER;
                    break;
                case 2:
                    role = Role.COMPANYINSTITUTION;
                    break;
                case 3:
                    role = Role.USERMANAGER;
                    break;
                case 4:
                    role = Role.ADMIN;
                    break;
            }

            IdentityUser user = memberService.getMemberByEmail(email);

            authenticationService.alterPrivilege(role, user);

        } catch (IdentityException e) {
            
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
