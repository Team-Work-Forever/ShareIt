package shareit.controllers;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.profArea.CreateProfAreaRequest;
import shareit.data.ProfArea;
import shareit.errors.ProfAreaException;
import shareit.helper.NavigationHelper;
import shareit.services.ProfAreaService;

import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.bufferInput;

@Controller
public class ProfAreaController extends ControllerBase {

    @Autowired
    private ProfAreaService profAreaService;

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
                        });
        
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
                    break;
                    case 4:
                        removeProfArea();
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
            String qtyProf = textField("QtyProf (default: 0)");
            
            profAreaService.createProfArea(new CreateProfAreaRequest(
                name, 
                description, 
                qtyProf.isEmpty() ? 0 : Integer.parseInt(qtyProf)
            ));

        } catch (ProfAreaException e) {
            printError(e.getMessage());

            if (repitAction("Do wanna repit?")) {
                createProfArea();
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private void listAllProfAreas() throws IOException {

        int i = 1;

        Collection<ProfArea> profAreas = profAreaService.getAll();

        if (profAreas.isEmpty())
        {
            printInfo("There is no professional areas yet!");
            return;
        }

        for (ProfArea profArea : profAreas) {
            System.out.println();
            printInfo(i  + " - " + profArea.toString());
            i++;
        }

        System.out.println();
            

    }

    private void updateProfArea() throws Exception {

        clear();

        listAllProfAreas();

        printInfo("Chose the name seperated by commas");

        var output = bufferInput.readLine();
        String[] profAreas = output.split(",");

        for (String profAreaName : profAreas) {

            try {

                var profArea = profAreaService.getProfAreaByName(profAreaName);

                System.out.println("Update Data: ");

                String name = textField("Professional Area Name: (default : same)");
                String desc = textField("Professional Area Description: (default : same)");
                String qtyProf = textField("Professional Area QtyProf (default : 0) ");

                ProfArea newProfArea = new ProfArea(
                    name.isEmpty() ? profArea.getName() : name, 
                    desc.isEmpty() ? profArea.getDescription() : desc, 
                    qtyProf.isEmpty() ? 0 : Integer.parseInt(qtyProf)
                );

            
                profAreaService.updateProfArea(newProfArea, profAreaName);
            } catch (ProfAreaException e) {
                printError(e.getMessage());
            }

        }

      waitForKeyEnter();

    }

    private void removeProfArea() throws Exception {

        clear();

        listAllProfAreas();

        printInfo("Chose the name seperated by commas");

        var output = bufferInput.readLine();
        String[] skills = output.split(",");

        for (String name : skills) {

            try {
                profAreaService.removeProfArea(name);
            } catch (ProfAreaException e) {
                printError(e.getMessage());
            }

        }

      waitForKeyEnter();

    }
    
}
