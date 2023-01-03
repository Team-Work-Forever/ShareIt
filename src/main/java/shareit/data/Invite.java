package shareit.data;

import java.io.Serializable;

public class Invite implements Serializable {
    
    private static int increment = 1;
    private int id;
    private Experience experience;
    private String email;

    public Invite(Experience experience, String email) {

        id = Invite.increment;

        this.experience = experience;
        this.email = email;

        Invite.increment++;
    }

    public Experience getexperience() {
        return experience;
    }

    public void setexperience(Experience experience) {
        this.experience = experience;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

    public static int getIncrement() {
        return increment;
    }

    public static void setIncrement(int increment) {
        Invite.increment = increment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Experience getExperience() {
        return experience;
    }

    @Override
    public String toString() {
        return "Invite : " + "\n" +
            "Id: " + this.id + "\n" +
            experience.toString();
    }

}
