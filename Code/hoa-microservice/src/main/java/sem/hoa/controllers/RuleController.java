package sem.hoa.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.entities.Rule;
import sem.hoa.domain.services.HOAService;
import sem.hoa.domain.services.RuleService;
import sem.hoa.dtos.AddRuleRequestModel;
import sem.hoa.dtos.AddRuleResponseModel;
import sem.hoa.dtos.EditRuleRequestModel;
import sem.hoa.dtos.HoaIDRulesListModel;

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

    @GetMapping("/rules")
    public ResponseEntity<HoaIDRulesListModel> displayRules(@RequestBody HoaIDRulesListModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<HOA> hoa = hoaService.findHOAByID(request.getHoaId());
        if(hoa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Rule> rules = ruleService.getHoaRules(request.getHoaId());
        if(rules == null) {
            return ResponseEntity.badRequest().build();
        }
        HoaIDRulesListModel response = new HoaIDRulesListModel();
        response.setRules(rules);
        return ResponseEntity.ok(response);
    }

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
        rules.add(new Rule(request.getHoaId(), request.getNewRule()));
        AddRuleResponseModel response = new AddRuleResponseModel();
        response.setHoaId(request.getHoaId());
        response.setRules(rules);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/edit-rule")
    public ResponseEntity<EditRuleRequestModel> editRule(@RequestBody EditRuleRequestModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Rule> rule = ruleService.findRuleById(request.getRuleId());
        if(rule.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ruleService.replaceRule(rule.get(), request.getChange());
        return ResponseEntity.ok().build();
        
    }
//
//    @DeleteMapping("/delete-rule")
//    public ResponseEntity deleteRule(@RequestBody HoaIDRuleDescDTO request) {
//        if (request == null) {
//            return ResponseEntity.badRequest().build();
//        }
//    }


}
