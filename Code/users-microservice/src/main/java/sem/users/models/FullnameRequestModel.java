package sem.users.models;

import lombok.Data;

/**
 * Model representing a request for the full name of a user.
 */
@Data
public class FullnameRequestModel {
    private String username;
}
