package shareit.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.textField;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.comboBox;
import static shareit.utils.ScreenUtils.printSuccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.talent.CreateTalentRequest;
import shareit.contracts.talent.TalentAssociationProfArea;
import shareit.contracts.talent.TalentAssociationSkill;
import shareit.contracts.talent.TalentDisassociateProf;
import shareit.contracts.talent.TalentDisassociateSkill;
import shareit.data.ProfArea;
import shareit.data.Skill;
import shareit.data.Talent;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.Authentication;
import shareit.services.ProfAreaService;
import shareit.services.SkillService;
import shareit.services.TalentService;

@Controller
public class ManageTalentController extends ControllerBase {

    @Autowired
    private Authentication authenticationService;

    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private TalentService talentService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private ProfAreaService profAreaService;

    @Autowired
    private RouteManager routeManager;

    private Talent currentTalent;

    @Override
    public void display() throws IOException {

        int index = 0;

        clear();

        do {
            
            try {

                if (currentTalent == null) {
                    currentTalent = (talentService.getTalentById(((Integer)routeManager.getArgs())));
                }
                
                var authUser = authenticationService.getAuthenticatedUser();

                do {
                    
                    clear();

                    index = menu("***************** Update Talent Menu *****************", new String[] {
                        "Update Domain",
                        "Associate Skill",
                        "Associate Prof. Area",
                        "Disasociate Skill",
                        "Disasociate Prof. Area"
                    }, authUser.getName());

                } while (index <= 0 && index >= 6);

                switch (index) {
                    case 1:
                        alterDomain();
                        break;
                    case 2:
                        associateSkillToTalent();
                        break;
                    case 3:
                        associateProfAreaToTalent();

                        waitForKeyEnter();
                        break;
                    case 4:
                        disassociateSkillToTalent();
                        break;
                    case 5:
                        disassociateProfAreaToTalent();
                        break;
                }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        currentTalent = null;

        navigationHelper.navigateBack();

    }

    public void alterDomain() throws IOException {

        try {

            clear();

            String talentName = textField("Talent Name (default : same)");
            String pricePerHour = textField("Price Per Hour (default : same)");
            String isPublic = textField("Public/Private (t|f)");

            talentService.updateTalent(
                currentTalent.getTalentId(),
                new CreateTalentRequest(
                    talentName.isEmpty() ? currentTalent.getName() : talentName, 
                    pricePerHour.isEmpty() ? currentTalent.getPricePerHour() : Float.parseFloat(pricePerHour), 
                    isPublic.isEmpty() ? currentTalent.getIsPublic() : true
                )
            );

        } catch (NumberFormatException e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                alterDomain();
            }

        } catch (Exception e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                alterDomain();
            }

        }
    }

    private void disassociateSkillToTalent() throws IOException {

        Collection<Skill> selectedSkills = new ArrayList<>();

        try {

            clear();

            listAllSkills();

            String[] skillNames = comboBox("Chose Skills by ID separeted by commas(,)");

            clear();

            for (String name : skillNames) {

                if (name.isEmpty()) {
                    printError("Id not valid: " + name);
                    continue;   
                }

                selectedSkills.add(skillService.getSkillById(Integer.parseInt(name)));
            
            }
            
            for (Skill skill : selectedSkills) {

                talentService.disassociateSkills(new TalentDisassociateSkill(
                    currentTalent,
                    skill
                ));
                
            }

            printSuccess("The Skill(s) was dissassociate!");

        } catch (NumberFormatException e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                alterDomain();
            }

        } catch (Exception e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                alterDomain();
            }

        }

    }

    private void disassociateProfAreaToTalent() throws IOException {

        Collection<ProfArea> selectedProfArea = new ArrayList<>();

        try {

            clear();

            listAllProfAreas();

            String[] profAreaNames = comboBox("Chose Prof. Areas by ID separeted by commas(,)");

            clear();

            for (String name : profAreaNames) {

                if (name.isEmpty()) {
                    printError("Id not valid: " + name);
                    continue;   
                }

                selectedProfArea.add(profAreaService.getProfAreaById(Integer.parseInt(name)));
            
            }
            
            for (ProfArea profA : selectedProfArea) {

                talentService.disassociateProfAreas(new TalentDisassociateProf(
                    currentTalent,
                    profA
                ));
                
            }

            printSuccess("The ProfArea(s) was dissassociate!");

        } catch (NumberFormatException e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                alterDomain();
            }

        } catch (Exception e) {
            
            printError(e.getMessage());

            if (repeatAction("Do you wanna repeat?")) {
                alterDomain();
            }

        }

    }

    private void associateProfAreaToTalent() throws IOException {

        Map<ProfArea, Integer> selectedProfAreas = new HashMap<>();

        clear();

        try {

            listAllProfAreas();
            
           associateProfArea(selectedProfAreas, currentTalent);

           printSuccess("The ProfArea was Associated!");

        } catch (NumberFormatException e) {
            
            printError(e.getMessage());
            
            if (repeatAction("Do you wanna repeat?")) {
                associateSkillToTalent();
            };

        } catch (Exception e) {
            
            printError(e.getMessage());
            
            if (repeatAction("Do you wanna repeat?")) {
                associateSkillToTalent();
            };

        }

    }

    private int listAllTalents() throws IOException {

        Collection<Talent> talents = talentService.getAllTalents();

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
   
    private void associateProfArea(Map<ProfArea, Integer> selectedProfArea, Talent talent) throws IOException, Exception {
        
        clear();

        listAllProfAreas();

        String[] profAreas = comboBox("Chose Professional Areas ID between commas(,): ");

        clear();

        try {

            for (String profAreaName : profAreas) {

                if (profAreaName.isEmpty())
                    continue;

                String expYears = textField("Prof. Area: " + profAreaName + "\tInsert years of experience");

                selectedProfArea.put(
                    profAreaService.getProfAreaById(Integer.parseInt(profAreaName)),
                    expYears.isEmpty() ? 0 : Integer.parseInt(expYears));

            }

            talentService.associateProfAreas(new TalentAssociationProfArea(
                talent,
                selectedProfArea
            ));

        } catch (Exception e) {
            
            printError(e.getMessage());

            if (repeatAction("Do You wanna repeat?")) {
                associateProfArea(selectedProfArea, talent);
            }

        }

    }

    private void listAllProfAreas() throws IOException {
        for (ProfArea profArea : profAreaService.getAll()) {
            printInfo(profArea.toString());
        }
    }

    private void associateSkillToTalent() throws IOException {

        Collection<Skill> selectedSkill = new ArrayList<>();

        clear();

        if (listAllTalents() == -1)
        {
            return;
        }

        try {
            
           associateSkill(selectedSkill, currentTalent);

           printSuccess("The Skill was associated!");

        } catch (NumberFormatException e) {
            
            printError(e.getMessage());
            
            if (repeatAction("Do you wanna repeat?")) {
                associateSkillToTalent();
            };

        } catch (Exception e) {
            
            printError(e.getMessage());
            
            if (repeatAction("Do you wanna repeat?")) {
                associateSkillToTalent();
            };

        }
    }

    private void associateSkill(Collection<Skill> selectedSkill, Talent talent) throws IOException, Exception {
    
        Map<Skill,Integer> selectedSkills = new HashMap<>();
        
        clear();

        listAllSkills();

        String[] skillsNames = comboBox("Chose Skills ID between commas(,) to associate them to the new talent");
        
        clear();

        try {
        
            for (String skillName : skillsNames) {

                if (skillName.isEmpty())
                    continue;

                String expYears = textField("Skill: " + skillName + "\tInsert years of experience");

                selectedSkills.put(
                    skillService.getSkillById(Integer.parseInt(skillName)),
                    expYears.isEmpty() ? 0 : Integer.parseInt(expYears)
                );

            }
            
        } catch (Exception e) {
                printError(e.getMessage());
        }

        talentService.associateSkills(new TalentAssociationSkill(
            talent, 
            selectedSkills
        ));
    
    }

    private void listAllSkills() throws IOException {
        for (Skill skill : skillService.getAll()) {
            printInfo(skill.toString());
        }
    }

}