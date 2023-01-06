package shareit.data.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import shareit.data.Talent;
import shareit.errors.TalentException;

public class IdentityUser implements Serializable {
    
    private String email;
    private String password;
    private String name;
    private String lastName;
    private Date bornDate;
    private String street;
    private String postCode;
    private String locality;
    private String country;
    private float moneyPerHour;
    private boolean isPublic;
    private String role;

    private Collection<Talent> talents = new ArrayList<>();

    public IdentityUser(String email, String password, String name, String lastName, Date bornDate, String street, String postCode, String locality,
        String country, float moneyPerHour, boolean isPublic, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.bornDate = bornDate;
        this.street = street;
        this.postCode = postCode;
        this.locality = locality;
        this.country = country;
        this.moneyPerHour = moneyPerHour;
        this.isPublic = isPublic;
        this.role = role;
    }

    public IdentityUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public float getMoneyPerHour() {
        return moneyPerHour;
    }

    public void setMoneyPerHour(float moneyPerHour) {
        this.moneyPerHour = moneyPerHour;
    }  
      
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

        public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBornDate() {
        return bornDate;
    }

    public void setBornDate(Date bornDate) {
        this.bornDate = bornDate;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
    
    public Collection<Talent> getTalents() {
        return talents;
    }

    public void setTalents(Collection<Talent> talents) {
        this.talents = talents;
    }

    public void addTalent(Talent talento) {

        boolean found = false;

        for (Talent tal : talents) {
            if (tal.getName().equals(talento.getName())) {
                found = true;
            }
        }

        if (found)
            throw new TalentException("You already associated this talent!");

        talents.add(talento);

    }

    public Talent getTalentByName(String name) {

        for (Talent talento : talents) {
            if(talento.getName().equals(name))
            {
                return talento;
            }
        }

        throw new TalentException("Talento com nome " + name + " não existe!");

    }

    public void removeTalent(String name) throws TalentException {

        Talent talent = getTalentByName(name);

        talents.remove(talent);

    }

    @Override
    public String toString() {
        return "Member: " + "\n" + 
            "\tEmail: " + email + "\n" + 
            "\tName: " + name + "\n" +
            "\tLastName: " + lastName
            ;
    }

}
