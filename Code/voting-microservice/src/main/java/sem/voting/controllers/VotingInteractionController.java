package sem.voting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sem.voting.authentication.AuthManager;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.implementations.AddOptionException;
import sem.voting.domain.services.implementations.VotingException;
import sem.voting.models.AddOptionRequestModel;
import sem.voting.models.AddOptionResponseModel;
import sem.voting.models.CastVoteRequestModel;
import sem.voting.models.ProposalInformationResponseModel;

import java.util.stream.Collectors;

@RestController
public class VotingInteractionController {

    private final transient AuthManager authManager;
    private final transient ProposalHandlingService proposalHandlingService;

    @Autowired
    public VotingInteractionController(AuthManager authManager, ProposalHandlingService proposalHandlingService) {
        this.authManager = authManager;
        this.proposalHandlingService = proposalHandlingService;
    }


    /**
     * Endpoint to remove a vote. Equivalent to /vote with an empty option.
     *
     * @param request model of the request.
     * @return same as /vote
     */
    @PostMapping("/remove-vote")
    public ResponseEntity<ProposalInformationResponseModel> removeVote(
            @RequestBody CastVoteRequestModel request) {
        request.setOption("");
        return castVote(request);
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
            @RequestBody CastVoteRequestModel request) {

        try {
            Proposal proposal = proposalHandlingService.checkHoa(request.getProposalId(), request.getHoaId());
            if (proposal == null) {
                return ResponseEntity.notFound().build();
            }

            Option beingVoted = request.getOption().equals("") ? null : new Option(request.getOption());
            Vote vote = new Vote(authManager.getUsername(), beingVoted);
            if (!proposal.addVote(vote)) {
                // Proposal needs to be saved because even if Vote wasn't successful, the status might have changed.
                proposal = proposalHandlingService.save(proposal);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ProposalInformationResponseModel(proposal));
            }
            proposal = proposalHandlingService.save(proposal);
            return ResponseEntity.ok(new ProposalInformationResponseModel(proposal));
        } catch (VotingException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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

        try {
            Proposal proposal = proposalHandlingService.checkHoa(request.getProposalId(), request.getHoaId());
            if (proposal == null) {
                return ResponseEntity.notFound().build();
            }
            AddOptionResponseModel response = new AddOptionResponseModel();
            response.setProposalId(proposal.getProposalId());
            response.setHoaId(proposal.getHoaId());
            proposal.addOption(new Option(request.getOption()), authManager.getUsername());
            proposal = proposalHandlingService.save(proposal);
            response.setOptions(proposal.getAvailableOptions().stream()
                    .map(Option::toString).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (AddOptionException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
