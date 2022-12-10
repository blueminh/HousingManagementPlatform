package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.domain.activity.Activity;
import sem.hoa.domain.activity.ActivityService;
import sem.hoa.models.ActivityCreationRequestModel;

import java.util.Date;

@RestController
public class ActivityController {

    private final transient ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * API call for adding new a new Activity.
     *
     * @param req It is the request model for taking all the required information about a new Activity.
     *
     * @return Returns 200 OK Response
     *
     * @throws Exception In case the addition of Activity fails, it throws a BAD REQUEST Exception
     */
    @PostMapping("/activity/add")
    public ResponseEntity addActivity(@RequestBody ActivityCreationRequestModel req) throws Exception {
        try {
            activityService.addActivity(req.getHoaId(), req.getDate(), req.getDesc());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

}
