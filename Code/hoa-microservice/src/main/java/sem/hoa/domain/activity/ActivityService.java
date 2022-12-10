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
     * It tries to add an activity to the repository.
     *
     * @param hoaId hoa ID of the HOA the activity belongs to.
     * @param date Date of the activity.
     * @param desc Description of the Activity.
     * @throws Exception This exception is thrown if we try to add an Activity that already exists. (I'll have to check this again because we do not generate activity Id)
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
