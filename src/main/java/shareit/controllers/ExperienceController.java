package shareit.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.printSuccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.experience.CreateExperienceRequest;
import shareit.data.Experience;
import shareit.data.Talent;
import shareit.errors.ExperienceException;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.AuthenticationService;
import shareit.services.TalentService;

@Controller
public class ExperienceController extends ControllerBase {

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TalentService talentService;

    private Talent currentTalent;

    @Override
    public void display() throws IOException {

        int index = 0;
        
        do {
            
            try {

                syncTalent();
                
                do {
                    
                    clear();

                    index = menu("***************** Talent Menu *****************", new String[] {
                        "Select Experience",
                        "Create Experience",
                        "List Experiences",
                        "Update Experience -- Not Implemented",
                        "Remove Experience"
                    }, authenticationService.getAuthenticatedUser().getName());
                    
                } while (index <= 0 && index >= 5);

                switch (index) {

                    case 1:
                        selectExperience();

                        waitForKeyEnter();
                        break;
                    case 2:
                        createExperience();
                        break;
                    case 3:
                        listExperience();

                        waitForKeyEnter();
                        break;
                    case 4:
                        updateExperience();

                        waitForKeyEnter();
                        break;
                    case 5:
                        removeExperience();

                        waitForKeyEnter();
                        break;
                    }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();

    }

    private void selectExperience() throws IOException {

        clear();

        if (listExperience() == -1) {
            return;
        }

        try {
            
            String experienceTitle = textField("Chose one Experience by his name");

            navigationHelper.navigateTo(
                routeManager.argumentRoute(
                    JobOfferController.class, 
                    Integer.parseInt(experienceTitle)
            ));

        } catch (NumberFormatException e) {
           
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                selectExperience();
            }

        } catch (Exception e) {
           
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                selectExperience();
            }

        }

    }

    private int listExperience() throws IOException {

        clear();

        Collection<Experience> experiences = currentTalent.getExperiences();

        if (experiences.size() <= 0) {
            printInfo("There is no Experience yet!");
            return -1;
        }

        try {
            
            for (Experience experience : experiences) {
                printInfo(experience.toString());
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

        return 0;

    }

    private void createExperience() throws Exception {

        clear();

        try {

            System.out.println("Experience Info:");

            String title = textField("Title");
            String name = textField("Name");
            String description = textField("Description");

            talentService.createExperience(new CreateExperienceRequest(
                currentTalent,
                title,
                name,
                description,
                new Date(),
                new Date()
            ));

            syncTalent();
            
        } catch (ExperienceException e) {
            printError(e.getMessage());
        }

    }
    
    // TODO: Acabar update Expereriencia
    private void updateExperience() {

     

    }

    private void removeExperience() throws IOException {

        clear();

        if (listExperience() == -1) {
            return;
        }

        try {

            String experienceId = textField("Experience id");

            if (experienceId.isEmpty())
            {
                throw new ExperienceException("Please provide a valid id");
            }
        
            talentService.removeExperienceById(Integer.parseInt(experienceId));
            
        } catch (Exception e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                removeExperience();
            }

        }

        printSuccess("Experience was removed!");

    }

    private void syncTalent() throws IOException {
        
        currentTalent = talentService.getTalentById((int)routeManager.getArgs());

    }

}
