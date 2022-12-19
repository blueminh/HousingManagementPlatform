package sem.hoa.domain.activity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import java.util.Date;

@Entity
@Table(name = "activities")
@NoArgsConstructor
public class Activity {
    // TODO: Add ending date
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activityId", nullable = false)
    private int activityId;

    @Column(name = "hoaId", nullable = false)
    private Integer hoaId;

    @Column(name = "name")
    private String name;

    @Column(name = "date", nullable = false, unique = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;

    @Column(name = "description", nullable = true, unique = false)
    private String description;

    /**
     * Create a new Activity.
     *
     * @param activityId  unique id used for identifying an Activity
     * @param date        date on which the activity takes place
     * @param description description about the activity
     */
    public Activity(Integer activityId, Integer hoaId, String name, Date date, String description) {
        this.activityId = activityId;
        this.hoaId = hoaId;
        this.name = name;
        this.date = date;
        this.description = description;
    }

    /**
     * Create a new Activity but without activityId.
     *
     * @param date        date on which the activity takes place
     * @param description description about the activity
     */
    public Activity(Integer hoaId, String name, Date date, String description) {
        this.hoaId = hoaId;
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public int getActivityId() {
        return activityId;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Integer getHoaId() {
        return hoaId;
    }
}
