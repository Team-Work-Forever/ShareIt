package shareit.helper;

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

    public static boolean checkInvite(Object inviteType, IdentityUser invited) {

        if (inviteType == null || invited == null)
            throw new InviteNotValidException();

        if ((inviteType instanceof Experience))
            return ((Experience)inviteType).containsClient(invited.getEmail());

        if ((inviteType instanceof JobOffer))
        {
            if(!compareSkills(inviteType, invited))
                return true;

            return ((JobOffer)inviteType).containsClient(invited.getEmail());

        } 
        
        return false;

    }

    private static boolean compareSkills(Object inviteType, IdentityUser invited) {

        Collection<Skill> intersection = new ArrayList<>(((JobOffer)inviteType).getAllSkills());
        intersection.retainAll(invited.getMySkills());

        return intersection.isEmpty();

    }

    public static Object completeInvite(Object inviteType, IdentityUser invited, Privilege privilege) {

        if (inviteType == null || inviteType == null)
            throw new InviteNotValidException();

        if ((inviteType instanceof Experience))
            inviteToExperience((Experience)inviteType, privilege, invited);

        if ((inviteType instanceof JobOffer))
            inviteToJobOffer((JobOffer)inviteType, invited);

        return inviteType;

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
