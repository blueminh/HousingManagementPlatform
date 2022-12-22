package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sem.hoa.authentication.AuthManager;
import sem.hoa.communications.VotingCommunication;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.services.HoaService;
import sem.hoa.domain.services.MemberManagementService;
import sem.hoa.dtos.CastVoteRequestModel;
import sem.hoa.dtos.UserNameHoaNameDto;
import java.util.Optional;

/**
 * REST controller for the board.
 */
@RestController
@RequestMapping("/board")
public class BoardController {
    private final transient AuthManager authManager;
    private final transient MemberManagementService memberManagementService;
    private final transient HoaService hoaService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public BoardController(AuthManager authManager, MemberManagementService memberManagementService, HoaService hoaService) {
        this.authManager = authManager;
        this.memberManagementService = memberManagementService;
        this.hoaService = hoaService;
    }

    /*** Redirect this request to the Voting service.*/
    @PostMapping("/apply")
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public ResponseEntity<String> applyForBoard(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
        @RequestBody UserNameHoaNameDto request) {
        // Contact the voting system
        Optional<Hoa> hoa = hoaService.findHoaByName(request.getHoaName());
        if (hoa.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA not found");
        }
        int proposalId;
        try {
            proposalId = VotingCommunication.getCurrentElectionId(authManager.getUsername(), hoa.get().getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        if (proposalId == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No elections");
        }
        try {
            VotingCommunication.redirectApplyingRequest(authManager.getUsername(), hoa.get().getId(), proposalId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /*** Redirect this request to the Voting service.
     */
    @PostMapping("/vote")
    public ResponseEntity<String> castVote(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
        @RequestBody CastVoteRequestModel request
    ) {
        try {
            VotingCommunication.redirectVotingRequest(authManager.getUsername(), request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
