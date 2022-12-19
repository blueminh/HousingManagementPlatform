package sem.hoa.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.entities.Rule;
import sem.hoa.domain.services.HOAService;
import sem.hoa.domain.services.RuleService;
import sem.hoa.dtos.HoaIDDTO;
import sem.hoa.dtos.HoaIDRuleDescDTO;

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
    public ResponseEntity<HoaIDRuleDescDTO> displayRules(@RequestBody HoaIDDTO request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        int hoaId = request.getId();
        Optional<HOA> hoa = hoaService.findHOAByID(hoaId);
        if(hoa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Rule> rules = ruleService.getHoaRules(hoaId);
        if(rules == null) {
            return ResponseEntity.badRequest().build();
        }
        HoaIDRuleDescDTO response = new HoaIDRuleDescDTO();
        response.setRules(rules);
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/add-rule")
//    public ResponseEntity addRule(@RequestBody HoaIDRuleDescDTO request) {
//        if (request == null) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PostMapping("/edit-rule")
//    public ResponseEntity editRule(@RequestBody HoaIDRuleDescDTO request) {
//        if (request == null) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @DeleteMapping("/delete-rule")
//    public ResponseEntity deleteRule(@RequestBody HoaIDRuleDescDTO request) {
//        if (request == null) {
//            return ResponseEntity.badRequest().build();
//        }
//    }


}
