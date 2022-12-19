package sem.voting.domain.services.validators;

import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.Vote;

public class NoSelfVoteValidator extends Validator {
    @Override
    public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
        if (proposal.getAvailableOptions().contains(vote.getChoice())) {
            throw new InvalidRequestException("A user can't vote for himself");
        }
        return super.checkNext(vote, proposal);
    }
}
