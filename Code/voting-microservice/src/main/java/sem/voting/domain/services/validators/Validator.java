package sem.voting.domain.services.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.Vote;


/**
 * x - check if an election is going for a HOA ID: hoaID
 * - check if a user is a board member: username, hoaID
 * - check if a user is a board member of any hoas: username
 * - check how long have a user been a member: username, hoaID
 * - check how long have a user been a board member: username, hoaID
 * x - member cannot vote for themselves: username
 */
public abstract class Validator {
  private Validator next;

  public void setNext(Validator validator) {
    this.next = validator;
  };

  protected boolean checkNext(Vote vote, Proposal proposal) throws InvalidRequestException {
    if (next == null)
      return true;
    return next.handle(vote, proposal);
  }

  public abstract boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException;
}

