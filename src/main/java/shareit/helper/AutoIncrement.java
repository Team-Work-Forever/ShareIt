package shareit.helper;

import java.util.Collection;

import shareit.data.Experience;
import shareit.data.ProfArea;
import shareit.data.Skill;
import shareit.data.Talent;
import shareit.data.auth.IdentityUser;

public class AutoIncrement {
    
    private static int incrementProfArea = 1;
    private static int incrementSkill = 1;
    private static int incrementInvitation = 1;
    private static int incrementIdentity = 1;
    private static int incrementExperiences = 1;
    private static int incrementTalents = 1;
    private static int incrementJobOffer = 1;

    public static int getIncrementProfArea() {
        return incrementProfArea++;
    }

    public static int getIncrementSkill() {
        return incrementSkill++;
    }

    public static int getIncrementInvitation() {
        return incrementInvitation++;
    }

    public static int getIncrementIdentity() {
        return incrementIdentity++;
    }

    public static int getIncrementExperiences() {
        return incrementExperiences++;
    }

    public static int getIncrementTalents() {
        return incrementTalents++;
    }

    public static int getIncrementJobOffer() {
        return incrementJobOffer++;
    }


    /**
     * Get data when application starts
     * @param profAreas Given ProfAreas
     * @param skills Given Skills
     * @param invitations Given Invitations
     * @param identityUsers Given IdentityUsers
     */
    public static void getData(Collection<ProfArea> profAreas,
                        Collection<Skill> skills,
                        Collection<Invitation> invitations,
                        Collection<IdentityUser> identityUsers) {

        incrementProfArea = profAreas.size();
        incrementSkill = skills.size();
        incrementInvitation = invitations.size();
        incrementIdentity = identityUsers.size();

        incrementTalents = sumTalents(identityUsers);
        incrementExperiences = sumExperiences(identityUsers);
        incrementJobOffer = sumJobOffer(identityUsers);

    }

    /**
     * Sum Experiences
     * @param identityUsers Given IdentityUsers
     * @return quantity of experiences 
     */
    private static int sumExperiences(Collection<IdentityUser> identityUsers) {
        
        int total = 0;

        for (IdentityUser identityUser : identityUsers) {
            for (Talent talent : identityUser.getTalents()) {
                total += talent.getExperiences().size();
            }
        }

        return total;
    }

    /**
     * Sum JobOffers
     * @param identityUsers Given IdentityUsers
     * @return total of jobOffers
     */
    private static int sumJobOffer(Collection<IdentityUser> identityUsers) {
        
        int total = 0;

        for (IdentityUser identityUser : identityUsers) {
            for (Talent talent : identityUser.getTalents()) {
                for (Experience experience : talent.getExperiences()) {
                    total += experience.getJobOffers().size();
                }
            }
        }

        return total;
    }

    /**
     * Sum Talents
     * @param identityUsers Given IdentityUsers
     * @return total of talents
     */
    private static int sumTalents(Collection<IdentityUser> identityUsers) {
        
        int total = 0;
        
        for (IdentityUser identityUser : identityUsers) {
            total += identityUser.getTalents().size();
        }

        return total;
    }

}
