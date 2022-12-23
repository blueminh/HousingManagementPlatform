package sem.hoa.dtos.rulemodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Model that represents a request for adding a new rule.
 */
@Data
@JsonIgnoreProperties
public class AddRuleRequestModel {

    private int hoaId;
    private String description;

    public int getHoaId() {
        return hoaId;
    }

    public String getNewRule() {
        return description;
    }
}
