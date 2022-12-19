package sem.hoa.dtos.ruleModels;

import lombok.Data;
import sem.hoa.domain.entities.Rule;

import java.util.ArrayList;
import java.util.List;


/**
 * Model that stores an id of an HOA and its list of rules
 */
@Data
public class HoaIDRulesListModel {

    private int hoaId;
    private List<String> rules;

    public void setRules(List<Rule> list) {
        rules = new ArrayList<>();
        for(Rule r : list) {
            rules.add(r.getDescription());
        }
    }

    public int getHoaId() {
        return hoaId;
    }
}
