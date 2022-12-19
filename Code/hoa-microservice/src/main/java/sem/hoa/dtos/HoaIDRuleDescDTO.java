package sem.hoa.dtos;

import lombok.Data;
import sem.hoa.domain.entities.Rule;

import java.util.ArrayList;
import java.util.List;

@Data
public class HoaIDRuleDescDTO {

    private List<String> rules;

    public void setRules(List<Rule> list) {
        rules = new ArrayList<>();
        for(Rule r : list) {
            rules.add(r.getDescription());
        }
    }
}
