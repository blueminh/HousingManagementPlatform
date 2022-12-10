package sem.voting.domain.services.implementations;

import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.services.VotingRightsService;

public class RuleChangesVotingRightsService implements VotingRightsService {
    private static final long serialVersionUID = 0L;

    @Override
    public boolean canVote(int userId, Proposal proposal) {
        // ToDo: check if user is on the HOA board
        return false;
    }
}
