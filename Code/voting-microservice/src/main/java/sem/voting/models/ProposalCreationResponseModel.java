package sem.voting.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model to represent a response to the creation of a proposal.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalCreationResponseModel {
    private int proposalId;
}
