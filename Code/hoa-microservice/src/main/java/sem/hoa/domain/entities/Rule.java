package sem.hoa.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import java.util.Objects;


@Entity
@Table(name = "rule")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {

    @Id
    @GeneratedValue
    @Column(name = "ruleId", nullable = false, unique = true)
    private int id;

    @Column(name = "hoaId", nullable = false)
    private int hoaId;

    @Column(name = "description", nullable = false)
    private String description;

    public Rule(int hoaId, String description) {
        this.hoaId = hoaId;
        this.description = description;
    }

    public int getRuleId() {
        return id;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rule rule = (Rule) o;
        return getHoaID() == rule.getHoaID() && Objects.equals(getDescription(), rule.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHoaID(), getDescription());
    }
}