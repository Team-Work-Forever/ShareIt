package shareit.contracts.auth;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import shareit.data.auth.IdentityUser;

public class RegisterRequest {
    
    @NotEmpty 
    @Email 
    private String email;

    @NotEmpty 
    @Size(min = 8, max = 16) 
    private String password;

    @NotEmpty
    private String name;

    private String lastName;
    
    private LocalDate bornDate;

    @NotEmpty
    private String street;

    @NotEmpty
    private String postCode;

    @NotEmpty
    private String locality;

    @NotEmpty
    private String country;

    private boolean isPublic;

    @Pattern(regexp = "Admin|UserManager|User", message = "Please use one of the roles available")
    private String role;

    public RegisterRequest(String email, String password, 
        String name, String lastName, LocalDate bornDate, String street,
        String postCode, String locality, 
        String country, boolean isPublic, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.bornDate = bornDate;
        this.street = street;
        this.postCode = postCode;
        this.locality = locality;
        this.country = country;
        this.isPublic = isPublic;
        this.role = role;
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

    public LocalDate getBornDate() {
        return bornDate;
    }

    public void setBornDate(LocalDate bornDate) {
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

    public IdentityUser toIdentityUser() {

        return new IdentityUser(
            email, 
            password, 
            name, 
            lastName, 
            bornDate, 
            street, 
            postCode, 
            locality, 
            country,
            isPublic, 
            role
        );

    }

}
