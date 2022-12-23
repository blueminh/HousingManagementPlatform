package sem.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.users.domain.user.FullName;
import sem.users.domain.user.Password;
import sem.users.models.ChangeUserInfoRequestModel;
import sem.users.domain.user.UserNotFoundException;
import sem.users.domain.user.Username;
import sem.users.models.AuthenticationRequestModel;
import sem.users.models.FullnameRequestModel;
import sem.users.models.FullnameResponseModel;
import sem.users.services.UserInfoService;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

@RestController
public class UserInfoController {

    private final transient UserInfoService userInfoService;

    @Autowired
    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * Endpoint for fetching the full name of a user.
     *
     * @param request The request model containing the username.
     *
     * @return The full name of the user if it was found.
     * @throws ResponseStatusException Exception if the user was not found.
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @PostMapping("/getfullname")
    public ResponseEntity<FullnameResponseModel> getFullName(@RequestBody FullnameRequestModel request) throws ResponseStatusException {
        final String fullname;
        try {
            Username username = new Username(request.getUsername());
            fullname = userInfoService.getFullname(username).toString();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok(new FullnameResponseModel(fullname));
    }

    /**
     * API endpoint to check if a user already exists in the system.
     *
     * @param request username to check
     * @return OK if the user already exists, BAD REQUEST if the user does not exist in the system.
     */
    @PostMapping("/userexists")
    public ResponseEntity userExists(@RequestBody AuthenticationRequestModel request) {
        Username username = new Username(request.getUsername());
        if (userInfoService.userExists(username)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();

    }

    /**
     * Method to change a user's full name.
     *
     * @param request a ChangeUserInfoRequestModel, containing the user's current credentials and the new full name
     * @return OK if successful, BAD_REQUEST or UNAUTHORIZED otherwise
     */
    @PostMapping("/changefullname")
    public ResponseEntity changeFullName(@RequestBody ChangeUserInfoRequestModel request) {

        try {
            userInfoService.changeFullName(new Username(request.getUsername()), new Password(request.getPassword()), new FullName(request.getNewAttribute()));
        } catch (InvalidParameterException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER NOT FOUND", e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e);
        }

        return ResponseEntity.ok().build();
    }

}
