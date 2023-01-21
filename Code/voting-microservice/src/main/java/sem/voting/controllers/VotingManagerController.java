package sem.voting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sem.voting.authentication.AuthManager;
import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.ProposalType;
import sem.voting.domain.services.implementations.AddOptionException;
import sem.voting.domain.services.implementations.BoardElectionOptionValidationService;
import sem.voting.domain.services.implementations.BoardElectionsVoteValidationService;
import sem.voting.domain.services.implementations.RuleChangesOptionValidationService;
import sem.voting.domain.services.implementations.RuleChangesVoteValidationService;
import sem.voting.domain.services.validators.InvalidRequestException;
import sem.voting.domain.services.validators.MemberIsBoardMemberValidator;
import sem.voting.domain.services.validators.NoBoardElectionValidator;
import sem.voting.domain.services.validators.Validator;
import sem.voting.models.ProposalCreationRequestModel;
import sem.voting.models.ProposalCreationResponseModel;

import java.time.Instant;
import java.util.Date;

@RestController
public class VotingManagerController {

    private final transient AuthManager authManager;
    private final transient ProposalHandlingService proposalHandlingService;

    @Autowired
    public VotingManagerController(AuthManager authManager, ProposalHandlingService proposalHandlingService) {
        this.authManager = authManager;
        this.proposalHandlingService = proposalHandlingService;
    }

    /**
     * Endpoint to create a new proposal.
     *
     * @param request model of the request
     * @return 200 if creation is successful
     * 400 if request is not complete
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @PostMapping("/propose")
    public ResponseEntity<ProposalCreationResponseModel> addProposal(
            @RequestBody ProposalCreationRequestModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        // Check if Date is valid
        Date deadline = request.getDeadline();
        if (Date.from(Instant.now()).after(deadline)) {
            return ResponseEntity.badRequest().build();
        }

        // Build proposal
        Proposal toAdd = new Proposal();
        toAdd.setHoaId(request.getHoaId());
        switch (request.getType()) {
            case BoardElection: {
                toAdd.setVoteValidationService(new BoardElectionsVoteValidationService());
                toAdd.setOptionValidationService(new BoardElectionOptionValidationService());
                break;
            }
            case HoaRuleChange: {
                toAdd.setVoteValidationService(new RuleChangesVoteValidationService());
                toAdd.setOptionValidationService(new RuleChangesOptionValidationService());
                break;
            }
            default: {
                return ResponseEntity.badRequest().build();
            }
        }
        toAdd.setVotingDeadline(deadline);
        if (request.getOptions() != null) {
            for (String s : request.getOptions()) {
                try {
                    toAdd.addOption(new Option(s), authManager.getUsername());
                } catch (AddOptionException e) {
                    System.out.println(e.getMessage());
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        toAdd.setTitle(request.getTitle());
        toAdd.setMotion(request.getMotion());

        // Validate Proposal
        Validator validator = new NoBoardElectionValidator(proposalHandlingService);
        try {
            // board elections can be started by any user if there is no current board member
            if (HoaCommunication.checkHoaHasBoard(authManager.getUsername(), request.getHoaId())
                    || request.getType() != ProposalType.BoardElection) {
                validator.addLast(new MemberIsBoardMemberValidator());
            } else {
                System.out.println("HOA doesn't have a board.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.err.println("Could not determine the number of HOA board members.");
            return ResponseEntity.badRequest().build();
        }
        try {
            validator.handle(authManager.getUsername(), null, toAdd);
        } catch (InvalidRequestException ex) {
            System.err.println(authManager.getUsername() + " does not have the rights to start a new proposal:");
            System.err.println(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }

        toAdd = proposalHandlingService.save(toAdd);
        ProposalCreationResponseModel response = new ProposalCreationResponseModel(toAdd.getProposalId());
        return ResponseEntity.ok(response);
    }
}
