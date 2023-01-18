package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.authentication.AuthManager;
import sem.hoa.communications.BadRequestException;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.services.HoaService;
import sem.hoa.domain.services.MemberManagementRepository;
import sem.hoa.domain.services.MemberManagementService;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/member")
public class MemberAttributeController {

    private final transient AuthManager authManager;
    private final transient MemberManagementService memberManagementService;

    private final transient MemberManagementRepository memberManagementRepository;

    private final transient HoaService hoaService;

    private static final transient String noSuchHoaIdError = "No HOA with the id: ";

    /**
     * Constructor.
     *
     * @param authManager authManager
     * @param memberManagementService memberManagementService
     * @param memberManagementRepository memberManagementRepository
     * @param hoaService hoaService
     */
    @Autowired
    public MemberAttributeController(AuthManager authManager, MemberManagementService memberManagementService, MemberManagementRepository memberManagementRepository, HoaService hoaService) {
        this.authManager = authManager;
        this.memberManagementService = memberManagementService;
        this.memberManagementRepository = memberManagementRepository;
        this.hoaService = hoaService;
    }


    /**
     * Updating a user's role for a given HOA.
     *
     * @param toBeUpdated username to be updated
     * @param hoaId hoaId
     * @param isBoardMember the new role
     */
    @PostMapping("/updateRole")
    public ResponseEntity<String> updateBoardMember(
            @RequestParam(name = "toBeUpdated") String toBeUpdated,
            @RequestParam(name = "hoaId") int hoaId,
            @RequestParam(name = "isBoardMember") Boolean isBoardMember
    ) {
        try {
            String username = authManager.getUsername();
            Optional<Membership> membership1 = memberManagementService.findByUsernameAndHoaId(username, hoaId);

            if (membership1.isEmpty() || !membership1.get().isBoardMember()) {
                throw new BadRequestException("This user does not have the rights to update role of another user");
            }

            Optional<Membership> membership2 = memberManagementService.findByUsernameAndHoaId(toBeUpdated, hoaId);
            if (membership2.isEmpty()) {
                throw new BadRequestException("User to be updated is not in this HOA");
            }

            if (isBoardMember) {
                membership2.get().setJoiningBoardDate(new Date().getTime());
            } else {
                membership2.get().setJoiningBoardDate(-1L);
            }
            membership2.get().setBoardMember(isBoardMember);

            memberManagementRepository.save(membership2.get());
            return ResponseEntity.ok().body("User's role successfully updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
}
