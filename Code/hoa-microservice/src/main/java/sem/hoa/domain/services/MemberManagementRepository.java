package sem.hoa.domain.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.entities.MembershipID;

import java.util.Optional;


@Repository
public interface MemberManagementRepository extends JpaRepository<Membership, MembershipID> {
  Optional<Membership> findMembershipByUsernameAndBoardMemberIsTrue(String username);
}
