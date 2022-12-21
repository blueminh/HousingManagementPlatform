package sem.voting.domain.services.validators;

import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;

public class IsAffirmativeOrNegativeValidator extends Validator {
    private static final String affirmativeVote = "Yes";
    private static final String negativeVote = "No";

    @Override
    public boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException {
        if (option.toString().equals(affirmativeVote) || option.toString().equals(negativeVote)) {
            return super.checkNext(username, option, proposal);
        }
        throw new InvalidRequestException("The only valid options are " + affirmativeVote + " or " + negativeVote);
    }
}
