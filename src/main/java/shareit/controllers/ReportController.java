package shareit.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static shareit.utils.ScreenUtils.printInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.data.ProfArea;
import shareit.data.auth.IdentityUser;
import shareit.errors.ReportException;
import shareit.helper.NavigationHelper;
import shareit.services.Authentication;
import shareit.services.ProfAreaService;
import shareit.services.ReportService;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.printSuccess;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.comboBox;

@Controller
public class ReportController extends ControllerBase {

    @Autowired
    private ReportService reportService;
    
    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private Authentication authenticationService;

    @Autowired
    private ProfAreaService profAreaService;
    
    @Override
    public void display() throws IOException {
        
        int index = 0;

        IdentityUser authUser = authenticationService.getAuthenticatedUser();

        do {
            
            try {

                do {
                    
                    clear();

                    index = menu("***************** Report Menu *****************", new String[] {
                        "Price (month - 176 hours) By Prof. Area and Country",
                        "Price (month - 176 hours) By Skill"
                    }, authUser.getName());
                    
                } while (index <= 0 && index >= 2);

                switch (index) {

                    case 1:
                        generateReportByProfAreaAndCountry();

                        waitForKeyEnter();
                        break;
                    case 2:
                        generateReportBySkill();

                        waitForKeyEnter();
                        break;
                }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();


    }

    private void generateReportBySkill() throws IOException {

        reportService.generateReportBySkill();

        // Option open in Excel
        printSuccess("The Report was genereted!");

        waitForKeyEnter();

    }

    private void generateReportByProfAreaAndCountry() throws IOException {

        Collection<ProfArea> selectedProfArea = new ArrayList<>();

        clear();

        String countryName = textField("Set Country");

        if (countryName.isEmpty())
            throw new ReportException("Please provide an country!");

        clear();

        listAllProfAreas();
        
        String[] profAreaName = comboBox("Chose Prof. Areas by ID separeted by commas(,)");

        clear();

        try {
                    
            for (String name : profAreaName) {
                
                if (name.isEmpty())
                    continue;

                selectedProfArea.add(profAreaService.getProfAreaById(Integer.parseInt(name)));

            }

        } catch (NumberFormatException e) {
            printError(e.getMessage());
        } catch (Exception e) {
            printError(e.getMessage());
        }

        reportService.generateReportByProfAreaAndCountry(selectedProfArea, countryName);

        printSuccess("The Report was genereted!");

        waitForKeyEnter();

    }

    private void listAllProfAreas() throws IOException {
        for (ProfArea profArea : profAreaService.getAll()) {
            printInfo(profArea.toString());
        }
    }
    
}
