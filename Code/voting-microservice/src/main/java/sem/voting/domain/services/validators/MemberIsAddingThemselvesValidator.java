package sem.voting.domain.services.validators;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import sem.voting.authentication.AuthManager;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

public class MemberIsAddingThemselvesValidator extends Validator {
    @Autowired
    @Getter
    AuthManager authManager;

    @Override
    public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
        if (!vote.getVoter().equals(authManager.getUserId())) {
            throw new InvalidRequestException("A user can only candidate themselves");
        }
        return super.checkNext(vote, proposal);
    }
}
