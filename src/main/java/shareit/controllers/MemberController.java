package shareit.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.data.JobOffer;
import shareit.data.auth.IdentityUser;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.MemberService;
import shareit.services.TalentService;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.waitForKeyEnter;
import static shareit.utils.ScreenUtils.comboBox;

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

    private JobOffer currentJobOffer;

    @Override
    public void display() throws IOException {
        int index = 0;

        currentJobOffer = talentService.getJobOfferById((int)routeManager.getArgs());

        do {
            
            try {
                
                do {
                    
                    clear();

                    index = menu("***************** JobOffer Menu *****************", new String[] {
                        "Select Member",
                        "Add Member",
                        "List Members",
                        "Remove JobOffers"
                    });
                    
                } while (index <= 0 && index >= 5);

                switch (index) {

                    case 1:
                        selectMember();
                        break;
                    case 2:
                        addMember();
                        break;
                    case 3:
                        listMembers();

                        waitForKeyEnter();
                        break;
                    case 4:
                        removeMember();
                        break;
                }

            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);
        
        navigationHelper.navigateBack();

    }

    // TODO: Implement Remove Member
    private void removeMember() {



    }

    private void listMembers() throws IOException {

        clear();

        for (IdentityUser client : currentJobOffer.getClients()) {
            printInfo(client.toString());
        }
    }

    // TODO: Implement Select Member
    private void selectMember() throws IOException {

        // clear();

        // listAllMembers();

        // String email = textField("Chose one Member by his email");

        // navigationHelper.navigateTo(
            
        // );

    }

    // TODO: Implement Add Member
    private void addMember() throws IOException {

        listAllMembers();

        String[] users = comboBox("Chose Member separeted by commas(,) to invite members");

        for (String member : users) {
            memberService.getMemberByEmail(member);
        }


    }

    // TODO: Remove this shit
    private void listAllMembers() throws IOException {

        for (IdentityUser user : memberService.getAllMembers()) {
            printInfo(user.toString());
        }

    }

}
