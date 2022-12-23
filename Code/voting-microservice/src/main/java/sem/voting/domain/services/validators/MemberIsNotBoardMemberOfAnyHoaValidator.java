package sem.voting.domain.services.validators;

import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;

public class MemberIsNotBoardMemberOfAnyHoaValidator extends BaseValidator {
    @Override
    public boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException {
        try {
            if (!HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(username, proposal.getHoaId())) {
                throw new InvalidRequestException("User is already a board member of another HOA");
            }
            return super.checkNext(username, option, proposal);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
