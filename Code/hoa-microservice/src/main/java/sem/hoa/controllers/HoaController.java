package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.services.HoaService;
import sem.hoa.domain.services.MemberManagementService;
import sem.hoa.dtos.JoiningRequestModel;
import sem.hoa.dtos.UserHoaCreationDto;

import java.util.Date;
import java.util.Optional;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class HoaController {

    private final transient AuthManager authManager;
    private final transient MemberManagementService memberManagementService;
    private final transient HoaService hoaService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public HoaController(AuthManager authManager, MemberManagementService memberManagementService, HoaService hoaService) {
        this.authManager = authManager;
        this.memberManagementService = memberManagementService;
        this.hoaService = hoaService;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @GetMapping("/welcomeHOA")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getNetId() + "! \nWelcome to Hoa!");

    }

    /**
     * Create a new Hoa.
     *
     * @param request request
     */
    @PostMapping("/createHOA")
    public ResponseEntity<Hoa> createHoa(@RequestBody UserHoaCreationDto request) {
        try {
            //System.out.println("ok");
            Hoa newHoa = new Hoa(request.hoaName, request.country, request.city);
            //System.out.println("ok");
            hoaService.createNewHoa(newHoa);
            //System.out.println("ok");
            //memberManagementService.addMembership(new Membership(authManager.getNetId(), newHoa.getId(), true));
            //System.out.println("ok");

            return ResponseEntity.ok(newHoa);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * A user can join a HOA.
     *
     * @param request request
     */
    @PostMapping("/joining")
    public ResponseEntity<String> joiningHoa(@RequestBody JoiningRequestModel request) {
        try {
            if (!request.userName.equals(authManager.getNetId())) {
                throw new BadRequestException("Wrong username");
            }

            Optional<Hoa> hoa = hoaService.findHoaByName(request.hoaName);
            if (hoa.isEmpty()) {
                throw new BadRequestException("No such Hoa with this name: " + request.hoaName);
            }

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaId(request.userName, hoa.get().getId());
            if (membership.isPresent()) {
                throw new BadRequestException("User is already in this Hoa");
            }

            Membership newMemberShip = new Membership(request.userName, hoa.get().getId(), false, request.country, request.city, new Date().getTime(), -1L);
            if (!memberManagementService.addressCheck(hoa.get(), newMemberShip)) {
                throw new BadRequestException("Invalid address");
            }

            memberManagementService.addMembership(newMemberShip);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

}
