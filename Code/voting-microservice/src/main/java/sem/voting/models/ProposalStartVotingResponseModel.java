package sem.voting.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sem.voting.domain.proposal.ProposalStage;

/**
 * Model of the response to start voting on a proposal.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProposalStartVotingResponseModel {
    private int proposalId;
    private int hoaId;
    private ProposalStage status;

}
