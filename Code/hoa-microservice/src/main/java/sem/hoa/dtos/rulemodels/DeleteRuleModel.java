package sem.hoa.dtos.rulemodels;


import lombok.Data;

/**
 * Model that represents a request for deleting a rule from an HOA.
 */
@Data
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
