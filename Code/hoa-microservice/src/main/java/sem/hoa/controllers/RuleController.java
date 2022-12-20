package sem.hoa.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.entities.Rule;
import sem.hoa.domain.services.HOAService;
import sem.hoa.domain.services.RuleService;
import sem.hoa.dtos.ruleModels.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RuleController {

    private final transient AuthManager authManager;
    private final transient RuleService ruleService;
    private final transient HOAService hoaService;

    @Autowired
    public RuleController(AuthManager authManager, RuleService ruleService, HOAService hoaService) {
        this.authManager = authManager;
        this.ruleService = ruleService;
        this.hoaService = hoaService;
    }

    /**
     * Endpoint to display all the rules of an HOA
     *
     * @param request model of the request
     * @return 200 if the HOA was found
     *      404 if the HOA was not found
     *      400 otherwise
     *      The response contains a list of the rules
     */
    @GetMapping("/rules")
    public ResponseEntity<HoaIDRulesListModel> displayRules(@RequestBody HoaIDRulesListModel request) {
        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<HOA> hoa = hoaService.findHOAByID(request.getHoaId());
        if(hoa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Rule> rules = ruleService.getHoaRules(request.getHoaId());
        HoaIDRulesListModel response = new HoaIDRulesListModel();
        response.setRules(rules);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to add a rule to an HOA
     *
     * @param request model of the request
     * @return 200 if the rule was successfully added
     *      404 if the HOA was not found
     *      400 otherwise
     *      The response contains the id of the HOA and the list of rules
     */
    @PostMapping("/add-rule")
    public ResponseEntity<AddRuleResponseModel> addRule(@RequestBody AddRuleRequestModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<HOA> hoa = hoaService.findHOAByID(request.getHoaId());
        if(hoa.isEmpty()) {
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
     * Endpoint to change a rule in an HOA
     * @param request model of the request
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
        if(rule.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ruleService.replaceRule(rule.get(), request.getChange());
        return ResponseEntity.ok("Edited rule with id:" + request.getRuleId() +
                "\nNew rule is: " + request.getChange());

    }

    /**
     * Endpoint to delete a rule
     * @param request model of the request
     * @return 200 if the deletion was successful
     *      401 if the rule was not found
     *      400 otherwise
     *      The response contains information about the deleted rule and the id of the HOA
     */
    @DeleteMapping("/delete-rule")
    public ResponseEntity deleteRule(@RequestBody DeleteRuleModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Rule> rule = ruleService.findRuleById(request.getRuleId());
        if(rule.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ruleService.removeRuleById(request.getRuleId());
        return ResponseEntity.ok("Deleted rule with id: " + request.getRuleId() +
                "\nPart of hoa with id: " + request.getHoaId());

    }


}
