package sem.hoa.domain.entities;

import java.io.Serializable;
import java.util.Objects;

public class RuleID implements Serializable {
    private int hoaID;
    private String description;

    public RuleID(int hoaID, String description) {
        this.hoaID = hoaID;
        this.description = description;
    }

    public int getHoaID() {
        return hoaID;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleID ruleID = (RuleID) o;
        return getHoaID() == ruleID.getHoaID() && Objects.equals(getDescription(), ruleID.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHoaID(), getDescription());
    }
}
