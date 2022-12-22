package sem.hoa.dtos.rulemodels;

import lombok.Data;
import sem.hoa.domain.entities.Rule;

import java.util.List;


/**
 * Model that represents a response to adding a new rule to an HOA.
 */
@Data
public class AddRuleResponseModel {

    private int hoaId;
    private List<Rule> rules;

    public int getHoaId() {
        return hoaId;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setHoaId(int hoaId) {
        this.hoaId = hoaId;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}
