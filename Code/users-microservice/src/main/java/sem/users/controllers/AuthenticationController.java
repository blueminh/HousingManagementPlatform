package sem.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.users.authentication.JwtTokenGenerator;
import sem.users.authentication.JwtUserDetailsService;
import sem.users.domain.user.FullName;
import sem.users.domain.user.Password;
import sem.users.models.ChangeUserInfoRequestModel;
import sem.users.services.RegistrationService;
import sem.users.domain.user.Username;
import sem.users.models.AuthenticationRequestModel;
import sem.users.models.AuthenticationResponseModel;
import sem.users.models.RegistrationRequestModel;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

@RestController
public class AuthenticationController {

    private final transient AuthenticationManager authenticationManager;

    private final transient JwtTokenGenerator jwtTokenGenerator;

    private final transient JwtUserDetailsService jwtUserDetailsService;

    private final transient RegistrationService registrationService;

    /**
     * Instantiates a new UsersController.
     *
     * @param authenticationManager the authentication manager
     * @param jwtTokenGenerator     the token generator
     * @param jwtUserDetailsService the user service
     * @param registrationService   the registration service
     */
    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtTokenGenerator jwtTokenGenerator,
                                    JwtUserDetailsService jwtUserDetailsService,
                                    RegistrationService registrationService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.registrationService = registrationService;
    }

    /**
     * Endpoint for authentication.
     *
     * @param request The login model
     * @return JWT token if the login is successful
     * @throws Exception if the user does not exist or the password is incorrect
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseModel> authenticate(@RequestBody AuthenticationRequestModel request)
            throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getUsername());
        final String jwtToken = jwtTokenGenerator.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponseModel(jwtToken));
    }

    /**
     * Method to change a user's password.
     *
     * @param request a ChangeUserInfoRequestModel, containing the user's current credentials and the new password
     * @return OK if successful, BAD_REQUEST or UNAUTHORIZED otherwise
     */
    @PostMapping("/changepassword")
    public ResponseEntity changePassword(@RequestBody ChangeUserInfoRequestModel request) {

        try {
            registrationService.changePassword(new Username(request.getUsername()), new Password(request.getPassword()), new Password(request.getNewAttribute()));
        } catch (InvalidParameterException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER NOT FOUND", e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint for registration.
     *
     * @param request The registration model
     * @return 200 OK if the registration is successful
     * @throws Exception if a user with this username already exists
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistrationRequestModel request) throws Exception {

        try {
            Username username = new Username(request.getUsername());
            Password password = new Password(request.getPassword());
            FullName fullname = new FullName(request.getFullname());
            registrationService.registerUser(username, password, fullname);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

        return ResponseEntity.ok().build();
    }


}
