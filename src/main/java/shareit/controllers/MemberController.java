package shareit.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.member.InviteMemberRequest;
import shareit.data.JobOffer;
import shareit.data.Skill;
import shareit.data.Talent;
import shareit.data.auth.IdentityUser;
import shareit.errors.JobOfferException;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.Authentication;
import shareit.services.JobOfferService;
import shareit.services.MemberService;
import shareit.services.SkillService;
import shareit.services.TalentService;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.comboBox;
import static shareit.utils.ScreenUtils.printSuccess;
import static shareit.utils.ScreenUtils.textField;

@Controller
public class MemberController extends ControllerBase {

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private TalentService talentService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JobOfferService jobOfferService;

    @Autowired
    private Authentication authenticationService;
   
    @Autowired
    private SkillService skillService;

    private JobOffer currentJobOffer;

    @Override
    public void display() throws IOException {
        
        int index = 0;

        do {
            
            try {

                if (!(routeManager.getArgs() instanceof Integer))
                    throw new JobOfferException("Please provide a valid id");
            
                Optional<JobOffer> jobOfferFound = talentService.getJobOfferById(((Integer)routeManager.getArgs()));;
            
                if (!jobOfferFound.isPresent())
                    throw new JobOfferException("That Job Offer does not exists!");
                    
                currentJobOffer = jobOfferFound.get();
                
                do {
                    
                    clear();

                    index = menu("***************** Job Offer Menu *****************", new String[] {
                        "Select Member",
                        "Add Member",
                        "List Members",
                        "Remove Member"
                    }, authenticationService.getAuthenticatedUser().getName());
                    
                } while (index <= 0 && index >= 5);

                switch (index) {

                    case 1:
                        selectMember();

                        waitForKeyEnter();
                        break;
                    case 2:
                        addMember();

                        waitForKeyEnter();
                        break;
                    case 3:
                        listMembers();

                        waitForKeyEnter();
                        break;
                    case 4:
                        removeMember();

                        waitForKeyEnter();
                        break;
                }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();

    }

    private void removeMember() throws IOException {

        String email;
        IdentityUser client;

        clear();

        if (listMembers() == -1)
            return;

        email = textField("Chose a Member by his email");

        if (email.isEmpty()) {
            printError("Please Provid an email");
            return;
        }

        client = memberService.getMemberByEmail(email);

        jobOfferService.disassociateJobOffer(currentJobOffer, client);

        printSuccess("The user with email: " + email + " has been removed!");

    }

    private int listMembers() throws IOException {

        clear();

        Collection<IdentityUser> clients = currentJobOffer.getClients();

        if (clients.isEmpty()) {
            printInfo("There is no member associated yet!");
            return -1;
        }

        for (IdentityUser client : clients) {
            printInfo(client.toString());
        }

        return 0;

    }

    private void selectMember() throws IOException {

        clear();

        if (listMembers() == -1)
            return;

        String email = textField("Chose a Member by his email");

        displayMember(email);

        waitForKeyEnter();

    }

    private void displayMember(String email) throws IOException {

        IdentityUser member = memberService.getMemberByEmail(email);;
        printInfo(member.toString());
    }

    private void addMember() throws IOException {

        Collection<Talent> reallyAllTalents = talentService.getAllTalentsPublic();
        Map<Skill, Integer> selectedSkills = new HashMap<>();
        Map<Talent, IdentityUser> selectedUsers = new HashMap<>();
        List<Talent> selectedTalents = new ArrayList<>();
        Collection<IdentityUser> users = memberService.getAllMembers();

        clear();

        if (reallyAllTalents.isEmpty()) {
            printInfo("There is no talents yet!");
            return;
        }

        try {

            if (listAllSkills() == -1) {
                return;
            }

            String[] skillNames = comboBox("Chose Skills by ID separeted by commas(,)");

            for (String name : skillNames) {
                
                if (name.isEmpty())
                    continue;
                
                clear();

                String necYears = textField("Skill: " + name + "\tInsert years of experience required");

                selectedSkills.put(
                    skillService.getSkillById(Integer.parseInt(name)),
                    necYears.isEmpty() ? 0 : Integer.parseInt(necYears)
                );

            }
            
            clear();
        
            selectedTalents = ((List<Talent>)talentService.getAllTalentsByOrder(
                Comparator.comparing(Talent::getPricePerHour), 
                selectedSkills
            ));

            if (selectedTalents.isEmpty()) {
                printInfo("There is no talents with that skills!");
                return;
            }

            selectedTalents.forEach(talent -> {
                
                for (IdentityUser user : users) {
                    if (user.containsTalent(talent.getTalentId())) {
                            selectedUsers.put(talent, user);
                    }
                }
  
            });

        } catch (Exception e) {
            printError(e.getMessage());
        }
        
        Map<Talent,IdentityUser> possibleMembers = memberService.getPossibleMembersToJobOffer(selectedUsers);

        for (Talent talent : possibleMembers.keySet()) {
            try {
                printInfo(possibleMembers.get(talent).toString());
                printInfo(talent.toString());
            } catch (Exception e) {
                System.out.println("--- Error ---");
            }  
        }
        
        if (possibleMembers.isEmpty()) {
            printInfo("There is no other members");
            return;
        }

        String[] emails = comboBox("Chose Member separeted by commas(,) to invite members");

        try {
            
            for (String email : emails) {
            
                clear();

                memberService.inviteMember(new InviteMemberRequest(
                    currentJobOffer, 
                    email
                ));
    
                printSuccess("Invite send with success to user with email "+ email);
    
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

        
    }

    private int listAllSkills() throws IOException {

        Collection<Skill> allSkills = skillService.getAll();

        if (allSkills.isEmpty()) {
            printInfo("There is no Skill available");
            return -1;
        }

        try {
            
            for (Skill skill : allSkills) {
                printInfo(skill.toString());
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

        return 0;

    }

}
