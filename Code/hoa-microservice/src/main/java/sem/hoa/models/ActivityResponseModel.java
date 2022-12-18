package sem.hoa.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ActivityResponseModel {
    private int activityId;
    private int hoaId;

    private String name;
    private String desc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;

    /**
     * Constructor for the ActivityResponseModel.
     *
     * @param activityId unique id of the activity
     * @param hoaId unique id of the hoa this activity belongs to
     * @param desc the description
     * @param date date at which the activity occurs
     */
    public ActivityResponseModel(int activityId, int hoaId, String name, String desc, Date date) {
        this.activityId = activityId;
        this.hoaId = hoaId;
        this.name = name;
        this.desc = desc;
        this.date = date;
    }
}
