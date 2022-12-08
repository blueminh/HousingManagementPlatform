package sem.hoa.domain.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class ActivityService {

    private final transient ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }


    /**
     * It tries to add an activity to the repository
     *
     * @param activity Activity to add to the repo
     */
    public void addActivity(int hoaId, Date date, String desc) throws Exception {
        Activity activity = new Activity(hoaId, date, desc);
        if (!activityRepository.existsActivityByActivityId(activity.getActivityId())) {
            activityRepository.save(activity);
        } else {
            throw new ActivityAlreadyExistsException();
        }
    }
}
