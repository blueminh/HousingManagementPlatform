package sem.hoa.models;

import lombok.Data;

import java.util.Date;

@Data
public class ActivityResponseModel {
    private int activityId;
    private int hoaId;
    private String desc;
    private Date date;

    /**
     * Constructor for the ActivityResponseModel.
     *
     * @param activityId unique id of the activity
     * @param hoaId unique id of the hoa this activity belongs to
     * @param desc the description
     * @param date date at which the activity occurs
     */
    public ActivityResponseModel(int activityId, int hoaId, String desc, Date date) {
        this.activityId = activityId;
        this.hoaId = hoaId;
        this.desc = desc;
        this.date = date;
    }
}
