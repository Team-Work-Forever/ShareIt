package shareit.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import shareit.data.auth.IdentityUser;
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

    public GlobalRepository() throws Exception {

        this.identityUsers = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.profAreas = new ArrayList<>();

        if (StoreUtils.verifyFile(DATA_FILE))
        {
            identityUsers = ((GlobalRepository)deserialize(DATA_FILE)).getIdentityUsers();
            skills = ((GlobalRepository)deserialize(DATA_FILE)).getSkills();
            profAreas = ((GlobalRepository)deserialize(DATA_FILE)).getProfAreas();
            authToken = ((GlobalRepository)deserialize(DATA_FILE)).getAuthToken();
        }
        else
            commit();
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

    public boolean updateIdentityUserByEmail(String email, IdentityUser identityUser) {

        if (containsEmail(email))
            return false;

        removeSkillByName(email);
        createIdentityUser(identityUser);

        return true;

    }

    // Crud Skill

    public Collection<Skill> getSkills() {
        return skills;
    }

    public Optional<Skill> getSkillByName(String name) {

        return skills
            .stream()
                .filter(skill -> skill.getName().equals(name))
                .findAny();

    }

    public boolean containsSkillName(String name) {
        return getSkillByName(name).isPresent();
    }

    public boolean createSkill(Skill skill) {

        if (containsSkillName(skill.getName()))
            return false;

        skills.add(skill);

        return true;

    }

    public boolean removeSkillByName(String name) {

        if (!containsSkillName(name))
            return false;

        skills.remove(getSkillByName(name).get());

        return true;

    }

    // Crud ProfArea

    public Collection<ProfArea> getProfAreas() {
        return profAreas;
    }
    
    public Optional<ProfArea> getProfAreaByName(String name) {

        return profAreas
            .stream()
                .filter(profArea -> profArea.getName().equals(name))
                .findAny();

    }

    public boolean containsProfAreaName(String name) {
        return getSkillByName(name).isPresent();
    }

    public boolean createProfArea(ProfArea profArea) {

        if (containsSkillName(profArea.getName()))
            return false;

        profAreas.add(profArea);

        return true;

    }

    public boolean removeProfAreaByName(String name) {

        if (!containsProfAreaName(name))
            return false;

        profAreas.remove(getProfAreaByName(name).get());

        return true;

    }

    public void commit() throws Exception {
        serialize(this, DATA_FILE);        
    }

}
