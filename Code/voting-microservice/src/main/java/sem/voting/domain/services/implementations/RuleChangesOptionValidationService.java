package sem.voting.domain.services.implementations;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import sem.voting.authentication.AuthManager;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.OptionValidationService;
import sem.voting.domain.services.validators.InvalidRequestException;
import sem.voting.domain.services.validators.IsAffirmativeOrNegativeValidator;
import sem.voting.domain.services.validators.MemberIsBoardMemberValidator;
import sem.voting.domain.services.validators.Validator;

public class RuleChangesOptionValidationService implements OptionValidationService {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isOptionValid(String userId, Option option, Proposal proposal) {
        Validator validator = new MemberIsBoardMemberValidator();
        validator.addLast(new IsAffirmativeOrNegativeValidator());
        try {
            return validator.handle(userId, option, proposal);
        } catch (InvalidRequestException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
