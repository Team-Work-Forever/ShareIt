package shareit.repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import jakarta.annotation.PreDestroy;
import shareit.data.auth.IdentityUser;
import shareit.helper.AutoIncrement;
import shareit.helper.Invitation;
import shareit.helper.Pair;
import shareit.data.Skill;
import shareit.data.ProfArea;
import shareit.utils.StoreUtils;
import static shareit.utils.StoreUtils.DATA_FILE;
import static shareit.utils.SerializeUtils.deserialize;
import static shareit.utils.SerializeUtils.serialize;

public class GlobalRepository implements Serializable {
    
    private AutoIncrement autoIncrement;
    private Pair<String, String> authToken;
    private Collection<IdentityUser> identityUsers;
    private Collection<Skill> skills;
    private Collection<ProfArea> profAreas;
    private Collection<Invitation> invites;

    public GlobalRepository() throws Exception {

        this.identityUsers = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.profAreas = new ArrayList<>();
        this.invites = new ArrayList<>();

        if (StoreUtils.verifyFile(DATA_FILE))
        {
            autoIncrement = extractRepository().getAutoIncrement();
            identityUsers = extractRepository().getIdentityUsers();
            skills = extractRepository().getSkills();
            profAreas = extractRepository().getProfAreas();
            authToken = extractRepository().getAuthToken();
            invites = extractRepository().getInvites();
        }
        else
            commit();
            
    }

    /**
     * Extract information from the global store file
     * @return GlobalRepository
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private GlobalRepository extractRepository() throws IOException, ClassNotFoundException {
        return (GlobalRepository)deserialize(DATA_FILE);
    }

    // Crud Authentication

    /**
     * Get The Authentication Token
     * @return
     */
    public Pair<String, String> getAuthToken() {
        return authToken;
    }

    public void setTokenPair(String email, String token) {
        authToken = new Pair<>(email, token);
    }

    public void clearAuthToken() {
        authToken = null;
    }

    public Collection<IdentityUser> getIdentityUsers() {
        return identityUsers;
    }

    /**
     * Get User
     * @param email Given IdentityUser Email
     * @return Optional
     */
    public Optional<IdentityUser> getIdentityUserByEmail(String email) {
        return identityUsers
            .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    /**
     * Verify If Repository contains User
     * @param email Given User Email
     * @return true if Repository contains user
     */
    public boolean containsEmail(String email) {
        return getIdentityUserByEmail(email).isPresent();
    }


    /**
     * Create User
     * @param identityUser user
     */
    public void createIdentityUser(IdentityUser identityUser) {
        identityUsers.add(identityUser);
    }

    /**
     * Remove User
     * @param email Given User Email
     * @return
     */
    public boolean removeIdentityUserByEmail(String email) {
        
        if (!containsEmail(email))
            return false;

        return identityUsers.remove(getIdentityUserByEmail(email).get());

    }

    /**
     * Update user 
     * @param email Given User Email
     * @param identityUser user
     * @return true if user is updated successfully
     */
    public boolean updateIdentityUserByEmail(String email, IdentityUser identityUser) {

        if (containsEmail(email))
            return false;

        removeIdentityUserByEmail(email);
        createIdentityUser(identityUser);

        return true;

    }

    // Crud Skill

    public Collection<Skill> getSkills() {
        return skills;
    }

    /**
     * Get Skill
     * @param id Given Skill Id
     * @return Optional
     */
    public Optional<Skill> getSkillById(int id) {

        return skills
            .stream()
                .filter(skill -> skill.getSkillId() == id)
                .findAny();

    }

    /**
     * Verify if Repository contains Skill
     * @param id Given Skill Id
     * @return true if Repository contains Skill
     */
    public boolean containsSkillById(int id) {
        return getSkillById(id).isPresent();
    }

    /**
     * Create Skill
     * @param skill Given Skill
     * @return true if skill is created successfully
     */
    public boolean createSkill(Skill skill) {

        if (containsSkillById(skill.getSkillId()))
            return false;

        skills.add(skill);

        return true;

    }

    /**
     * Remove Skill from Repository
     * @param id Given Skill Id
     * @return true if Skill is removed successfully
     */
    public boolean removeSkillById(int id) {

        if (!containsSkillById(id))
            return false;

        skills.remove(getSkillById(id).get());

        return true;

    }

    // Crud ProfArea

    public Collection<ProfArea> getProfAreas() {
        return profAreas;
    }
    
    /**
     * Get ProfArea of Repository
     * @param id Given ProfArea Id
     * @return Optional
     */
    public Optional<ProfArea> getProfAreaById(int id) {

        return profAreas
            .stream()
                .filter(profArea -> profArea.getProfAreaId() == id)
                .findAny();

    }

    /**
     * Verify if Repository contains ProfArea
     * @param id Given ProfArea Id
     * @return true if Repository contains ProfArea
     */
    public boolean containsProfAreaById(int id) {
        return getProfAreaById(id).isPresent();
    }

    /**
     * Create ProfArea
     * @param profArea Given ProfArea
     * @return true if ProfArea is created successfully
     */
    public boolean createProfArea(ProfArea profArea) {

        if (containsProfAreaById(profArea.getProfAreaId()))
            return false;

        profAreas.add(profArea);

        return true;

    }

    /**
     * Remove ProfArea from Repository
     * @param id Given ProfArea If
     * @return true if ProfArea is removed successfully
     */
    public boolean removeProfAreaById(int id) {

        if (!containsProfAreaById(id))
            return false;

        profAreas.remove(getProfAreaById(id).get());

        return true;

    }

    /**
     * Commit Changes to file
     * @throws Exception
     */
    public void commit() throws Exception {
        serialize(this, DATA_FILE);        
    }

    // Crud Invites

    public Collection<Invitation> getInvites() {
        return invites;
    }

    /**
     * Verify if Repository contains Invitation of Owner
     * @param emailOwner Given Owner Email
     * @return true if Repository contains Invitations
     */
    public boolean containsInvitationWithOwner(String emailOwner) {

        return invites
            .stream()
                .filter(invite -> invite.getEmailFrom().equals(emailOwner))
                .findAny().isPresent();

    }

    /**
     * Get Invitation From Repository
     * @param emailOwner Given Owner Email
     * @return Optional
     */
    public Optional<Invitation> getInvitationByOwner(String emailOwner) {

        return invites
            .stream()
                .filter(invite -> invite.getEmailFrom().equals(emailOwner))
                .findAny();

    }

    /**
     * Create Invite
     * @param invitation Given Invitation
     * @return true if invite is created successfully
     */
    public boolean createInvite(Invitation invitation) {
        return invites.add(invitation);
    }

    /**
     * Remove Invite
     * @param invitation Given Invitation
     * @return true if Invitation is removed successfully
     */
    public boolean removeInvite(Invitation invitation) {
        return invites.remove(invitation);
    }

    public AutoIncrement getAutoIncrement() {
        return autoIncrement;
    }

    @PreDestroy()
    public void close() throws Exception {
        commit();
    }

}
