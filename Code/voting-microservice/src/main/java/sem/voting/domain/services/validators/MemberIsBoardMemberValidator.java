package sem.voting.domain.services.validators;

import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;

public class MemberIsBoardMemberValidator extends Validator {
    @Override
    public boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException {
        try {
            if (!HoaCommunication.checkUserIsBoardMember(username, proposal.getHoaId())) {
                throw new InvalidRequestException("User is not a board member of this HOA");
            }
            return super.checkNext(username, option, proposal);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
