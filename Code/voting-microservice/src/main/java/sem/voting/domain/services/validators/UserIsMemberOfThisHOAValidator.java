package sem.voting.domain.services.validators;

import sem.voting.communication.HOACommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

public class UserIsMemberOfThisHOAValidator extends Validator{
  @Override
  public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
    try {
      return HOACommunication.checkUserIsMemberOfThisHOA(vote.getVoter(), proposal.getHoaId(), vote.getVoterToken());
    } catch (Exception e){
      throw new InvalidRequestException(e.getMessage());
    }
  }
}
