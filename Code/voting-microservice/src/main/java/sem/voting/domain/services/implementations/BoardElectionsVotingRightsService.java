package sem.voting.domain.services.implementations;

import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.services.VotingRightsService;

public class BoardElectionsVotingRightsService implements VotingRightsService {
    private static final long serialVersionUID = 0L;

    @Override
    public boolean canVote(String username, Proposal proposal) {
        // ToDo: check if user is member of HOA
        return true;
    }
}
