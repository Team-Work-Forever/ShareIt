package shareit.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;

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
import shareit.utils.DatePattern;

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

                if (currentTalent == null) {
                    syncTalent();
                }
                
                do {
                    
                    clear();

                    index = menu("***************** Talent Menu *****************", new String[] {
                        "Select Experience",
                        "Create Experience",
                        "List Experiences",
                        "Update Experience -- Not Implemented",
                        "Remove Experience"
                    }, authenticationService.getAuthenticatedUser().getName());
                    
                } while (index <= 0 && index >= 6);

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
            
            String experienceID = textField("Chose one Experience by his ID");

            navigationHelper.navigateTo(
                routeManager.argumentRoute(
                    JobOfferController.class, 
                    Integer.parseInt(experienceID)
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

        if (experiences.isEmpty()) {
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

            String title = textField("Title (default)");
            String name = textField("Name (default)");
            String description = textField("Description (default)");
            String startDate = textField("Start Date (default/today:(dd-MM-yyyy))");
            String finalDate = textField("Final Date (default/today:(dd-MM-yyyy))");

            talentService.createExperience(new CreateExperienceRequest(
                currentTalent,
                title.isEmpty() ? "" : title,
                name.isEmpty() ? "" : name,
                description.isEmpty() ? "" : description,
                startDate.isEmpty() ? LocalDate.now() : DatePattern.insertDate(startDate),
                finalDate.isEmpty() ? LocalDate.now() : DatePattern.insertDate(finalDate)
            ));

            syncTalent();
            
        } catch (ExperienceException e) {
            printError(e.getMessage());
        }

    }
    
    private void updateExperience() throws IOException {

        clear();

        if (listExperience() == -1) {
            return;
        }

        listExperience();

        String experienceNameTemp = textField("Chose one Experience by his ID");

        if (experienceNameTemp.isEmpty())
            throw new ExperienceException("Please provide an id!");

        clear();

        String experienceTitle = textField("Experience Title (default : same)");
        String experienceName = textField("Experience Name (default : same)");
        String description = textField("Description (default : same)");
        String startDate = textField("Start Date (default : same/(dd-MM-yyyy))");
        String finalDate = textField("Finish Date (default : same/(dd-MM-yyyy))");

        try {

            var experience = talentService.getExperienceById(Integer.parseInt(experienceNameTemp));

            talentService.updateExperience(
                new CreateExperienceRequest(
                    currentTalent,
                    experienceTitle.isEmpty() ? experience.getTitle() : experienceTitle, 
                    experienceName.isEmpty() ? experience.getName() : experienceName, 
                    description.isEmpty() ? experience.getDesc() : description, 
                    startDate.isEmpty() ? experience.getStartDate() : DatePattern.insertDate(startDate), 
                    finalDate.isEmpty() ? experience.getFinalDate() : DatePattern.insertDate(finalDate)
                ),
                Integer.parseInt(experienceNameTemp)
            );

        } catch (NumberFormatException e) {
            exitDisplay(e.getMessage());
        } catch (Exception e) {
            exitDisplay(e.getMessage());
        }

    }

    private void exitDisplay(String msg) throws IOException {
        printError(msg);

        if (repeatAction("Do you wanna repeat?")) {
            updateExperience();
        }
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
