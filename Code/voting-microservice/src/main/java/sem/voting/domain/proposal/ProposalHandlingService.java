package sem.voting.domain.proposal;

import org.springframework.stereotype.Service;

/**
 * DDD Service to handle proposals.
 */
@Service
public class ProposalHandlingService {
    private final transient ProposalRepository proposalRepository;

    /**
     * Constructor of service.
     *
     * @param proposalRepository repository of proposals
     */
    public ProposalHandlingService(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    /**
     * Check if proposal with given id refers to given HOA.
     *
     * @param proposalId Id of the proposal
     * @param hoaId Id of the HOA
     * @return true if the proposal refers to the HOA
     */
    public boolean checkHoa(int proposalId, int hoaId) {
        Proposal p = proposalRepository.findById(proposalId).orElseGet(null);
        if (p == null) {
            return false;
        }
        return p.getHoaId() == hoaId;
    }
}
