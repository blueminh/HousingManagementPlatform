package sem.voting.domain.proposal;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

/**
 * Repository of proposals.
 */
@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Integer> {
    List<Proposal> findByHoaId(@NonNull int hoaId);

    List<Proposal> findByHoaIdAndVotingDeadlineIsGreaterThan(@NonNull int hoaId, @NonNull Date votingDeadline);

    List<Proposal> findByHoaIdAndVotingDeadlineIsLessThanEqual(@NonNull int hoaId, @NonNull Date votingDeadline);

}
