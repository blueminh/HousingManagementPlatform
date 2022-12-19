package sem.voting.domain.services.validators;

import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

public class UserIsMemberOfThisHoaValidator extends Validator {
    @Override
    public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
        try {
            if (!HoaCommunication.checkUserIsMemberOfThisHoa(vote.getVoter(), proposal.getHoaId(), vote.getVoterToken())) {
                throw new InvalidRequestException("User is not a member of this HOA");
            }
            return super.checkNext(vote, proposal);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
