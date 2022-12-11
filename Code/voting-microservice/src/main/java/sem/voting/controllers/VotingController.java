package sem.voting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sem.voting.authentication.AuthManager;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.models.ProposalCreationRequestModel;
import sem.voting.models.ProposalCreationResponseModel;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class VotingController {

    private final transient AuthManager authManager;
    private final transient ProposalHandlingService proposalHandlingService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     * @param proposalHandlingService Service to handle proposals
     */
    @Autowired
    public VotingController(AuthManager authManager, ProposalHandlingService proposalHandlingService) {
        this.authManager = authManager;
        this.proposalHandlingService = proposalHandlingService;
    }

    /**
     * Example endpoint.
     *
     * @return 200 and say hi to the currently logged-in user.
     */
    @PostMapping("/example")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getNetId());
    }

    /**
     * Endpoint to create a new proposal.
     *
     * @param request model of the request
     * @return 200 if creation is successful
     *      400 if request is not complete
     */
    @PostMapping("/propose")
    public ResponseEntity<ProposalCreationResponseModel> addProposal(
            @RequestBody ProposalCreationRequestModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        // ToDo
        return ResponseEntity.ok(null);
    }

    /**
     * Endpoint to start voting on a proposal.
     *
     * @return 200 if transition is possible
     *      400 otherwise
     */
    @PostMapping("/start")
    public ResponseEntity<String> beginVoting() {
        // ToDo
        return ResponseEntity.ok("");
    }

    @PostMapping("/vote")
    public ResponseEntity<String> castVote() {
        // ToDo
        return ResponseEntity.ok("");
    }

    @PostMapping("/vote/remove")
    public ResponseEntity<String> removeVote() {
        // ToDo
        return ResponseEntity.ok("");
    }

    @PostMapping("/results")
    public ResponseEntity<String> getResults() {
        // ToDo
        return ResponseEntity.ok("");
    }

    @PostMapping("/info")
    public ResponseEntity<String> listProposals() {
        // ToDo
        return ResponseEntity.ok("");
    }
}
