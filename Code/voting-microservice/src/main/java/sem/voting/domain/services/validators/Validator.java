package sem.voting.domain.services.validators;

import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;

public interface Validator {
    void addLast(Validator validator);

    boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException;
}
