package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.activity.Activity;
import sem.hoa.domain.activity.ActivityService;
import sem.hoa.domain.utils.Clock;
import sem.hoa.models.ActivityCreationRequestModel;
import sem.hoa.models.ActivityResponseModel;
import sem.hoa.models.GetActivitiesWithHoaIdRequestModel;
import sem.hoa.models.GetActivityWithHoaIdAndDateRequestModel;
import sem.hoa.models.UserParticipateModel;

import java.util.Date;


@RestController
public class ActivityController {

    private final transient ActivityService activityService;
    private final transient  Clock clock;

    private final transient AuthManager authManager;

    /**
     * Controller for Activity.
     *
     * @param activityService activity service that contains all the business logic
     * @param clock global clock that is used to get current time
     * @param authManager authentication manager
     */
    @Autowired
    public ActivityController(ActivityService activityService, Clock clock, AuthManager authManager) {
        this.activityService = activityService;
        this.clock = clock;
        this.authManager = authManager;
    }

    /**
     * API call for adding new a new Activity.
     *
     * @param req It is the request model for taking all the required information about a new Activity.
     * @return Returns 200 OK Response
     * @throws Exception In case the addition of Activity fails, it throws a BAD REQUEST Exception
     */
    @PostMapping("/activity/add")
    public ResponseEntity<Integer> addActivity(@RequestBody ActivityCreationRequestModel req) throws Exception {
        try {
            int res = activityService.addActivity(req.getHoaId(), req.getName(), req.getDate(), req.getDesc(), req.getCreatedBy());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

    }

    /**
     * API call for getting details about an Activity.
     *
     * @param activityName Unique name of the activity to get
     * @return Returns 200 OK Response along with the ActivityResponseModel
     * @throws Exception In case the retrieval of Activity fails, it throws a BAD REQUEST and a NoSuchActivityException
     */
    @GetMapping("/activity/get")
    public ResponseEntity<ActivityResponseModel> getActivity(@RequestParam(name = "name") String activityName) throws Exception {
        try {
            Activity activity = activityService.getActivity(activityName, authManager.getUsername());
            ActivityResponseModel responseModel = new ActivityResponseModel(
                    activity.getActivityId(), activity.getHoaId(), activity.getName(), activity.getDescription(), activity.getDate(), activity.getCreatedBy()
            );
            return ResponseEntity.ok(responseModel);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * API call for removing an Activity.
     *
     * @param activityId Unique id of the activity to get
     * @return Returns 200 OK Response
     * @throws Exception In case the deletion of Activity fails, it throws a BAD REQUEST and a NoSuchActivityException
     */
    @DeleteMapping("/activity/remove")
    public ResponseEntity removeActivity(@RequestParam(name = "id") int activityId) throws Exception {
        try {
            activityService.removeActivity(activityId, authManager.getUsername());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * API call for user participating in an activity.
     *
     * @param userParticipateModel it is the request model for taking all the information regarding which user participates in which activity
     * @return returns 200 OK response if everything goes fine
     * @throws Exception In case the user already participates in the activity or the activity does not exist or the user does not exist
     */
    @PostMapping("/activity/participate")
    public ResponseEntity participate(@RequestBody UserParticipateModel userParticipateModel) throws Exception {
        try {
            activityService.participate(authManager.getUsername(), userParticipateModel.getActivityId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * API call for removing user participation in an activity.
     *
     * @param userParticipateModel it is the request model for taking all the information regarding which user participates in which activity
     * @return returns 200 OK response if everything goes fine
     * @throws Exception In case the user already does not participate in the activity or the activity does not exist or the user does not exist
     */
    @DeleteMapping("/activity/removeParticipate")
    public ResponseEntity removeParticipate(@RequestBody UserParticipateModel userParticipateModel) throws Exception {
        try {
            activityService.removeParticipate(authManager.getUsername(), userParticipateModel.getActivityId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * API endpoint to get all the activities that occurs after the provided date.
     *
     * @param requestModel Request model that has date and hoaId
     * @return an array of Activities as a response
     */
    @GetMapping("/activity/getAllAfterDate")
    public ResponseEntity<ActivityResponseModel[]> getAllActivitiesAfterDate(@RequestBody GetActivityWithHoaIdAndDateRequestModel requestModel) throws Exception {
        try {
            ActivityResponseModel[] response = activityService.getAllActivitiesAfterDate(requestModel.getDate(), requestModel.getHoaId(), authManager.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * API endpoint to get all the activities that occurred before the provided date.
     *
     * @param requestModel request model that has date and hoaId
     * @return an array of Activities as a response
     */
    @GetMapping("/activity/getAllBeforeDate")
    public ResponseEntity<ActivityResponseModel[]> getAllActivitiesBeforeDate(@RequestBody GetActivityWithHoaIdAndDateRequestModel requestModel) throws Exception {
        try {
            ActivityResponseModel[] response = activityService.getAllActivitiesBeforeDate(requestModel.getDate(), requestModel.getHoaId(), authManager.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * API endpoint to get all the future activities.
     *
     * @return an array of Activities as a response
     */
    @GetMapping("/activity/getAllFutureActivities")
    public ResponseEntity<ActivityResponseModel[]> getAllFutureActivities(@RequestBody GetActivitiesWithHoaIdRequestModel requestModel) throws Exception {
        Date currentDate = clock.getCurrentDate();
        try {
            ActivityResponseModel[] response = activityService.getAllActivitiesAfterDate(currentDate, requestModel.getHoaId(), authManager.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * API endpoint to get all the past activities.
     *
     * @return an array of Activities as a response
     */
    @GetMapping("/activity/getAllPastActivities")
    public ResponseEntity<ActivityResponseModel[]> getAllPastActivities(@RequestBody GetActivitiesWithHoaIdRequestModel requestModel) throws Exception {
        Date currentDate = clock.getCurrentDate();
        try {
            ActivityResponseModel[] response = activityService.getAllActivitiesBeforeDate(currentDate, requestModel.getHoaId(), authManager.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

}
