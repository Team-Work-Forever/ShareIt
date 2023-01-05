package shareit.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import shareit.repository.GlobalRepository;
import shareit.validator.BeanValidator;
import shareit.contracts.member.InviteMemberRequest;
import shareit.data.auth.IdentityUser;
import shareit.errors.MemberFailedInvitation;
import shareit.errors.auth.IdentityException;
import shareit.helper.Invitation;
import shareit.helper.InvitationManager;

@Service
public class MemberService {
    
    private final BeanValidator<InviteMemberRequest> validatorInviteRequest = new BeanValidator<>();

    @Autowired
    private GlobalRepository globalRepository;

    @Autowired
    private Authentication authenticationService;

    @Autowired
    private SkillService skillService;

    public IdentityUser getMemberByEmail(String email) {

        if (!globalRepository.containsEmail(email))
            throw new IdentityException("Was not found any User with this email!");

        return globalRepository.getIdentityUserByEmail(email).get();
        
    }

    public Collection<IdentityUser> getAllMembers() {
        return globalRepository.getIdentityUsers();
    }

    public Collection<Invitation> getInviteInBox(String email) {

        Collection<Invitation> invites = new ArrayList<>();

        for (Invitation invitation : globalRepository.getInvites()) {
            if (invitation.getEmailTo().equals(email)) {
                invites.add(invitation);
            }
        }

        return invites;

    }

    public boolean containsInvites(String email) {
        return getInviteInBox(email).size() > 0;
    }

    public boolean inviteMember(@Validated InviteMemberRequest request) throws Exception {

        String authEmail = authenticationService.getAuthenticatedUser().getEmail();

        var errors = validatorInviteRequest.validate(request);

        if (!errors.isEmpty()) {
            throw new MemberFailedInvitation(errors.iterator().next().getMessage());
        }

        request.setEmailFrom(authEmail);

        if (request.getEmailTo().equals(authEmail)) {
            throw new MemberFailedInvitation("You cannot invite yourself!");
        }

        Optional<IdentityUser> invitedUser = globalRepository.getIdentityUserByEmail(request.getEmailTo());

        if (!invitedUser.isPresent()) {
            throw new IdentityException("User Not Found!");
        }

        globalRepository.createInvite(request.toInvitation());
        globalRepository.commit();

        return true;

    }

    /**
     * @param invite 
     * @return boolean
     * @throws Exception
     */
    public boolean acceptInvite(Invitation invite) throws Exception {

        // Accept's the invite sent
        InvitationManager
            .completeInvite(
                invite.getObject(),
                getMemberByEmail(invite.getEmailTo()),
                invite.getPrivilege()
            );

        // Removes invite from the system
        removeInvite(invite);

        globalRepository.commit();

        return true;

    }

    public boolean removeInvite(Invitation invitation) {
        return globalRepository.removeInvite(invitation);
    }

    // TODO: What For?
    public int qtyProfUsingSkill(String name) {

        var skill = skillService.getSkillByName(name);

        return skill.getQtyProf();

    }

}
