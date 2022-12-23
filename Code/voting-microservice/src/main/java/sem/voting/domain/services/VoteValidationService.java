package sem.voting.domain.services;

import java.io.Serializable;

import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

public interface VoteValidationService extends Serializable {
    boolean isVoteValid(Vote vote, Proposal proposal);
}
