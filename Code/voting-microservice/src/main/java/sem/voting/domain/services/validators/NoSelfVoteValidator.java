package sem.voting.domain.services.validators;

import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;

public class NoSelfVoteValidator extends BaseValidator {
    @Override
    public boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException {
        if (username.equals(option.toString())) {
            throw new InvalidRequestException("Users can't vote for themselves");
        }
        return super.checkNext(username, option, proposal);
    }
}
