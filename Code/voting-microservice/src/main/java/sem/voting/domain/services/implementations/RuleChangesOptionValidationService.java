package sem.voting.domain.services.implementations;

import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.services.OptionValidationService;

public class RuleChangesOptionValidationService implements OptionValidationService {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isOptionValid(Option option, Proposal proposal) {
        // TODO implement checks here
        return false;
    }
}
