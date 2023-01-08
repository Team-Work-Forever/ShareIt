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
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.Authentication;
import shareit.services.MemberService;
import shareit.services.TalentService;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.printInfo;

@Controller
public class AdminController extends ControllerBase {

    @Autowired
    private TalentService talentService;

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
                        "Professinal Area Menu",
                        "Generate Report"
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
                    case 10:
                        authorize(Role.ADMIN);
                        generateReport();
                        break;
                }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateTo(routeManager.authRoute());

    }

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

    private int listAllJobOffers() throws IOException {

        clear();

        Collection<IdentityUser> users = memberService.getAllMembers();

        for (IdentityUser user : users) {
            for (Talent talent : user.getTalents()) {
                for (Experience experience : talent.getExperiences()) {

                    if (experience.getJobOffers().isEmpty()) {
                        printInfo("There is no job offer yet!");
                        return -1;
                    }

                    for (JobOffer jobOffer : experience.getJobOffers()) {
                        printInfo(jobOffer.toString());
                    }
                }
            }
        }

        return 0;

    }

    private int listAllUsers() throws IOException {

        clear();

        Collection<IdentityUser> users = memberService.getAllMembers();

        for (IdentityUser user : users) {
            printInfo(user.toString());
        }

        return 0;

    }
    

    private void navigateToDashBoard() throws IOException {
        navigationHelper.navigateTo(DashBoardController.class);
    }

    private void skillMenu() throws IOException {
        navigationHelper.navigateTo(SkillController.class);
    }

    private void profAreaMenu() throws IOException {
        navigationHelper.navigateTo(ProfAreaController.class);
    }

    private void generateReport() throws IOException {
        navigationHelper.navigateTo(ReportController.class);
    }
    
}
