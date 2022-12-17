package sem.hoa.domain.services;

import org.springframework.stereotype.Service;
import sem.hoa.domain.entities.Rule;
import sem.hoa.domain.entities.RuleID;

@Service
public class RuleService {

    private final transient RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public void deleteRule(Rule rule) {
        //TODO implement the logic
    }

    public void addRule(Rule rule) {
        //TODO do some checks here
        ruleRepository.save(rule);
    }

    public void changeRule(Rule rule) {
        //TODO implement the logic

    }
}
