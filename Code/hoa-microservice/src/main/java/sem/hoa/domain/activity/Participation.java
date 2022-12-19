package sem.hoa.domain.activity;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "participation")
@IdClass(ParticipationKey.class)
public class Participation {
    @Id
    private int activityId;
    @Id
    private String username;

    /**
     * Constructor for participation. Participation of users in activity is stored in this entity.
     *
     * @param activityId unique activity id
     * @param username   unique user id
     */
    public Participation(int activityId, String username) {
        this.activityId = activityId;
        this.username = username;
    }

    /**
     * Getter for activity id.
     *
     * @return the unique activity id
     */
    public int getActivityId() {
        return activityId;
    }

    /**
     * Getter for user id.
     *
     * @return the unique user id
     */
    public String getUsername() {
        return username;
    }
}
