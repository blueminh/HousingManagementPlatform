package sem.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.users.domain.user.RegistrationService;
import sem.users.domain.user.UserNotFoundException;
import sem.users.domain.user.Username;
import sem.users.models.FullnameRequestModel;
import sem.users.models.FullnameResponseModel;

@RestController
public class UserInfoController {
    private final transient RegistrationService registrationService;

    @Autowired
    public UserInfoController(RegistrationService registrationService) {
        this.registrationService = registrationService;
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
            fullname = registrationService.getFullname(username).toString();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok(new FullnameResponseModel(fullname));
    }

}
