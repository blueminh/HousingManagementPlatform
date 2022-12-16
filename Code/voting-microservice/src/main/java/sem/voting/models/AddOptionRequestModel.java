package sem.voting.models;

import lombok.Data;

/**
 * Model representing a request to add an option to a proposal.
 */
@Data
public class AddOptionRequestModel {
    private int proposalId;
    private int hoaId;
    private String option;
}
