package sem.voting.domain.services.validators;

import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

public class MemberIsBoardMemberValidator extends Validator {
    @Override
    public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
        // communicate with the HOA microservice
        boolean isBoardMemeber = false;
        try {
            isBoardMemeber = HoaCommunication.checkUserIsBoardMember(vote.getVoter(), proposal.getHoaId(), vote.getVoterToken());
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
        return isBoardMemeber;
    }
}
