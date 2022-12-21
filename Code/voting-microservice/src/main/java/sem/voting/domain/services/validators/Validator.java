package sem.voting.domain.services.validators;

import lombok.Getter;
import lombok.Setter;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;


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
    private Validator next = null;

    protected boolean checkNext(String username, Option option, Proposal proposal) throws InvalidRequestException {
        if (next == null) {
            return true;
        }
        return next.handle(username, option, proposal);
    }

    /**
     * Add a Validator at the end of the chain.
     *
     * @param next validator to add last.
     */
    public void addLast(Validator next) {
        Validator curr = this;
        while (curr.next != null) {
            curr = curr.next;
        }
        curr.setNext(next);
    }

    public abstract boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException;
}

