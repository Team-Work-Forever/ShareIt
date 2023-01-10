package shareit.services;

import shareit.contracts.auth.AuthenticationRequest;
import shareit.contracts.auth.RegisterRequest;
import shareit.data.auth.IdentityUser;
import shareit.errors.auth.AuthenticationException;

public interface Authentication {
    
    /**
     * Verifies if user have an AuthToken
     * @return true if an user has an AuthToken
    */
    boolean isBeforeAuthenticated();

    /**
     * Authenticate with AuthTokens
     * @return true if user log's in
     * @throws Exception
     */
    boolean authenticateWithToken() throws Exception;

    /**
     * Verifies if exists an user authenticated
     * @return true if user is authenticated
     */
    boolean isAuthenticated();

    /**
     * Returns the current authenticated user
     * @return IdentityUser
     */
    IdentityUser getAuthenticatedUser();

    /**
     * Log an user in the system
     * @param request Given AuthenticationRequest
     * @return true if an user can successfully log in
     * @throws Exception
     */
    boolean login(AuthenticationRequest request) throws Exception;

    /**
     * Register an user in the system
     * @param request Given RegisterRequest
     * @return true if user can successfully register
     * @throws Exception
     */
    boolean signIn(RegisterRequest request) throws Exception;

    /**
     * Log's out an user
     * @throws Exception
     */
    void logout() throws Exception;

    /**
     * Modifies Role of user
     * @param role Given Role (ADMIN, COMPANYINSTITUTION, USERMANAGER, USER)
     * @param identityUser Given User
     * @return true if Role of User is modified successfully
     * @throws AuthenticationException
     */
    boolean alterPrivilege(String role, IdentityUser identityUser) throws AuthenticationException;

}
