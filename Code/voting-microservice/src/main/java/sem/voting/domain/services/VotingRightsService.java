package sem.voting.domain.services;

import java.io.Serializable;
import sem.voting.domain.proposal.Proposal;

public interface VotingRightsService extends Serializable {
    boolean canVote(int userId, Proposal proposal);
}
