package shareit.controllers;

import java.io.IOException;
import java.util.Date;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.bufferInput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.experience.CreateExperienceRequest;
import shareit.data.Experience;
import shareit.data.Talent;
import shareit.errors.ExperienceException;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.TalentService;

@Controller
public class ExperienceController extends ControllerBase {

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private TalentService talentService;

    private Talent currentTalent;

    @Override
    public void display() throws IOException {

        int index = 0;

        syncTalent();
        
        do {
            
            try {
                
                do {
                    
                    clear();

                    index = menu("***************** Talent Menu *****************", new String[] {
                        "Select Experience",
                        "Create Experience",
                        "List Experiences",
                        "Update Experience",
                        "Remove Experience"
                    });
                    
                } while (index <= 0 && index >= 5);

                switch (index) {

                    case 1:
                        selectExperience();
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
                        break;
                    case 5:
                        removeExperience();
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

        listExperience();

        printInfo("Chose one Experience by his name");
        String experienceTitle = bufferInput.readLine();

        navigationHelper.navigateTo(
            routeManager.argumentRoute(
                JobOfferController.class, 
                new String[] { experienceTitle, currentTalent.getName() }
        ));

    }

    private void listExperience() throws IOException {

        clear();

        try {
            
            for (Experience experience : currentTalent.getExperiences()) {
                printInfo(experience.toString());
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private void createExperience() throws Exception {

        clear();

        try {

            System.out.println("Experience Info:");

            String title = textField("Title");
            String name = textField("Name");
            String description = textField("Description");

            talentService.createExperience(new CreateExperienceRequest(
                currentTalent.getName(), 
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

    // TODO: Acabar remove Expereriencia
    private void removeExperience() {



    }

    private void syncTalent() {
        currentTalent = talentService.getTalentByName((String)routeManager.getArgs());
    }

}
