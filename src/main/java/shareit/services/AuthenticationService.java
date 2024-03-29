package shareit.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

import shareit.contracts.auth.AuthenticationRequest;
import shareit.contracts.auth.RegisterRequest;
import shareit.data.auth.IdentityUser;
import shareit.data.auth.Role;
import shareit.errors.auth.AuthenticationException;
import shareit.repository.GlobalRepository;
import shareit.validator.BeanValidator;

public class AuthenticationService implements Authentication {

    private final BeanValidator<AuthenticationRequest> validatorAuthRequest = new BeanValidator<>();
    private final BeanValidator<RegisterRequest> validatorRegisterRequest = new BeanValidator<>();

    @Autowired
    private GlobalRepository globalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private IdentityUser currentUser;

    @Override
    public boolean isBeforeAuthenticated() {
        return globalRepository.getAuthToken() != null;
    }

    public boolean authenticateWithToken() throws Exception {

        String userEmail = globalRepository.getAuthToken().getKey();

        Optional<IdentityUser> validateUser = globalRepository.getIdentityUserByEmail(userEmail);

        if (!validateUser.isPresent())
            throw new AuthenticationException("User not found!");

        currentUser = validateUser.get();

        return true;

    }

    @Override
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    @Override
    public IdentityUser getAuthenticatedUser() {
        return isAuthenticated() ? currentUser : null;
    }

    @Override
    public boolean login(@Validated AuthenticationRequest request) throws Exception {
        
        // validation
        var errors = validatorAuthRequest.validate(request);

        if (!errors.isEmpty())
        {
            throw new AuthenticationException(errors.iterator().next().getMessage());
        }
        
        // Utilizar um mapper
        Optional<IdentityUser> validateUser = globalRepository.getIdentityUserByEmail(request.getEmail());

        if (!validateUser.isPresent())
            throw new AuthenticationException("User not found!");

        // Validate Password
        if (!passwordEncoder.matches(request.getPassword(), validateUser.get().getPassword()))
            throw new AuthenticationException("Password is incorrect!");

        // Generate Token
        var token = UUID.randomUUID().toString();

        globalRepository.setTokenPair(validateUser.get().getEmail(), token);
        globalRepository.commit();

        currentUser = validateUser.get();

        return true;

    }

    @Override
    public boolean signIn(@Validated RegisterRequest request) throws Exception {
        
        var errors = validatorRegisterRequest.validate(request);

        if (!errors.isEmpty())
        {
            throw new AuthenticationException(errors.iterator().next().getMessage());
        }

        IdentityUser signUser = request.toIdentityUser();
        
        // Validate Email
        if(globalRepository.containsEmail(request.getEmail()))
            throw new AuthenticationException("This email already exists!");
        
        // Encrypt Password
        signUser.setPassword(passwordEncoder.encode(signUser.getPassword()));

        globalRepository.createIdentityUser(signUser);
        globalRepository.commit();

        return true;
    }

    public void logout() throws Exception {

        globalRepository.clearAuthToken();
        globalRepository.commit();

        currentUser = null;

    }

    @Override
    public boolean alterPrivilege(String role, IdentityUser identityUser) throws AuthenticationException {
        
        if (!getAuthenticatedUser().getRole().equals(Role.ADMIN)) {
            throw new AuthenticationException("You don't have permition to change roles!");
        }

        if (identityUser.getRole().equals(role)) {
            throw new AuthenticationException("This user already as assigned this role!");
        }

        identityUser.setRole(role);

        return true;

    }

}
