package sem.hoa.domain.entities;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "rule")
public class Rule {

    @Id
    @GeneratedValue
    @Column(name = "ruleId", nullable = false, unique = true)
    private int id;

    @Column(name = "hoaId", nullable = false)
    private final int hoaId;

    @Column(name = "description", nullable = false)
    private String description;

    public Rule(int hoaId, String description) {
        this.hoaId = hoaId;
        this.description = description;
    }

    public int getHoaID() {
        return hoaId;
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