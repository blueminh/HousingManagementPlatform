package sem.voting.domain.services.validators;

import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

public class MemberIsNotBoardMemberOfAnyHoaValidator extends Validator {
    @Override
    public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
        try {
            if (!HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(vote.getVoter())) {
                throw new InvalidRequestException("User is already a board member of another HOA");
            }
            return super.checkNext(vote, proposal);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
