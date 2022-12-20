package sem.voting.controllers;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import sem.voting.authentication.AuthManager;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.ProposalStage;
import sem.voting.domain.proposal.Result;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.implementations.AddOptionException;
import sem.voting.domain.services.implementations.BoardElectionOptionValidationService;
import sem.voting.domain.services.implementations.BoardElectionsVoteValidationService;
import sem.voting.domain.services.implementations.RuleChangesOptionValidationService;
import sem.voting.domain.services.implementations.RuleChangesVoteValidationService;
import sem.voting.domain.services.implementations.VotingException;
import sem.voting.models.AddOptionRequestModel;
import sem.voting.models.AddOptionResponseModel;
import sem.voting.models.CastVoteRequestModel;
import sem.voting.models.ProposalCreationRequestModel;
import sem.voting.models.ProposalCreationResponseModel;
import sem.voting.models.ProposalGenericRequestModel;
import sem.voting.models.ProposalHistoryResponseModel;
import sem.voting.models.ProposalInfoRequestModel;
import sem.voting.models.ProposalInformationResponseModel;
import sem.voting.models.ProposalResultsResponseModel;
import sem.voting.models.ProposalStartVotingResponseModel;

/**
 * Controller of the Voting Microservice.
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
        toAdd.setHoaId(request.getHoaId());
        if (request.getOptions() != null) {
            for (String s : request.getOptions()) {
                try {
                    toAdd.addOption(new Option(s));
                } catch (AddOptionException e) {
                    System.out.println(e.getMessage());
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        toAdd.setTitle(request.getTitle());
        toAdd.setMotion(request.getMotion());
        toAdd = proposalHandlingService.save(toAdd);
        ProposalCreationResponseModel response = new ProposalCreationResponseModel(toAdd.getProposalId());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to add an option to a proposal.
     *
     * @param request model of the request
     * @return 200 and the model of the response if everything went good
     * 404 if the proposal was not found
     * 409 if the option could not be added
     * 401 otherwise
     */
    @PostMapping("/add-option")
    public ResponseEntity<AddOptionResponseModel> addOption(
        @RequestBody AddOptionRequestModel request) {
        if (request == null || !proposalHandlingService.checkHoa(request.getProposalId(), request.getHoaId())) {
            return ResponseEntity.badRequest().build();
        }
        // ToDo: check if authentication and HOA are valid

        Optional<Proposal> proposal = proposalHandlingService.getProposalById(request.getProposalId());
        if (proposal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AddOptionResponseModel response = new AddOptionResponseModel();
        response.setProposalId(proposal.get().getProposalId());
        response.setHoaId(proposal.get().getHoaId());
        // ToDo: check validity of new option, either here or in Proposal
        try {
            proposal.get().addOption(new Option(request.getOption()));
        } catch (AddOptionException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        proposal = Optional.of(proposalHandlingService.save(proposal.get()));
        response.setOptions(proposal.get().getAvailableOptions().stream()
            .map(Option::toString).collect(Collectors.toList()));
        // TODO: if these lines do sth important please add them back
        //        if (!added) {
        //            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        //        }
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
    public ResponseEntity<ProposalStartVotingResponseModel> beginVoting(
        @RequestBody ProposalGenericRequestModel request) {
        if (request == null || !proposalHandlingService.checkHoa(request.getProposalId(), request.getHoaId())) {
            return ResponseEntity.badRequest().build();
        }
        // ToDo: check if authentication and HOA are valid

        Optional<Proposal> proposal = proposalHandlingService.getProposalById(request.getProposalId());
        if (proposal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        proposal.get().startVoting();
        proposal = Optional.of(proposalHandlingService.save(proposal.get()));
        ProposalStartVotingResponseModel response = new ProposalStartVotingResponseModel();
        response.setProposalId(proposal.get().getProposalId());
        response.setHoaId(proposal.get().getHoaId());
        response.setStatus(proposal.get().getStatus());
        if (proposal.get().getStatus() != ProposalStage.Voting) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to add a vote to a proposal.
     *
     * @param request model of the request
     * @return 200 if it was possible to cast the vote,
     * 404 if the proposal was not found,
     * 401 if the user is not authorized to vote,
     * 400 otherwise.
     * The response contains the information on the proposal being edited.
     */
    @PostMapping("/vote")
    public ResponseEntity<ProposalInformationResponseModel> castVote(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
        @RequestBody CastVoteRequestModel request) {
        if (request == null || !proposalHandlingService.checkHoa(request.getProposalId(), request.getHoaId())) {

            return ResponseEntity.badRequest().build();
        }
        // ToDo: check if authentication and HOA are valid

        Optional<Proposal> proposal = proposalHandlingService.getProposalById(request.getProposalId());
        if (proposal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Option beingVoted = request.getOption().equals("") ? null : new Option(request.getOption());
        Vote vote = new Vote(authManager.getUserId(), beingVoted, authToken.split(" ")[1]);


        try {
            if (!proposal.get().addVote(vote)) {
                // Proposal needs to be saved because even if Vote wasn't successful, the status might have changed.
                proposalHandlingService.save(proposal.get());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ProposalInformationResponseModel(proposal.get()));
            }
        } catch (VotingException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        proposalHandlingService.save(proposal.get());
        return ResponseEntity.ok(new ProposalInformationResponseModel(proposal.get()));
    }

    /**
     * Endpoint to remove a vote. Equivalent to /vote with an empty option.
     *
     * @param request model of the request.
     * @return same as /vote
     */
    @PostMapping("/remove-vote")
    public ResponseEntity<ProposalInformationResponseModel> removeVote(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
        @RequestBody CastVoteRequestModel request) {
        request.setOption("");
        return castVote(authToken, request);
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
        if (request == null || !proposalHandlingService.checkHoa(request.getProposalId(), request.getHoaId())) {
            return ResponseEntity.badRequest().build();
        }
        // ToDo: check if authentication and HOA are valid

        Optional<Proposal> proposal = proposalHandlingService.getProposalById(request.getProposalId());
        if (proposal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Set<Result> results = proposal.get().getResults();
        proposalHandlingService.save(proposal.get());
        if (results == null) {
            return ResponseEntity.badRequest().build();
        }
        ProposalResultsResponseModel response = new ProposalResultsResponseModel();
        response.setProposalId(proposal.get().getProposalId());
        response.setHoaId(proposal.get().getHoaId());
        response.setAllResults(results);
        return ResponseEntity.ok(response);
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
        // ToDo: check if authentication and HOA are valid

        List<Proposal> active = proposalHandlingService.getActiveProposals(request.getHoaId());
        return ResponseEntity.ok(active.stream().map(p -> {
            p.checkDeadline();
            return proposalHandlingService.save(p);
        }).map(ProposalInformationResponseModel::new).collect(Collectors.toList()));
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
        // ToDo: check if authentication and HOA are valid

        List<Proposal> history = proposalHandlingService.getHistoryProposals(request.getHoaId());
        return ResponseEntity.ok(history.stream().map(p -> {
            p.checkDeadline();
            return proposalHandlingService.save(p);
        }).map(ProposalHistoryResponseModel::new).collect(Collectors.toList()));
    }
}
