package sem.hoa.domain.activity;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
public class ParticipationKey implements Serializable {
    private int activityId;
    private String username;

    /**
     * Constructor for ParticipationKey. This is used to represent a composite key
     *
     * @param activityId the unique activity id
     *
     * @param username the unique user id
     */
    public ParticipationKey(int activityId, String username) {
        this.activityId = activityId;
        this.username = username;
    }

    /**
     * Getter for user id.
     *
     * @return the unique user id
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for activity id.
     *
     * @return the unique activity id
     */
    public int getActivityId() {
        return activityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParticipationKey that = (ParticipationKey) o;
        return getActivityId() == that.getActivityId() && getUsername().equals(that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getActivityId(), getUsername());
    }
}
