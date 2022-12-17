package sem.voting.domain.services.validators;

import sem.voting.communication.HOACommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UserIsMemberForAtLeast3YearsValidator extends Validator{
  @Override
  public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
    try {
      long joiningDate = HOACommunication.getJoiningDate(vote.getVoter(), proposal.getHoaId(), vote.getVoterToken());
      long duration = new Date().getTime() - joiningDate;
      // 1 year is 365 days
      return TimeUnit.MILLISECONDS.toDays(duration) / 365 >= 3;
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
  }
}
