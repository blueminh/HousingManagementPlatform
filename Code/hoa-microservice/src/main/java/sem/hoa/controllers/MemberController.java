package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.authentication.AuthManager;
import sem.hoa.communications.BadRequestException;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.services.HoaService;
import sem.hoa.domain.services.MemberManagementService;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final transient AuthManager authManager;
    private final transient MemberManagementService memberManagementService;
    private final transient HoaService hoaService;

    private static final transient String noSuchHoaIdError = "No HOA with the id: ";
    private static final transient String noSuchHoaNameError = "No HOA with the name: ";
    private static final transient String userNotRegisteredError = "User is not registered in this Hoa";
    private static final transient String userNotBoardMemberError = "User is not a board member of this Hoa";

    /**
     * Constructor of this controller.
     *
     * @param authManager             authManager
     * @param memberManagementService service
     * @param hoaService              service
     */
    @Autowired
    public MemberController(AuthManager authManager, MemberManagementService memberManagementService, HoaService hoaService) {
        this.authManager = authManager;
        this.memberManagementService = memberManagementService;
        this.hoaService = hoaService;
    }

    /**
     * Find this user's role for a given hoa name.
     * Username is extracted from the auth token.
     *
     * @param hoaName contains hoa name
     * @return a boolean value indicate the role of this user (true if boardMember)
     */
    @GetMapping("/findUserRoleByHoaName")
    public ResponseEntity<String> findUserRoleByHoaName(@RequestParam String hoaName) {
        try {
            Optional<Hoa> hoa = hoaService.findHoaByName(hoaName);
            if (hoa.isEmpty()) {
                throw new BadRequestException(noSuchHoaNameError + hoaName);
            }

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaId(authManager.getUsername(), hoa.get().getId());
            if (membership.isEmpty()) {
                throw new BadRequestException(userNotRegisteredError);
            }

            if (membership.get().isBoardMember()) {
                return ResponseEntity.ok("boardMember");
            }
            return ResponseEntity.ok("normalMember");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Find this user's role for a given hoa ID.
     * Username is extracted from the auth token.
     *
     * @param hoaId contains hoa ID
     * @return a boolean value indicate the role of this user (true if boardMember)
     */
    @GetMapping("/findUserRoleByHoaID")
    public ResponseEntity<String> findUserRoleByHoaId(@RequestParam Integer hoaId) {
        try {
            Optional<Hoa> hoa = hoaService.findHoaById(hoaId);
            if (hoa.isEmpty()) {
                throw new BadRequestException(noSuchHoaIdError + hoaId);
            }

            String username = authManager.getUsername();
            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaId(username, hoa.get().getId());
            if (membership.isEmpty()) {
                throw new BadRequestException(userNotRegisteredError);
            }

            if (membership.get().isBoardMember()) {
                return ResponseEntity.ok("boardMember");
            }
            return ResponseEntity.ok("normalMember");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Check if user is a board member of any HOAs.
     * If yes, return the ID of the Hoa.
     */
    @GetMapping("/isaBoardMemberOfAny")
    public ResponseEntity<Integer> isBoardMember() {
        try {
            String username = authManager.getUsername();
            int hoaId = memberManagementService.isBoardMemberOf(username);
            return ResponseEntity.ok(hoaId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Check whether a user is a member of a Hoa.
     * Username is extracted from the auth token.
     *
     * @param hoaId contains hoaID
     */
    @GetMapping("/isMemberOf")
    public ResponseEntity<String> isMemberOfHoa(@RequestParam Integer hoaId) {
        try {
            Optional<Hoa> hoa = hoaService.findHoaById(hoaId);
            if (hoa.isEmpty()) {
                throw new BadRequestException(noSuchHoaIdError + hoaId);
            }

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaId(authManager.getUsername(), hoaId);
            return ResponseEntity.ok(Boolean.toString(membership.isPresent()));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Return the joiningDate of a user.
     * Username is extracted from the auth token
     *
     * @param hoaId the hoaID
     */
    @GetMapping("/joiningDate")
    public ResponseEntity<String> getJoiningDate(@RequestParam Integer hoaId) {
        try {
            Optional<Hoa> hoa = hoaService.findHoaById(hoaId);
            if (hoa.isEmpty()) {
                throw new BadRequestException(noSuchHoaIdError + hoaId);
            }

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaId(authManager.getUsername(), hoaId);
            if (membership.isEmpty()) {
                throw new BadRequestException(userNotRegisteredError);
            }
            return ResponseEntity.ok(membership.get().getJoiningDate().toString());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Return the board joining date of a user given a hoaID.
     * Username is taken from auth token.
     *
     * @param hoaId the hoaID
     */
    @GetMapping("/joiningBoardDate")
    public ResponseEntity<String> getBoardJoiningDate(@RequestParam Integer hoaId) {
        try {
            Optional<Hoa> hoa = hoaService.findHoaById(hoaId);
            if (hoa.isEmpty()) {
                throw new BadRequestException(noSuchHoaIdError + hoaId);
            }

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaId(authManager.getUsername(), hoaId);
            if (membership.isEmpty()) {
                throw new BadRequestException(userNotRegisteredError);
            }
            if (!membership.get().isBoardMember()) {
                // Return the minimum Date to indicate that the user was never on the board
                Date neverDate = Date.from(Instant.ofEpochMilli(0));
                return ResponseEntity.ok(neverDate.toString());
            }
            return ResponseEntity.ok(membership.get().getJoiningBoardDate().toString());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Endpoint to count the current number of board members of an HOA.
     *
     * @param hoaId the id of the hoa
     * @return 200 and the number of board members, 400 otherwise
     */
    @GetMapping("/numberBoardMembers")
    public ResponseEntity<String> countBoardMembers(@RequestParam Integer hoaId) {
        try {
            Optional<Hoa> hoa = hoaService.findHoaById(hoaId);
            if (hoa.isEmpty()) {
                throw new BadRequestException(noSuchHoaIdError + hoaId);
            }

            int count = memberManagementService.findBoardMembersByHoaId(hoaId).size();
            return ResponseEntity.ok(Integer.toString(count));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Indicates if there are possible candidates for the board.
     *
     * @param hoaId HOA to investigate
     * @return 200 and "true" if at least one candidate exists, "false" otherwise
     *      400 id the HOA doesn't exist
     */
    @GetMapping("/hasEligibleMembers")
    public ResponseEntity<String> countEligibleMembers(@RequestParam Integer hoaId) {
        Optional<Hoa> hoa = hoaService.findHoaById(hoaId);
        if (hoa.isEmpty()) {
            return ResponseEntity.badRequest().body(noSuchHoaIdError + hoaId);
        }
        return ResponseEntity.ok(Boolean.toString(memberManagementService.hasPossibleBoardCandidates(hoaId)));
    }

}
