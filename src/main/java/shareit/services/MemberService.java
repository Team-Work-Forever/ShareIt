package shareit.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import shareit.repository.GlobalRepository;
import shareit.validator.BeanValidator;
import shareit.contracts.member.InviteMemberRequest;
import shareit.data.Invite;
import shareit.data.Privilege;
import shareit.data.auth.IdentityUser;
import shareit.errors.MemberFailedInvitation;
import shareit.errors.auth.IdentityException;

@Service
public class MemberService {
    
    private final BeanValidator<InviteMemberRequest> validatorInviteRequest = new BeanValidator<>();

    @Autowired
    private GlobalRepository globalRepository;

    @Autowired
    private TalentService talentService;

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

    public boolean inviteMember(@Validated InviteMemberRequest request) throws Exception {

        var errors = validatorInviteRequest.validate(request);

        if (!errors.isEmpty()) {
            throw new MemberFailedInvitation(errors.iterator().next().getMessage());
        }

        if (!globalRepository.containsEmail(request.getEmail())) {
            throw new IdentityException("User not found!");
        }

        if (!request.getexperience().containsClient(request.getEmail())) {
            throw new MemberFailedInvitation("You cannot invite yourself!");
        }

        var member = getMemberByEmail(request.getEmail());

        member.addInvite(
            new Invite(
                request.getexperience(),
                request.getEmail()
        ));

        globalRepository.commit();

        return true;

    }

    public boolean acceptInvite(Invite invite) throws Exception {

        var experience = talentService.getExperienceByTitle(invite.getexperience().getTitle());

        experience.addClient(
            getMemberByEmail(invite.getEmail()), 
            Privilege.Worker
        );

        globalRepository.commit();

        return true;

    }

    public int qtyProfUsingSkill(String name) {

        var skill = skillService.getSkillByName(name);

        return skill.getQtyProf();

    }

}
