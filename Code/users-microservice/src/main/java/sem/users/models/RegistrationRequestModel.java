package sem.users.models;

import lombok.Data;

/**
 * Model representing a registration request.
 */
@Data
public class RegistrationRequestModel {
    private String username;
    private String password;
    private String fullname;
}