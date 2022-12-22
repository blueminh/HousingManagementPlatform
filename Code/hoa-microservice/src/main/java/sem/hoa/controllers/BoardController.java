package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.services.HOAService;
import sem.hoa.domain.services.MemberManagementService;

/**
 * REST controller for the board.
 */
@RestController
@RequestMapping("/board")
public class BoardController {
    private final transient AuthManager authManager;
    private final transient MemberManagementService memberManagementService;
    private final transient HOAService hoaService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public BoardController(AuthManager authManager, MemberManagementService memberManagementService, HOAService hoaService) {
        this.authManager = authManager;
        this.memberManagementService = memberManagementService;
        this.hoaService = hoaService;
    }
}
