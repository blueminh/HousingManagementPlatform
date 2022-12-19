package sem.voting.domain.services.implementations;

import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.VoteValidationService;
import sem.voting.domain.services.validators.InvalidRequestException;
import sem.voting.domain.services.validators.NoSelfVoteValidator;
import sem.voting.domain.services.validators.UserIsMemberOfThisHoaValidator;
import sem.voting.domain.services.validators.Validator;

public class BoardElectionsVoteValidationService implements VoteValidationService {
    private static final long serialVersionUID = 0L;

    @Override
    public boolean isVoteValid(Vote vote, Proposal proposal) {
        Validator validator = new UserIsMemberOfThisHoaValidator();
        validator.setNext(new NoSelfVoteValidator());
        try {
            return validator.handle(vote, proposal);
        } catch (InvalidRequestException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
