package shareit.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import shareit.repository.GlobalRepository;
import shareit.validator.BeanValidator;
import shareit.contracts.member.InviteMemberRequest;
import shareit.data.Talent;
import shareit.data.auth.IdentityUser;
import shareit.errors.ExperienceException;
import shareit.errors.InviteNotValidException;
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

    public IdentityUser getMemberByEmail(String email) {

        if (!globalRepository.containsEmail(email))
            throw new IdentityException("Was not found any User with this email!");

        return globalRepository.getIdentityUserByEmail(email).get();
        
    }

    public Collection<IdentityUser> getAllMembers() {
        return globalRepository.getIdentityUsers();
    }

    public Collection<IdentityUser> getPossibleMembers() {

        return getAllMembers()
            .stream()
                .filter(member -> !member
                    .getEmail().equals(authenticationService.getAuthenticatedUser().getEmail()))
                    .toList();

    }

    public Map<Talent, IdentityUser> getPossibleMembersToJobOffer(Map<Talent, IdentityUser> users) {

        for (Talent talent : users.keySet()) {
            if (users.get(talent).getEmail().equals(authenticationService.getAuthenticatedUser().getEmail())) {
                users.remove(talent);
            }
        }

        return users;

    }

    public Collection<Invitation> getInviteInBox(String email) {

        Collection<Invitation> invites = globalRepository.getInvites();
        Collection<Invitation> selectedInvites = new ArrayList<>();

        Iterator<Invitation> it = invites.iterator();

        while( it.hasNext() ) {

            Invitation invite = it.next();

            if (invite.getEmailTo().equals(email)) {
                if (invite.getExpire().isAfter(LocalDate.now())) {
                    selectedInvites.add(invite);
                } else {
                    it.remove(); // If this invitation get expired then is removed! O error esta aqui não se pode remover uma variael fltuante!
                }
            }

        }

        return selectedInvites;

    }

    public boolean containsInvites(String email) {
        return !getInviteInBox(email).isEmpty();
    }

    public boolean inviteMember(@Validated InviteMemberRequest inviteMemberRequest) throws Exception {

        String authEmail = authenticationService.getAuthenticatedUser().getEmail();

        var errors = validatorInviteRequest.validate(inviteMemberRequest);

        if (!errors.isEmpty()) {
            throw new MemberFailedInvitation(errors.iterator().next().getMessage());
        }

        inviteMemberRequest.setEmailFrom(authEmail);

        if (inviteMemberRequest.getEmailTo().equals(authEmail)) {
            throw new MemberFailedInvitation("You cannot invite yourself!");
        }

        if (verifyMultiInvitation(inviteMemberRequest.getEmailFrom(), inviteMemberRequest.getEmailTo())) {
            throw new MemberFailedInvitation("You cannot prepose an invite twoice!");
        }

        Optional<IdentityUser> invitedUser = globalRepository.getIdentityUserByEmail(inviteMemberRequest.getEmailTo());

        if (!invitedUser.isPresent()) {
            throw new IdentityException("User Not Found!");
        }

        Invitation invite = inviteMemberRequest.toInvitation();

       try {
             
            InvitationManager
            .checkInvite(
                invite.getInvitationType(), 
                invitedUser.get(),
                invite.getExpire()
            );

        } catch (InviteNotValidException e) {
            throw new ExperienceException(e.getMessage());
        }

        globalRepository.createInvite(inviteMemberRequest.toInvitation());
        globalRepository.commit();

        return true;

    }

    public boolean verifyMultiInvitation(String emailFrom, String emailTo) {

        return globalRepository.getInvites()
                .stream()
                    .filter((invite) -> {
                        return invite.getEmailFrom().equals(emailFrom) && invite.getEmailTo().equals(emailTo);
                    })
                    .findAny().isPresent();

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

}
