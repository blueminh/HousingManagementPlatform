package sem.hoa.domain.services;

import org.springframework.stereotype.Service;
import sem.hoa.domain.entities.Rule;

import java.util.List;
import java.util.Optional;

@Service
public class RuleService {

    private final transient RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public List<Rule> getHoaRules(int hoaId) {
        return this.ruleRepository.getRulesByHoaId(hoaId);
    }

    public Rule saveRule(Rule rule) {
        return this.ruleRepository.save(rule);
    }

    public Optional<Rule> findRuleById(int ruleId) {
        return this.ruleRepository.findById(ruleId);
    }

    public void replaceRule(Rule toBeReplaced, String replacement) {
        toBeReplaced.setDescription(replacement);
    }

    public void removeRuleById(int ruleId) {
        this.ruleRepository.deleteById(ruleId);
    }
}
