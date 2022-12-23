package sem.hoa.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.entities.Rule;
import sem.hoa.domain.services.HoaService;
import sem.hoa.domain.services.RuleService;
import sem.hoa.dtos.rulemodels.AddRuleRequestModel;
import sem.hoa.dtos.rulemodels.AddRuleResponseModel;
import sem.hoa.dtos.rulemodels.RulesRequestModel;
import sem.hoa.dtos.rulemodels.RulesResponseModel;
import sem.hoa.dtos.rulemodels.EditRuleModel;
import sem.hoa.dtos.rulemodels.DeleteRuleModel;

import java.util.List;
import java.util.Optional;

@RestController
public class RuleController {

    private final transient AuthManager authManager;
    private final transient RuleService ruleService;
    private final transient HoaService hoaService;

    /**
     * Constructor of the controller.
     *
     * @param authManager authentication manager
     * @param ruleService the service for the rules
     * @param hoaService the service for hoa
     */
    @Autowired
    public RuleController(AuthManager authManager, RuleService ruleService, HoaService hoaService) {
        this.authManager = authManager;
        this.ruleService = ruleService;
        this.hoaService = hoaService;
    }

    /**
     * Endpoint to display all the rules of an Hoa.
     *
     * @param request model of the request
     * @return 200 if the Hoa was found
     *      404 if the Hoa was not found
     *      400 otherwise
     *      The response contains a list of the rules
     */
    @GetMapping("/rules")
    public ResponseEntity<RulesResponseModel> displayRules(@RequestBody RulesRequestModel request) {
        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<Hoa> hoa = hoaService.findHoaById(request.getHoaId());
        if (hoa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Rule> rules = ruleService.getHoaRules(request.getHoaId());
        RulesResponseModel response = new RulesResponseModel();
        response.setHoaId(request.getHoaId());
        response.setRules(rules);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to add a rule to an Hoa.
     *
     * @param request model of the request
     * @return 200 if the rule was successfully added
     *      404 if the Hoa was not found
     *      400 otherwise
     *      The response contains the id of the Hoa and the list of rules
     */
    @PostMapping("/add-rule")
    public ResponseEntity<AddRuleResponseModel> addRule(@RequestBody AddRuleRequestModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Hoa> hoa = hoaService.findHoaById(request.getHoaId());
        if (hoa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Rule> rules = ruleService.getHoaRules(request.getHoaId());
        Rule newRule = new Rule(request.getHoaId(), request.getNewRule());
        ruleService.saveRule(newRule);
        rules.add(newRule);
        AddRuleResponseModel response = new AddRuleResponseModel();
        response.setHoaId(request.getHoaId());
        response.setRules(rules);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to change a rule in an Hoa.
     *
     * @param request model of the request
     *
     * @return 200 if the modification was successful
     *      401 if the rule was not found
     *      400 otherwise
     */
    @PostMapping("/edit-rule")
    public ResponseEntity editRule(@RequestBody EditRuleModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Rule> rule = ruleService.findRuleById(request.getRuleId());
        if (rule.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ruleService.replaceRule(rule.get(), request.getChange());
        ruleService.saveRule(rule.get());
        return ResponseEntity.ok("Edited rule with id: " + request.getRuleId()
                + "\nNew rule is: " + request.getChange());

    }

    /**
     * Endpoint to delete a rule.
     *
     * @param request model of the request
     *
     * @return 200 if the deletion was successful
     *      401 if the rule was not found
     *      400 otherwise
     *
     */
    @DeleteMapping("/delete-rule")
    public ResponseEntity deleteRule(@RequestBody DeleteRuleModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Rule> rule = ruleService.findRuleById(request.getRuleId());
        Optional<Hoa> hoa = hoaService.findHoaById(request.getHoaId());
        if (rule.isEmpty() || hoa.isEmpty()) {
            ruleService.removeRuleById(request.getRuleId());
        }
        return ResponseEntity.ok("Deleted rule with id: " + request.getRuleId()
                + "\nPart of hoa with id: " + request.getHoaId());

    }


}
