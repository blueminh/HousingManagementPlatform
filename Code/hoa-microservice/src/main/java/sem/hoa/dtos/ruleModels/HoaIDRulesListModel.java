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
    private List<Rule> rules;

    public void setRules(List<Rule> list) {
        this.rules = list;
    }

    public int getHoaId() {
        return hoaId;
    }
}
