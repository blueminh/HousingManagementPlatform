package sem.voting.domain.services.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sem.voting.communication.HOACommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.Vote;

public class MemberIsBoardMemberValidator extends Validator{
  @Override
  public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
    // communicate with the HOA microservice
    boolean isBoardMemeber = false;
    try {
      isBoardMemeber = HOACommunication.checkUserIsBoardMember(vote.getVoter(), proposal.getHoaId());
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
    return isBoardMemeber;
  }
}
