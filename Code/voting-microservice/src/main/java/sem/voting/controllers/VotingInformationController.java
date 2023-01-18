package sem.voting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sem.voting.authentication.AuthManager;
import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.ProposalStage;
import sem.voting.domain.proposal.Result;
import sem.voting.models.ProposalGenericRequestModel;
import sem.voting.models.ProposalHistoryResponseModel;
import sem.voting.models.ProposalInfoRequestModel;
import sem.voting.models.ProposalInformationResponseModel;
import sem.voting.models.ProposalResultsResponseModel;
import sem.voting.models.ProposalStartVotingResponseModel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class VotingInformationController {

    private final transient AuthManager authManager;
    private final transient ProposalHandlingService proposalHandlingService;

    @Autowired
    public VotingInformationController(AuthManager authManager, ProposalHandlingService proposalHandlingService) {
        this.authManager = authManager;
        this.proposalHandlingService = proposalHandlingService;
    }

    /**
     * Find all closed proposals for a given HOA.
     *
     * @param request model of the request
     * @return 200 if the request is valid,
     * 400 otherwise
     */
    @PostMapping("/history")
    public ResponseEntity<List<ProposalHistoryResponseModel>> listExpiredProposals(
            @RequestBody ProposalInfoRequestModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Proposal> history = proposalHandlingService.getHistoryProposals(request.getHoaId());
        return ResponseEntity.ok(history.stream().map(p -> {
            p.checkDeadline();
            return proposalHandlingService.save(p);
        }).map(ProposalHistoryResponseModel::new).collect(Collectors.toList()));
    }

    /**
     * Find all active proposals for a given HOA.
     *
     * @param request model of the request
     * @return 200 if the request is valid,
     * 400 otherwise
     */
    @PostMapping("/active")
    public ResponseEntity<List<ProposalInformationResponseModel>> listActiveProposals(
            @RequestBody ProposalInfoRequestModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Proposal> active = proposalHandlingService.getActiveProposals(request.getHoaId());
        return ResponseEntity.ok(active.stream().map(p -> {
            p.checkDeadline();
            return proposalHandlingService.save(p);
        }).map(ProposalInformationResponseModel::new).collect(Collectors.toList()));
    }

    /**
     * Endpoint to get results of a voting.
     *
     * @param request model of the request
     * @return 200 if the results were computed correctly,
     * 404 if the proposal was not found,
     * 400 otherwise
     */
    @PostMapping("/results")
    public ResponseEntity<ProposalResultsResponseModel> getResults(
            @RequestBody ProposalGenericRequestModel request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        Proposal proposal = proposalHandlingService.checkHoa(request.getProposalId(), request.getHoaId());
        if (proposal == null) {
            return ResponseEntity.notFound().build();
        }

        Set<Result> results = proposal.getResults();
        proposalHandlingService.save(proposal);
        if (results == null) {
            return ResponseEntity.badRequest().build();
        }
        ProposalResultsResponseModel response = new ProposalResultsResponseModel();
        response.setProposalId(proposal.getProposalId());
        response.setHoaId(proposal.getHoaId());
        response.setAllResults(results);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to start voting on a proposal.
     *
     * @return 200 if transition was possible,
     * 404 if the proposal was not found,
     * 409 if the transition was not possible,
     * 400 otherwise
     */
    @PostMapping("/start")
    public ResponseEntity<ProposalStartVotingResponseModel> beginVoting(@RequestBody ProposalGenericRequestModel request) {
        try {
            Proposal proposal = proposalHandlingService.checkHoa(request.getProposalId(), request.getHoaId());
            if (proposal == null) {
                return ResponseEntity.notFound().build();
            } else if (!HoaCommunication.checkUserIsBoardMember(authManager.getUsername(), request.getHoaId())) {
                System.out.println("User is not a board member.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            proposal.startVoting();
            proposal = proposalHandlingService.save(proposal);
            ProposalStartVotingResponseModel response =
                    new ProposalStartVotingResponseModel(proposal.getProposalId(), proposal.getHoaId(), proposal.getStatus());
            if (proposal.getStatus() != ProposalStage.Voting) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Cannot find if user " + authManager.getUsername() + " is a board member of HOA " + request.getHoaId());
            return ResponseEntity.badRequest().build();
        }
    }
}
