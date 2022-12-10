package sem.voting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sem.voting.authentication.AuthManager;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class VotingController {

    private final transient AuthManager authManager;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public VotingController(AuthManager authManager) {
        this.authManager = authManager;
    }

    /**
     * Adds a proposal to vote on.
     *
     * @return 200 if the proposal has been added successfully.
     */
    @PostMapping("/propose")
    public ResponseEntity<String> addProposal() {
        return ResponseEntity.ok("Hello " + authManager.getNetId());
    }

    // ToDo: add APIs
    /*
    - send results (i.e. all expired proposals of an HOA)
    - cast a vote
    - remove a vote
    - add a new proposal
    - start voting on a new proposal
    - get all proposals of an HOA
     */

}
