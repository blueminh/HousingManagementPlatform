package sem.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a response sending a full name of a user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullnameResponseModel {
    private String fullname;
}
