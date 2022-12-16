package sem.hoa.domain.activity;

import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import java.util.Date;
import java.util.Optional;

@Service
public class ActivityService {

    private final transient ActivityRepository activityRepository;
    private final transient ParticipationRepository participationRepository;

    public ActivityService(ActivityRepository activityRepository, ParticipationRepository participationRepository) {
        this.activityRepository = activityRepository;
        this.participationRepository = participationRepository;
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


    /**
     * Gets the specified activity from the database.
     *
     * @param activityId activity id of the activity to be fetched
     *
     * @return the activity retrieved from the database
     * @throws Exception throws an exception if no such activity was found
     */
    public Activity getActivity(int activityId) throws Exception {
        Optional<Activity> activity = activityRepository.findByActivityId(activityId);
        if (activity.isPresent()) {
            return activity.get();
        } else {
            throw new NoSuchActivityException("No such activity");
        }
    }

    /**
     * This service adds the userId and activityId to the Participation repository.
     *
     * @param username the unique username
     * @param activityId the unique activity id
     */
    public void participate(String username, int activityId) throws Exception {
        if (participationRepository.existsByActivityIdAndUsername(activityId, username)) {
            System.out.println("Participation not added because user already participates in that activity.");
            throw new UserAlreadyParticipatesException("User already participates in the given activity");
        } else if (!activityRepository.existsActivityByActivityId(activityId)) {
            System.out.println("Participation not added because there is no such activity");
            throw new NoSuchActivityException("There is no activity with the id " + activityId);
        } else {
            participationRepository.save(new Participation(activityId, username));
            System.out.println("User with the id " + username + " now participates in activity with the id " + activityId);
        }
        //TODO: I should add another test to check if the user exists but for that I need to communicates with the user microservice
        //TODO: I should also add another check to see if the activity and the user belongs to the same HOA
    }
}
