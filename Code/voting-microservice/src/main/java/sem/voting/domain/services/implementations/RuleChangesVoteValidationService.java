package sem.voting.domain.services.implementations;

import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.VoteValidationService;
import sem.voting.domain.services.validators.InvalidRequestException;
import sem.voting.domain.services.validators.MemberIsBoardMemberValidator;
import sem.voting.domain.services.validators.Validator;

public class RuleChangesVoteValidationService implements VoteValidationService {
    private static final long serialVersionUID = 0L;

    @Override
    public boolean isVoteValid(Vote vote, Proposal proposal) {
        // ToDo: check if user is board member of HOA
        Validator validator = new MemberIsBoardMemberValidator();
        try {
            return validator.handle(vote, proposal);
        } catch (InvalidRequestException e) {
            return false;
        }
    }
}
