package shareit.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.Privilege;
import shareit.data.Skill;
import shareit.data.auth.IdentityUser;
import shareit.errors.InviteNotValidException;
import shareit.errors.auth.IdentityException;

public class InvitationManager {

    /**
     * Checks invitation clauses
     * @param inviteType Given Type (JobOffer, Experience, ReverseInvite)
     * @param invited Given User to be Invited
     * @param expiredDate Given Expired Date
     * @return
     */
    public static boolean checkInvite(Object inviteType, IdentityUser invited, LocalDate expiredDate) {

        if (expiredDate.isBefore(LocalDate.now())) {
            throw new InviteNotValidException();
        }

        if (inviteType == null || invited == null)
            throw new InviteNotValidException();

        if ((inviteType instanceof Experience))
            if (((Experience)inviteType).containsClient(invited.getEmail()))
                throw new InviteNotValidException("This user already is registered in the experience!");

        if ((inviteType instanceof JobOffer))
        {
            if(!compareSkills(inviteType, invited))
                throw new InviteNotValidException("You don't have the skills necessary!");

            if (((JobOffer)inviteType).containsClient(invited.getEmail())) {
                throw new InviteNotValidException("This user already is registered in the Job Offer!");
            }

        }

        if (inviteType instanceof ReverseInvite) {

            if(!compareSkills(((ReverseInvite)inviteType).getApplication(), ((ReverseInvite)inviteType).getCadidateUser()))
                throw new InviteNotValidException("You don't have the skills necessary!");
            
            if (!((JobOffer)((ReverseInvite)inviteType).getApplication()).containsClient(invited.getEmail())) {
                throw new InviteNotValidException("There is no Job Offer available!");
            }

        }
        
        return false;

    }

    /**
     * Compare Skills
     * @param inviteType Given Invite (JobOffer, Experience, ReverseInvite)
     * @param invited Given User to be invited
     * @return true if has all skills
     */
    private static boolean compareSkills(Object inviteType, IdentityUser invited) {

        int i = 0;
        Collection<Skill> intersection = new ArrayList<>(((JobOffer)inviteType).getAllSkills());

        for (Skill skillOffer : intersection) {
                
            for (Skill skill : invited.getMySkills()) {
                if (skillOffer.getSkillId() == skill.getSkillId()) {
                    i++;
                }
            }
        }

        if (i == intersection.size()) {
            return true;
        }

        return false;

    }

    /**
     * Accepts Invite
     * @param inviteType Given Type (JobOffer, Experience, ReverseInvite)
     * @param invited Given User to be Invited
     * @param privilege Given Expired Date
     * @return
     */
    public static Object completeInvite(Object inviteType, IdentityUser invited, Privilege privilege) {

        if (inviteType == null || inviteType == null)
            throw new InviteNotValidException();

        if ((inviteType instanceof Experience))
            inviteToExperience((Experience)inviteType, privilege, invited);

        if ((inviteType instanceof JobOffer))
            inviteToJobOffer((JobOffer)inviteType, invited);
        
        if ((inviteType instanceof ReverseInvite))
            inviteReverse((ReverseInvite)inviteType);

        return inviteType;

    }

    private static void inviteReverse(ReverseInvite inviteType) {
    
        JobOffer application = (JobOffer)inviteType.getApplication();

        application.addClient(inviteType.getCadidateUser());
        inviteType.getCadidateUser().associateJobOffer(application);

    }

    private static void inviteToExperience(Experience experience, Privilege privilege, IdentityUser invited) {
        
        if (privilege == null)
            throw new InviteNotValidException();
        
        experience.addClient(invited, (Privilege)privilege);
        invited.associateExperience(experience, privilege);

    }

    private static void inviteToJobOffer(JobOffer jobOffer, IdentityUser invited) {

        try {
            jobOffer.addClient(invited);
        } catch (IdentityException e) {
            throw new InviteNotValidException();
        }

        invited.associateJobOffer(jobOffer);

    }

}
