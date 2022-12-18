package sem.hoa.domain.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, ParticipationKey> {
    Optional<Participation> findParticipationByActivityIdAndUsername(int activityId, String username);

    boolean existsByActivityIdAndUsername(int activityId, String username);
}
