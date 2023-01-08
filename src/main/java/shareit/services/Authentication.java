package shareit.services;

import shareit.contracts.auth.AuthenticationRequest;
import shareit.contracts.auth.RegisterRequest;
import shareit.data.auth.IdentityUser;
import shareit.errors.auth.AuthenticationException;

public interface Authentication {
    
    boolean isBeforeAuthenticated();
    boolean authenticateWithToken() throws Exception;
    boolean isAuthenticated();
    IdentityUser getAuthenticatedUser();
    boolean login(AuthenticationRequest request) throws Exception;
    boolean signIn(RegisterRequest request) throws Exception;
    public void logout() throws Exception;
    boolean alterPrivilege(String role, IdentityUser identityUser) throws AuthenticationException;

}
