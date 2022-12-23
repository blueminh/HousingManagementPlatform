package sem.voting.domain.services.validators;

import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.services.implementations.BoardElectionsVoteValidationService;

public class NoBoardElectionValidator extends BaseValidator {
    private transient ProposalHandlingService proposalHandlingService;

    public NoBoardElectionValidator(ProposalHandlingService phs) {
        this.proposalHandlingService = phs;
    }

    @Override
    public boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException {
        for (Proposal prop : proposalHandlingService.getActiveProposals(proposal.getHoaId())) {
            if (prop.getVoteValidationService() instanceof BoardElectionsVoteValidationService) {
                throw new InvalidRequestException("A board election is ongoing");
            }
        }
        return super.checkNext(username, option, proposal);
    }
}
