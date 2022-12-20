package sem.voting.domain.services.validators;

import lombok.Getter;
import lombok.Setter;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;


///**
// * NEED TO FIND A WAY TO COMMUNICATE.
// *
// * x - check if user is a member of a HOA
// * x - check if an election is going for a HOA ID: hoaID
// * half - check if a user is a board member: username, hoaID
// * half - check if a user is a board member of any hoas: username
// * - check how long have a user been a member: username, hoaID
// * - check how long have a user been a board member: username, hoaID
// * x - member cannot vote for themselves: username
// */
public abstract class Validator {
    @Setter
    @Getter
    private Validator next;

    protected boolean checkNext(Vote vote, Proposal proposal) throws InvalidRequestException {
        if (next == null) {
            return true;
        }
        return next.handle(vote, proposal);
    }

    public abstract boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException;
}

