package sem.hoa.domain.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sem.hoa.domain.entities.HOA;

import java.util.Optional;

@Repository
public interface HOARepository extends JpaRepository<HOA, Integer> {
  /**
   * Find an HOA by its ID
   * @param integer must not be {@literal null}.
   * @return the HOA
   */
  @Override
  Optional<HOA> findById(Integer integer);

  Optional<HOA> findByHoaName(String hoaName);
}
