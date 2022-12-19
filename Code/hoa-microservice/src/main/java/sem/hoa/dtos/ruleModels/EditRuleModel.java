package sem.hoa.dtos.ruleModels;


import lombok.Data;


/**
 * Model that represents a request for modifying a rule
 */
@Data
public class EditRuleModel {

    private int ruleId;
    private String change;

    public int getRuleId() {
        return ruleId;
    }

    public String getChange() {
        return change;
    }
}
