package shareit.controllers;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.profArea.CreateProfAreaRequest;
import shareit.data.ProfArea;
import shareit.errors.ProfAreaException;
import shareit.helper.NavigationHelper;
import shareit.services.AuthenticationService;
import shareit.services.ProfAreaService;

import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.comboBox;

@Controller
public class ProfAreaController extends ControllerBase {

    @Autowired
    private ProfAreaService profAreaService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private NavigationHelper navigationHelper;

    @Override
    public void display() throws IOException {
       
        int index = 0;

            do {
                try {
                    do {
                
                        clear();
        
                        index = menu("***************** Professional Area Menu *****************", new String[] { 
                            "Create Professional Area", 
                            "List All Professional Areas",
                            "Update Professional Area",
                            "Remove Professional Area",
                        }, authenticationService.getAuthenticatedUser().getName());
        
                    } while (index <= 0 && index >= 4);

                switch (index) {
                    case 1:
                        createProfArea();
                    break;
                    case 2:
                        listAllProfAreas();

                        waitForKeyEnter();
                    break;
                    case 3:
                        updateProfArea();

                        waitForKeyEnter();
                    break;
                    case 4:
                        removeProfArea();

                        waitForKeyEnter();
                    break;
                } 
        
            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();

}

    private void createProfArea() throws IOException {

        clear();

        try {

            System.out.println("Professional Area Info:");

            String name = textField("Name");
            String description = textField("Description");

            profAreaService.createProfArea(new CreateProfAreaRequest(
                name, 
                description
            ));

        } catch (ProfAreaException e) {
            printError(e.getMessage());

            if (repeatAction("Do wanna exit creation?")) {
                createProfArea();
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private void listAllProfAreas() throws IOException {

        Collection<ProfArea> profAreas = profAreaService.getAll();

        if (profAreas.isEmpty())
        {
            printInfo("There is no professional areas yet!");
            return;
        }

        for (ProfArea profArea : profAreas) {
            System.out.println();
            printInfo(profArea.toString());
        }

        System.out.println();
            

    }

    private void updateProfArea() throws Exception {

        clear();

        listAllProfAreas();

        String[] profAreas = comboBox("Chose the ID seperated by commas (,)");

        for (String profAreaName : profAreas) {

            if (profAreaName.isEmpty())
                continue;

            int profAreaId = Integer.parseInt(profAreaName);

            try {

                var profArea = profAreaService.getProfAreaById(profAreaId);

                System.out.println("Update Data: ");

                String name = textField("Professional Area Name (default : same)");
                String desc = textField("Professional Area Description (default : same)");

                ProfArea newProfArea = new ProfArea(
                    name.isEmpty() ? profArea.getName() : name, 
                    desc.isEmpty() ? profArea.getDescription() : desc
                );
            
                profAreaService.updateProfArea(newProfArea, profAreaId);
            } catch (ProfAreaException e) {
                printError(e.getMessage());
            }

        }

      waitForKeyEnter();

    }

    private void removeProfArea() throws Exception {

        clear();

        listAllProfAreas();
        
        String[] skills = comboBox("Chose the ID seperated by commas");

        for (String id : skills) {

            try {
                profAreaService.removeProfArea(Integer.parseInt(id));
            } catch (ProfAreaException e) {
                printError(e.getMessage());
            }

        }

      waitForKeyEnter();

    }
    
}
