package sem.voting.models;

import lombok.Data;
import sem.voting.domain.proposal.ProposalStage;

/**
 * Model of the response to start voting on a proposal.
 */
@Data
public class ProposalStartVotingResponseModel {
    private int proposalId;
    private int hoaId;
    private ProposalStage status;
}
