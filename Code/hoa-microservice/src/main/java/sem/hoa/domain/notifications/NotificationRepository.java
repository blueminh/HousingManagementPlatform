package sem.hoa.domain.notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    boolean existsByMessageAndUsername(String message, String username);

    boolean existsByUsername(String username);

    List<Notification> removeAllByUsername(String username);
}
