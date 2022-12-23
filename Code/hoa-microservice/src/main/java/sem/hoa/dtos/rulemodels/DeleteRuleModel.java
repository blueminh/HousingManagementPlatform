package sem.hoa.dtos.rulemodels;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Model that represents a request for deleting a rule from an HOA.
 */
@Data
@JsonIgnoreProperties
public class DeleteRuleModel {

    private int hoaId;
    private int ruleId;

    public int getRuleId() {
        return ruleId;
    }

    public int getHoaId() {
        return hoaId;
    }
}
