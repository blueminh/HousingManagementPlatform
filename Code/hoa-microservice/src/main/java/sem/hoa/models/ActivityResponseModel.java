package sem.hoa.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ActivityResponseModel {
    private int activityId;
    private int hoaId;

    private String name;
    private String desc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;
    private String createdBy;

    /**
     * Constructor for the ActivityResponseModel.
     *
     * @param activityId unique id of the activity
     * @param hoaId unique id of the hoa this activity belongs to
     * @param desc the description
     * @param date date at which the activity occurs
     * @param createdBy username of the user that created the activity
     */
    public ActivityResponseModel(int activityId, int hoaId, String name, String desc, Date date, String createdBy) {
        this.activityId = activityId;
        this.hoaId = hoaId;
        this.name = name;
        this.desc = desc;
        this.date = date;
        this.createdBy = createdBy;
    }
}
