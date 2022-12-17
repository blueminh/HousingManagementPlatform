package sem.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model representing a response sending a full name of a user.
 */
@Data
@AllArgsConstructor
public class FullnameResponseModel {
    private String fullname;
}
