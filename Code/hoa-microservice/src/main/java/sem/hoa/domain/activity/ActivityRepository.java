package sem.hoa.domain.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    Optional<Activity> findByActivityId(Integer activityId);

    Optional<Activity> findActivityByName(String activityName);

    List<Activity> findActivitiesByHoaId(Integer hoaId);

    List<Activity> findActivitiesByDateAfterAndHoaId(Date date, int hoaId);

    List<Activity> findActivitiesByDateBeforeAndHoaId(Date date, int hoaId);

    boolean existsActivityByName(String name);

    boolean existsActivityByDateAfterAndHoaId(Date date, int hoaId);

    boolean existsActivityByDateBeforeAndHoaId(Date date, int hoaId);

    boolean existsActivityByActivityId(Integer activityId);
}
