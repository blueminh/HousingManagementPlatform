package sem.voting.domain.services.validators;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import sem.voting.authentication.AuthManager;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;

public class MemberIsAddingThemselvesValidator extends Validator {
    @Autowired
    @Getter
    AuthManager authManager;

    @Override
    public boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException {
        if (!username.equals(authManager.getUserId())) {
            throw new InvalidRequestException("A user can only candidate themselves");
        }
        return super.checkNext(username, option, proposal);
    }
}
