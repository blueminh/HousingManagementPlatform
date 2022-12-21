package sem.voting.domain.services.validators;

import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

public class IsAffirmativeOrNegativeValidator extends Validator {
    private static final String affirmativeVote = "Yes";
    private static final String negativeVote = "No";

    @Override
    public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
        String option = vote.getChoice().toString();
        if (option.equals(affirmativeVote) || option.equals(negativeVote)) {
            return super.checkNext(vote, proposal);
        }
        throw new InvalidRequestException("The only valid options are " + affirmativeVote + " or " + negativeVote);
    }
}
