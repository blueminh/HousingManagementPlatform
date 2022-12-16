package sem.voting.domain.services.validators;

import sem.voting.communication.HOACommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

public class UserIsMemberForAtLeast10YearsValidators extends Validator{
  @Override
  public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
    try {
      // TODO: what the fuck is 10 years
      return HOACommunication.getJoiningDate(vote.getVoter(), proposal.getHoaId()) >= 0;
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
  }
}
