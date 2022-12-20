package sem.hoa.domain.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    Optional<Activity> findByActivityId(Integer activityId);

    List<Activity> findActivitiesByHoaId(Integer hoaId);

    List<Activity> findActivitiesByDateAfter(Date date);

    List<Activity> findActivitiesByDateBefore(Date date);

    boolean existsActivityByName(String name);

    boolean existsActivityByDateAfter(Date date);

    boolean existsActivityByDateBefore(Date date);

    boolean existsActivityByActivityId(Integer activityId);
}
