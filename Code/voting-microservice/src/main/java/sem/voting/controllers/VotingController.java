package sem.voting.controllers;

import java.sql.Date;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sem.voting.authentication.AuthManager;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.services.implementations.BoardElectionsVoteValidationService;
import sem.voting.domain.services.implementations.BoardElectionsVotingRightsService;
import sem.voting.domain.services.implementations.RuleChangesVoteValidationService;
import sem.voting.domain.services.implementations.RuleChangesVotingRightsService;
import sem.voting.models.AddOptionRequestModel;
import sem.voting.models.AddOptionResponseModel;
import sem.voting.models.CastVoteRequestModel;
import sem.voting.models.ProposalCreationRequestModel;
import sem.voting.models.ProposalCreationResponseModel;
import sem.voting.models.ProposalGenericRequestModel;
import sem.voting.models.ProposalInfoRequestModel;

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
     * @param authManager             Spring Security component used to authenticate and authorize the user
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
        // Check if Date is valid
        if (Date.from(Instant.now()).after(request.getDeadline())) {
            return ResponseEntity.badRequest().build();
        }
        Proposal toAdd = new Proposal();
        toAdd.setHoaId(request.getHoaId());
        for (String s : request.getOptions()) {
            toAdd.addOption(new Option(s));
        }
        toAdd.setTitle(request.getTitle());
        toAdd.setMotion(request.getMotion());
        switch (request.getType()) {
            case BoardElection: {
                toAdd.setVoteValidationService(new BoardElectionsVoteValidationService());
                toAdd.setVotingRightsService(new BoardElectionsVotingRightsService());
                break;
            }
            case HoaRuleChange: {
                toAdd.setVoteValidationService(new RuleChangesVoteValidationService());
                toAdd.setVotingRightsService(new RuleChangesVotingRightsService());
                break;
            }
            default: {
                return ResponseEntity.badRequest().build();
            }
        }
        toAdd = proposalHandlingService.save(toAdd);
        ProposalCreationResponseModel response = new ProposalCreationResponseModel(toAdd.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-option")
    public ResponseEntity<AddOptionResponseModel> addOption(
            @RequestBody AddOptionRequestModel request) {
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
    public ResponseEntity<String> beginVoting(
            @RequestBody ProposalGenericRequestModel request) {
        // ToDo
        return ResponseEntity.ok("");
    }

    @PostMapping("/vote")
    public ResponseEntity<String> castVote(
            @RequestBody CastVoteRequestModel request) {
        // ToDo
        return ResponseEntity.ok("");
    }

    @PostMapping("/remove-vote")
    public ResponseEntity<String> removeVote(
            @RequestBody CastVoteRequestModel request) {
        // ToDo
        return ResponseEntity.ok("");
    }

    @PostMapping("/results")
    public ResponseEntity<String> getResults(
            @RequestBody ProposalGenericRequestModel request) {
        // ToDo
        return ResponseEntity.ok("");
    }

    @PostMapping("/info")
    public ResponseEntity<String> listProposals(
            @RequestBody ProposalInfoRequestModel request) {
        // ToDo
        return ResponseEntity.ok("");
    }
}
