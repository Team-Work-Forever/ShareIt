package shareit.controllers;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.skill.CreateSkillRequest;
import shareit.data.Skill;
import shareit.errors.SkillException;
import shareit.helper.NavigationHelper;
import shareit.services.SkillService;

import static shareit.utils.ScreenUtils.comboBox;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.bufferInput;

@Controller
public class SkillController extends ControllerBase {

    @Autowired
    private SkillService skillService;

    @Autowired
    private NavigationHelper navigationHelper;

    @Override
    public void display() throws IOException {
       
        int index = 0;

            do {
                try {
                    do {
                
                        clear();
        
                        index = menu("***************** Skill Menu *****************", new String[] { 
                            "Create Skill", 
                            "List All Skills",
                            "Update Skill",
                            "Remove Skill",
                        });
        
                    } while (index <= 0 && index >= 4);

                switch (index) {
                    case 1:
                        createSkill();
                    break;
                    case 2:
                        listAllSkills();
                        waitForKeyEnter();
                    break;
                    case 3:
                        updateSkill();
                    break;
                    case 4:
                        removeSkill();
                    break;
                } 
        
            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();

}

    private void createSkill() throws IOException {

        clear();

        try {

            System.out.println("Skill Info:");

            String name = textField("Name");
            String description = textField("Description");
            
            skillService.createSkill(new CreateSkillRequest(
                name, 
                description
            ));

        } catch (SkillException e) {
            printError(e.getMessage());

            if (repitAction("Do you wanna exit creation?")) {
                createSkill();
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private void listAllSkills() throws IOException {

        int i = 1;

        Collection<Skill> skills = skillService.getAll();

        if (skills.isEmpty())
        {
            printInfo("There is no skills yet!");
            return;
        }

        for (Skill skill : skills) {
            System.out.println();
            printInfo(i  + " - " + skill.toString());
            i++;
        }

        System.out.println();

    }

    private void updateSkill() throws Exception {

        clear();

        listAllSkills();

        printInfo("Chose the name seperated by commas");

        var output = bufferInput.readLine();
        String[] skills = output.split(",");

        for (String skillName : skills) {

            try {

                var skill = skillService.getSkillByName(skillName);

                System.out.println("Update Data: ");
                String name = textField("Skill Name: (default : same)");
                String desc = textField("Skill Description: (default : same)");

                Skill newSkill = new Skill(
                    name.isEmpty() ? skill.getName() : name, 
                    desc.isEmpty() ? skill.getDesc() : desc
                );

            
                skillService.updateSkill(newSkill, skillName);
            } catch (SkillException e) {
                printError(e.getMessage());
            }

        }

      waitForKeyEnter();

    }

    private void removeSkill() throws Exception {

        clear();

        listAllSkills();

        System.out.println();

        String[] skills = comboBox("Chose the Name seperated by commas (,)");

        for (String name : skills) {
                
            try {
                skillService.removeSkill(name);
            } catch (SkillException e) {
                printError(e.getMessage());
            }

        }

        waitForKeyEnter();

    }
    
}
