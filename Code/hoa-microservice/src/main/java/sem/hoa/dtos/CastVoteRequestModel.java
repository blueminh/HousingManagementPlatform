package sem.hoa.dtos;

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

    /**
     * Constructor for a cast vote request.
     *
     * @param proposalId proposalID
     * @param hoaId hoaID
     * @param username username
     * @param option option voted
     */
    public CastVoteRequestModel(int proposalId, int hoaId, String username, String option) {
        this.proposalId = proposalId;
        this.hoaId = hoaId;
        this.username = username;
        this.option = option;
    }
}
