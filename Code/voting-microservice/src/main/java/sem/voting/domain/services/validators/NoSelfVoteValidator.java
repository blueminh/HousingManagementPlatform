package sem.voting.domain.services.validators;

import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;

public class NoSelfVoteValidator extends Validator {
    @Override
    public boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException {
        if (proposal.getAvailableOptions().contains(new Option(username))) {
            throw new InvalidRequestException("A user can't vote for himself");
        }
        return super.checkNext(username, option, proposal);
    }
}
