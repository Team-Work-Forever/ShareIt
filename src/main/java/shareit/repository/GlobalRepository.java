package shareit.repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import jakarta.annotation.PreDestroy;
import shareit.data.auth.IdentityUser;
import shareit.helper.Invitation;
import shareit.helper.Pair;
import shareit.data.Skill;
import shareit.data.ProfArea;
import shareit.utils.StoreUtils;
import static shareit.utils.StoreUtils.DATA_FILE;
import static shareit.utils.SerializeUtils.deserialize;
import static shareit.utils.SerializeUtils.serialize;

public class GlobalRepository implements Serializable {
    
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
            identityUsers = extractRepository().getIdentityUsers();
            skills = extractRepository().getSkills();
            profAreas = extractRepository().getProfAreas();
            authToken = extractRepository().getAuthToken();
            invites = extractRepository().getInvites();
        }
        else
            commit();
            
    }

    private GlobalRepository extractRepository() throws IOException, ClassNotFoundException {
        return (GlobalRepository)deserialize(DATA_FILE);
    }

    // Crud Authentication

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

    public Optional<IdentityUser> getIdentityUserByEmail(String email) {
        return identityUsers
            .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public boolean containsEmail(String email) {
        return getIdentityUserByEmail(email).isPresent();
    }

    public void createIdentityUser(IdentityUser identityUser) {
        identityUsers.add(identityUser);
    }

    public boolean removeIdentityUserByEmail(String email) {
        
        if (!containsEmail(email))
            return false;

        return identityUsers.remove(getIdentityUserByEmail(email).get());

    }

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

    public Optional<Skill> getSkillById(int id) {

        return skills
            .stream()
                .filter(skill -> skill.getSkillId() == id)
                .findAny();

    }

    public boolean containsSkillById(int id) {
        return getSkillById(id).isPresent();
    }

    public boolean createSkill(Skill skill) {

        if (containsSkillById(skill.getSkillId()))
            return false;

        skills.add(skill);

        return true;

    }

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
    
    public Optional<ProfArea> getProfAreaById(int id) {

        return profAreas
            .stream()
                .filter(profArea -> profArea.getProfAreaId() == id)
                .findAny();

    }

    public boolean containsProfAreaById(int id) {
        return getProfAreaById(id).isPresent();
    }

    public boolean createProfArea(ProfArea profArea) {

        if (containsProfAreaById(profArea.getProfAreaId()))
            return false;

        profAreas.add(profArea);

        return true;

    }

    public boolean removeProfAreaById(int id) {

        if (!containsProfAreaById(id))
            return false;

        profAreas.remove(getProfAreaById(id).get());

        return true;

    }

    public void commit() throws Exception {
        serialize(this, DATA_FILE);        
    }

    // Crud Invites

    public Collection<Invitation> getInvites() {
        return invites;
    }

    public boolean containsInvitationWithOwner(String emailOwner) {

        return invites
            .stream()
                .filter(invite -> invite.getEmailFrom().equals(emailOwner))
                .findAny().isPresent();

    }

    public Optional<Invitation> getInvitationByOwner(String emailOwner) {

        return invites
            .stream()
                .filter(invite -> invite.getEmailFrom().equals(emailOwner))
                .findAny();

    }

    public boolean createInvite(Invitation invitation) {
        return invites.add(invitation);
    }

    public boolean removeInvite(Invitation invitation) {
        return invites.remove(invitation);
    }

    @PreDestroy()
    public void close() throws Exception {
        commit();
    }

}
