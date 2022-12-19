package sem.hoa.dtos;

import sem.hoa.domain.entities.Rule;

import java.util.List;

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
