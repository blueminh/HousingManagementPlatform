package sem.voting.domain.services.implementations;

import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.VoteValidationService;
import sem.voting.domain.services.validators.MemberIsBoardMemberValidator;

public class BoardElectionsVoteValidationService implements VoteValidationService {
    private static final long serialVersionUID = 0L;

    @Override
    public boolean isVoteValid(Vote vote, Proposal proposal) {
        // ToDo: check if user is member of HOA
        // ToDo: check if user is voting for themselves
        return true;
    }
}
