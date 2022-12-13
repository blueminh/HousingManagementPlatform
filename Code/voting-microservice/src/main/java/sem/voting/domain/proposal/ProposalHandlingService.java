package sem.voting.domain.proposal;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
     * @param hoaId      Id of the HOA
     * @return true if the proposal refers to the HOA
     */
    public boolean checkHoa(int proposalId, int hoaId) {
        // ToDo check with the service if all permissions are satisfied
        Proposal p = proposalRepository.findById(proposalId).orElseGet(null);
        if (p == null) {
            return false;
        }
        return p.getHoaId() == hoaId;
    }

    /**
     * Save or update a proposal in the repository.
     *
     * @param proposal proposal to save
     * @return updated proposal, as stored in the database
     */
    public Proposal save(Proposal proposal) {
        return this.proposalRepository.save(proposal);
    }

    /**
     * Find a proposal given an id.
     *
     * @param proposalId id of the proposal to find
     * @return Optional of the proposal or empty if the id was not found
     */
    public Optional<Proposal> getProposalById(int proposalId) {
        return this.proposalRepository.findById(proposalId);
    }

    /**
     * List all the proposals of an HOA whose deadline has passed.
     *
     * @param hoaId id of HOA.
     * @return List of proposals with past deadline.
     */
    public List<Proposal> getHistoryProposals(int hoaId) {
        return this.proposalRepository.findByHoaIdAndVotingDeadlineIsLessThanEqual(hoaId, Date.from(Instant.now()));
    }

    /**
     * List all the proposals of an HOA that are currently active.
     *
     * @param hoaId id of HOA.
     * @return List of proposals with future deadline.
     */
    public List<Proposal> getActiveProposals(int hoaId) {
        return this.proposalRepository.findByHoaIdAndVotingDeadlineIsGreaterThan(hoaId, Date.from(Instant.now()));
    }
}
