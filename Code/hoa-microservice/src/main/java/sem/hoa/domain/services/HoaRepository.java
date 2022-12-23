package sem.hoa.domain.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sem.hoa.domain.entities.Hoa;

import java.util.Optional;

@Repository
public interface HoaRepository extends JpaRepository<Hoa, Integer> {
    /**
     * Find an Hoa by its ID.
     *
     * @param integer must not be {@literal null}.
     * @return the Hoa
     */
    @Override
    Optional<Hoa> findById(Integer integer);

    Optional<Hoa> findByHoaName(String hoaName);
}
