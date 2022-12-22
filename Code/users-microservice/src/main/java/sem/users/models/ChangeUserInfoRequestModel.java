package sem.users.models;

import lombok.Data;

/**
 * Model representing a request to change user info (to be specified by the endpoint).
 */
@Data
public class ChangeUserInfoRequestModel {
    private String username;
    private String password;
    private String newAttribute;
}
