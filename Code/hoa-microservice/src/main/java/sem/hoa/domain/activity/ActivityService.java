package sem.hoa.domain.activity;

import org.springframework.stereotype.Service;
import sem.hoa.domain.services.HoaRepository;
import sem.hoa.domain.services.MemberManagementRepository;
import sem.hoa.domain.utils.Clock;
import sem.hoa.models.ActivityResponseModel;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

@Service
public class ActivityService {

    private final transient ActivityRepository activityRepository;
    private final transient ParticipationRepository participationRepository;
    private final transient MemberManagementRepository memberManagementRepository;
    private final transient HoaRepository hoaRepository;
    private final transient Clock clock;

    /**
     * Constructor for ActivityService.
     *
     * @param activityRepository         repositories that stores activities
     * @param participationRepository    participation repository
     * @param memberManagementRepository membership repository
     * @param hoaRepository              hoa repository
     * @param clock                      clock
     */
    public ActivityService(
            ActivityRepository activityRepository, ParticipationRepository participationRepository, MemberManagementRepository memberManagementRepository, HoaRepository hoaRepository, Clock clock
    ) {
        this.activityRepository = activityRepository;
        this.participationRepository = participationRepository;
        this.memberManagementRepository = memberManagementRepository;
        this.hoaRepository = hoaRepository;
        this.clock = clock;
    }

    /**
     * It tries to add an activity to the repository.
     *
     * @param hoaId hoa ID of the HOA the activity belongs to.
     * @param date  Date of the activity.
     * @param desc  Description of the Activity.
     * @throws Exception This exception is thrown if we try to add an Activity that already exists. (I'll have to check this again because we do not generate activity Id)
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public Integer addActivity(int hoaId, String name, Date date, String desc, String createdBy) throws Exception {

        checkParamValidity(name, date, desc);

        Activity activity = new Activity(hoaId, name, date, desc, createdBy);

        if (!hoaRepository.existsById(hoaId)) {
            throw new NoSuchHOAException("No HOAs with the id " + hoaId + " exists!");
        } else if (!memberManagementRepository.existsMembershipByHoaIdAndUsername(hoaId, createdBy)) {
            throw new NoAccessToHoaException(createdBy + " is not a member of the HOA " + hoaId);
        } else if (!activityRepository.existsActivityByNameAndHoaId(activity.getName(), activity.getHoaId())) {
            activityRepository.save(activity);
            return activity.getActivityId();
        } else {
            throw new ActivityAlreadyExistsException("Activity with that name already exists;"
                    + "Change the name if it is the same activity but a new version of it;"
                    + "For example, \"activity v1\" and \"activity v2\"");
        }
    }

    /**
     * Private utility method that checks if the name, the date and the description of the activity is valid.
     * It ensures that the length is between 1 and 30, and it only includes characters from a-z, A-Z and 0-9.
     * Special characters like - and _ are only allowed.
     *
     * @param name name to be checked for validity
     * @param date date to be checked for validity
     * @param desc description to be checked for validity
     */
    private void checkParamValidity(String name, Date date, String desc) {
        String regex = "[ a-zA-Z0-9_-]{1,30}";
        if (!name.matches(regex)) {
            throw new InvalidParameterException("Activity name (" + name + ") is invalid");
        }
        if (desc.length() == 0 || desc.length() > 100) {
            throw new InvalidParameterException("Description too long or blank");
        }
        if (date.before(clock.getCurrentDate())) {
            throw new InvalidParameterException("Activity date before current time not possible");
        }
    }

    /**
     * It tries to remove an activity to the repository.
     *
     * @param activityId activity ID of the activity to be removed
     * @throws Exception This exception is thrown if we try to remove an Activity that does not exist. (I'll have to check this again because we do not generate activity Id)
     */
    public void removeActivity(int activityId, String requestBy) throws Exception {

        if (activityRepository.findByActivityId(activityId).isEmpty()) {
            throw new NoSuchActivityException("No activity found to be removed");
        } else {
            Activity activity = activityRepository.findByActivityId(activityId).get();
            int hoaId = activity.getHoaId();
            if (!memberManagementRepository.existsMembershipByHoaIdAndUsername(hoaId, requestBy)) {
                throw new NoAccessToHoaException(requestBy + " is not a member of the HOA " + hoaId);
            }
            if (!activity.getCreatedBy().equals(requestBy)) {
                throw new NoAccessToHoaException("Only the creator of the activity can remove it");
            }
            // TODO: I could add another check that lets all the board members to remove the activity as well
            System.out.println("Activity deleted");
            activityRepository.deleteById(activityId);
        }
    }


    /**
     * Gets the specified activity from the database.
     *
     * @param activityName activity name of the activity to be fetched
     * @return the activity retrieved from the database
     * @throws Exception throws an exception if no such activity was found
     */
    public Activity getActivity(String activityName, String requestBy) throws Exception {

        if (activityRepository.findActivityByName(activityName).isPresent()) {
            Activity activity = activityRepository.findActivityByName(activityName).get();
            int hoaId = activity.getHoaId();
            if (!memberManagementRepository.existsMembershipByHoaIdAndUsername(hoaId, requestBy)) {
                throw new NoAccessToHoaException(requestBy + " is not a member of the HOA " + hoaId);
            }
            return activity;
        } else {
            System.out.println("No such activity to retrieve");
            throw new NoSuchActivityException("No such activity");
        }
    }

    /**
     * This service adds the userId and activityId to the Participation repository.
     *
     * @param username   the unique username
     * @param activityId the unique activity id
     */
    public void participate(String username, int activityId) throws Exception {
        if (participationRepository.existsByActivityIdAndUsername(activityId, username)) {
            System.out.println("Participation not added because user already participates in that activity.");
            throw new UserAlreadyParticipatesException("User already participates in the given activity");
        } else if (activityRepository.findByActivityId(activityId).isEmpty()) {
            System.out.println("Participation not added because there is no such activity");
            throw new NoSuchActivityException("There is no activity with the id " + activityId);
        } else {
            Activity activity = activityRepository.findByActivityId(activityId).get();
            int hoaId = activity.getHoaId();
            if (!memberManagementRepository.existsMembershipByHoaIdAndUsername(hoaId, username)) {
                throw new NoAccessToHoaException(username + " cannot participate as they are not a member of the HOA that the activity is part of");
            }
            participationRepository.save(new Participation(activityId, username));
            System.out.println("User with the id " + username + " now participates in activity with the id " + activityId);
        }
        //TODO: I should add another test to check if the user exists but for that I need to communicates with the user microservice
        //TODO: I should also add another check to see if the activity and the user belongs to the same HOA
    }

    /**
     * This service removes the userId and activityId to the Participation repository.
     *
     * @param username   the unique username
     * @param activityId the unique activity id
     */
    public void removeParticipate(String username, int activityId) throws Exception {
        if (!activityRepository.existsActivityByActivityId(activityId)) {
            System.out.println("Participation not added because there is no such activity");
            throw new NoSuchActivityException("There is no such activity with the id " + activityId);
        } else if (!participationRepository.existsByActivityIdAndUsername(activityId, username)) {
            System.out.println("No such participation exists in the database");
            throw new NoSuchParticipationException("There is no such participation with the id");
        } else {
            participationRepository.deleteById(new ParticipationKey(activityId, username));
            System.out.println("User with the id " + username + " now DOES NOT participate in activity with the id " + activityId);
        }
        //TODO: I should add another test to check if the user exists but for that I need to communicates with the user microservice
        //TODO: I should also add another check to see if the activity and the user belongs to the same HOA
    }

    /**
     * The service to get all the activities that occurs after the provided date.
     *
     * @param date all the activities retrieved will occur after this date
     * @return an array of Activities as a response
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public ActivityResponseModel[] getAllActivitiesAfterDate(Date date, int hoaId, String username) throws Exception {
        List<Activity> activities = activityRepository.findActivitiesByDateAfterAndHoaId(date, hoaId);
        if (activities.isEmpty()) {
            throw new NoSuchActivityException("There are no activities after the mentioned date");
        }
        if (!memberManagementRepository.existsMembershipByHoaIdAndUsername(hoaId, username)) {
            throw new NoAccessToHoaException(username + " cannot participate as they are not a member of the HOA that the activity is part of");
        }
        ActivityResponseModel[] res = new ActivityResponseModel[activities.size()];
        int idx = 0;
        for (Activity activity : activities) {
            res[idx++] = new ActivityResponseModel(activity.getActivityId(), activity.getHoaId(), activity.getName(), activity.getDescription(), activity.getDate(), activity.getCreatedBy());
        }
        return res;
    }

    /**
     * The service to get all the activities that occurred before the provided date. This can be used to get history of past activities
     *
     * @param date all the activities retrieved will occur after this date
     * @return an array of Activities as a response
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public ActivityResponseModel[] getAllActivitiesBeforeDate(Date date, int hoaId, String username) throws Exception {
        List<Activity> activities = activityRepository.findActivitiesByDateBeforeAndHoaId(date, hoaId);
        if (activities.isEmpty()) {
            throw new NoSuchActivityException("There are no activities before the mentioned date");
        }
        if (!memberManagementRepository.existsMembershipByHoaIdAndUsername(hoaId, username)) {
            throw new NoAccessToHoaException(username + " cannot participate as they are not a member of the HOA that the activity is part of");
        }
        ActivityResponseModel[] res = new ActivityResponseModel[activities.size()];
        int idx = 0;
        for (Activity activity : activities) {
            res[idx++] = new ActivityResponseModel(activity.getActivityId(), activity.getHoaId(), activity.getName(), activity.getDescription(), activity.getDate(), activity.getCreatedBy());
        }
        return res;
    }
}
