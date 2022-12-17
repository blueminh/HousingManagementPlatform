package sem.hoa.domain.entities;

import javax.persistence.*;
import java.util.Objects;


@Entity
@IdClass(RuleID.class)
@Table(name = "rule")
public class Rule {
    @Id
    @Column(name = "hoaID", nullable = false)
    private final int hoaID;

    @Id
    @Column(name = "description", nullable = false)
    private String description;

    public Rule(int hoaID, String description) {
        this.hoaID = hoaID;
        this.description = description;
    }

    public int getHoaID() {
        return hoaID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return getHoaID() == rule.getHoaID() && Objects.equals(getDescription(), rule.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHoaID(), getDescription());
    }
}