package shareit.helper;

import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.Privilege;
import shareit.data.auth.IdentityUser;
import shareit.errors.InviteNotValidException;

public class InvitationManager {

    /**
     * @param Object inviteType
     * @param IdentityUser invited
     * @param Collection<Object> params
     * @return
     */
    public static Object completeInvite(Object inviteType, IdentityUser invited, Privilege privilege) {

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
    }

    private static void inviteToJobOffer(JobOffer jobOffer, IdentityUser invited) {
        jobOffer.addClient(invited);
    }

}
