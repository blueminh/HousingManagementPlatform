package sem.hoa.dtos.ruleModels;

import lombok.Data;

/**
 * Model that represents a request for adding a new rule
 */
@Data
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
