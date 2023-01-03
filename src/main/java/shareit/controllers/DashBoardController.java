package shareit.controllers;

import java.io.IOException;

import static shareit.utils.ScreenUtils.menu;
import static shareit.utils.ScreenUtils.clear;
import static shareit.utils.ScreenUtils.printError;
import static shareit.utils.ScreenUtils.printInfo;
import static shareit.utils.ScreenUtils.printSuccess;
import static shareit.utils.ScreenUtils.comboBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import shareit.data.Invite;
import shareit.data.auth.IdentityUser;
import shareit.errors.InviteNotValidException;
import shareit.helper.NavigationHelper;
import shareit.helper.RouteManager;
import shareit.services.AuthenticationService;
import shareit.services.MemberService;

@Controller
public class DashBoardController extends ControllerBase {

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private NavigationHelper navigationHelper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MemberService memberService;

    private IdentityUser authUser;

    @Override
    public void display() throws IOException {

        int index = 0;

        authUser = authenticationService.getAuthenticatedUser();

        clear();
        
        do {

            try {
            
                do {
                
                    clear();

                    String[] options = {
                        "Skill Menu",
                        "ProfArea Menu",
                        "Talent Menu",
                        "Invites Available"
                    };
        
                    index = menu("***************** DashBoard *****************", options);
        
                } while (index <= 0 && index >= 4);

                switch (index) {
                    case 0:
                        logout();
                        break;
                    case 1:
                        authorize();
                        skillMenu();
                        break;
                    case 2:
                        authorize();
                        profAreaMenu();
                        break;
                    case 3:
                        talentMenu();
                        break;
                    case 4:
                        menuInvite();
                        break;
                }
    
            } catch (Exception e) {
                printError(e.getMessage());
            }

        } while (index != 0);

        navigationHelper.navigateTo(routeManager.authRoute());

    }
    
    private void menuInvite() throws IOException {

        clear();

        if (!authUser.thereIsInvites()) {
            printInfo("You don't have any invite yet!");
            return;
        }

        for (Invite invite : authUser.getInvites()) {
            printInfo(invite.toString());
        }

        try {
            
            String[] invites = comboBox("Chose Invite by id separeted by commas(,)");

            for (String invite : invites) {
    
                if (invite.isEmpty())
                    throw new InviteNotValidException();

                var result = memberService.acceptInvite(
                    authUser.getInviteById(Integer.parseInt(invite.trim()))
                );

                if (result)
                    printSuccess("Invite Accept!");
                    
            }

        } catch (Exception e) {
            printError(e.getMessage());
        }

    }

    private void skillMenu() throws IOException {
        navigationHelper.navigateTo(SkillController.class);
    }
    
    private void talentMenu() throws IOException {
        navigationHelper.navigateTo(TalentController.class);
    }
    
    private void profAreaMenu() throws IOException {
        navigationHelper.navigateTo(ProfAreaController.class);
    }

}
