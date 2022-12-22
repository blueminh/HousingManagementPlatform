package sem.hoa.domain.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.entities.MembershipId;

import java.util.List;
import java.util.Optional;


@Repository
public interface MemberManagementRepository extends JpaRepository<Membership, MembershipId> {
    List<Membership> findByUsernameAndIsBoardMemberIsTrue(String username);

    List<Membership> findByHoaIdAndIsBoardMemberIsTrue(int hoaId);

    boolean existsByHoaIdAndJoiningDateLessThanEqual(int hoaId, Long joiningDate);

}
