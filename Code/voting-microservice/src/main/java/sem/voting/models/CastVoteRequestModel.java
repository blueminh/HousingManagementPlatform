package sem.voting.models;

import lombok.Data;

/**
 * Model to represent a request to add a vote to a proposal.
 */
@Data
public class CastVoteRequestModel {
    private int proposalId;
    private int hoaId;
    private String username;
    private String option;
}
