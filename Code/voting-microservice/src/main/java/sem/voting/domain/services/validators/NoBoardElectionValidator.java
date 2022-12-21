package sem.voting.domain.services.validators;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.services.implementations.BoardElectionsVoteValidationService;

public class NoBoardElectionValidator extends Validator {
    private ProposalHandlingService proposalHandlingService;

    public NoBoardElectionValidator(ProposalHandlingService phs) {
        this.proposalHandlingService = phs;
    }

    @Override
    public boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException {
        for (Proposal proposal1 : proposalHandlingService.getActiveProposals(proposal.getHoaId())) {
            if (proposal1.getVoteValidationService() instanceof BoardElectionsVoteValidationService) {
                throw new InvalidRequestException("A board election is ongoing");
            }
        }
        return super.checkNext(username, option, proposal);
    }
}
