package sem.users.models;

import lombok.Data;

/**
 * Model representing a registration request.
 */
@Data
public class MembershipRequestModel {
    private String hoaId;
    private String username;
    private String roleType;
}