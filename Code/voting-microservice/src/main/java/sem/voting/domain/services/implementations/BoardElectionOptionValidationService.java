package sem.voting.domain.services.implementations;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import sem.voting.authentication.AuthManager;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.OptionValidationService;
import sem.voting.domain.services.validators.BoardMemberForLess10YearsValidator;
import sem.voting.domain.services.validators.InvalidRequestException;
import sem.voting.domain.services.validators.MemberIsAddingThemselvesValidator;
import sem.voting.domain.services.validators.MemberIsNotBoardMemberOfAnyHoaValidator;
import sem.voting.domain.services.validators.UserIsMemberForAtLeast3YearsValidator;
import sem.voting.domain.services.validators.UserIsMemberOfThisHoaValidator;
import sem.voting.domain.services.validators.Validator;

public class BoardElectionOptionValidationService implements OptionValidationService {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isOptionValid(String userId, Option option, Proposal proposal) {
        Validator validator = new UserIsMemberOfThisHoaValidator();
        validator.addLast(new MemberIsAddingThemselvesValidator());
        validator.addLast(new MemberIsNotBoardMemberOfAnyHoaValidator());
        validator.addLast(new UserIsMemberForAtLeast3YearsValidator());
        validator.addLast(new BoardMemberForLess10YearsValidator());
        try {
            return validator.handle(userId, option, proposal);
        } catch (InvalidRequestException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
