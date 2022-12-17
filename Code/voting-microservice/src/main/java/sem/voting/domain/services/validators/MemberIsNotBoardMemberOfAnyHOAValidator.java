package sem.voting.domain.services.validators;

import sem.voting.communication.HOACommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

public class MemberIsNotBoardMemberOfAnyHOAValidator extends Validator{
  @Override
  public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {

    boolean isBoardMemeberOfAny = false;
    try {
      isBoardMemeberOfAny = HOACommunication.checkUserIsNotBoardMemberOfAnyHoa(vote.getVoter(), vote.getVoterToken());
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
    return isBoardMemeberOfAny;
  }
}
