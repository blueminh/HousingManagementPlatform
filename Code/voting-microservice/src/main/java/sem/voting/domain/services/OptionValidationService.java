package sem.voting.domain.services;

import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;

import java.io.Serializable;

public interface OptionValidationService extends Serializable {
    boolean isOptionValid(String userId, Option option, Proposal proposal);
}
