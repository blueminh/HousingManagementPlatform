package sem.hoa.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.services.RuleService;

@RestController
public class RuleController {

    private final transient AuthManager authManager;
    private final transient RuleService ruleService;

    @Autowired
    public RuleController(AuthManager authManager, RuleService ruleService) {
        this.authManager = authManager;
        this.ruleService = ruleService;
    }
}
