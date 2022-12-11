package sem.voting.models;

import lombok.Data;

/**
 * Model to request information about a specific proposal.
 */
@Data
public class ProposalInformationRequestModel {
    private int proposalId;
    private int hoaId;
}
