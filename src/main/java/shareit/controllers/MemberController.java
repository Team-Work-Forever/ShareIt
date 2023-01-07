package shareit.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.contracts.member.InviteMemberRequest;
import shareit.data.JobOffer;
import shareit.data.auth.IdentityUser;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.Authentication;
import shareit.services.MemberService;
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
    private Authentication authenticationService;

    private JobOffer currentJobOffer;

    @Override
    public void display() throws IOException {
        int index = 0;

        if (routeManager.getArgs() instanceof Integer) {
            currentJobOffer = talentService.getJobOfferById(((Integer)routeManager.getArgs()));
        } else {
            printError("Something went wrong!");
            navigationHelper.navigateBack();
        }

        do {
            
            try {
                
                do {
                    
                    clear();

                    index = menu("***************** JobOffer Menu *****************", new String[] {
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

        talentService.getJobOfferById(currentJobOffer.getJobOfferId())
            .removeClient(client);

        printSuccess("The user with email: " + email + " has been removed!");

    }

    private int listMembers() throws IOException {

        clear();

        Collection<IdentityUser> clients = currentJobOffer.getClients();

        if (clients.size() <= 0) {
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

        clear();

        Collection<IdentityUser> allMembers = memberService.getAllMembers();
        
        allMembers = allMembers
            .stream()
                .filter(member -> !member
                    .getEmail().equals(authenticationService.getAuthenticatedUser().getEmail()))
                    .toList();

        if (allMembers.isEmpty()) {
            printInfo("There is no other member to invite!");
            return;
        }

        String[] emails = comboBox("Chose Member separeted by commas(,) to invite members");

        try {
            
            for (String email : emails) {
            
                clear();

                memberService.inviteMember(new InviteMemberRequest(
                    currentJobOffer, 
                    new Date(), // Expire Date
                    email
                ));
    
                printSuccess("Invite send with success to user with email "+ email);
    
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

}
