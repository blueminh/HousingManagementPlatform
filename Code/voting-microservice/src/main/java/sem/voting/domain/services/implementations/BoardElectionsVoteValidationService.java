package sem.voting.domain.services.implementations;

import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.VoteValidationService;
import sem.voting.domain.services.validators.*;

public class BoardElectionsVoteValidationService implements VoteValidationService {
    private static final long serialVersionUID = 0L;

    @Override
    public boolean isVoteValid(Vote vote, Proposal proposal) {
        Validator validator = new UserIsMemberOfThisHOAValidator();
        validator.setNext(new NoSelfVoteValidator());
        try {
            return validator.handle(vote, proposal);
        } catch (InvalidRequestException e){
            return false;
        }
    }
}
