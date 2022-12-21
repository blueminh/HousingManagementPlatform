package sem.hoa.controllers;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.entities.MembershipID;
import sem.hoa.domain.services.HOAService;
import sem.hoa.domain.services.MemberManagementService;
import sem.hoa.dtos.HoaModifyDTO;
import sem.hoa.dtos.UserNameHoaIDDTO;
import sem.hoa.dtos.UserNameHoaNameDTO;
import sem.hoa.exceptions.HoaCreationException;
import sem.hoa.exceptions.HoaJoiningException;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class HOAController {

    private final transient AuthManager authManager;
    private final transient MemberManagementService memberManagementService;
    private final transient HOAService hoaService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public HOAController(AuthManager authManager, MemberManagementService memberManagementService, HOAService hoaService) {
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
        return ResponseEntity.ok("Hello " + authManager.getNetId() + "! \nWelcome to HOA!");

    }

    /**
     * Create an HOA.
     *
     * @param request model to create an HOA
     * @return 200 and the hoa created if joined successfully
     */
    @PostMapping("/createHOA")
    public ResponseEntity<HOA> createHOA(@RequestBody HoaModifyDTO request) {

        try {
            //CHECKS
            hoaService.checkHoaModifyDTO(request);
            if (hoaService.hoaExistsByName(request.hoaName)) {
                System.err.println("HOA with that name already exists");
                throw new HoaCreationException("HOA by that name already exists");
            }

            //Creation
            HOA newHOA = new HOA(request.hoaName, request.userCountry, request.userCity);
            hoaService.createNewHOA(newHOA);
            memberManagementService
                    .addMembership(new Membership(authManager.getNetId(), newHOA.getId(), true,
                    request.userCountry, request.userCity,
                            request.userStreet, request.userHouseNumber, request.userPostalCode));
            return ResponseEntity.ok(newHOA);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempted to create HOA, but " + e.getMessage());
        }
    }
    

    /**
     * Join an HOA.
     *
     * @param request model to join an HOA
     * @return 200 if joined successfully
     */
    // Membership
    @PostMapping("/joining")
    public ResponseEntity joiningHOA(@RequestBody HoaModifyDTO request) {
        try {
            //CHECKS
            hoaService.checkHoaModifyDTO(request);
            if (!hoaService.hoaExistsByName(request.hoaName)) {
                throw new HoaJoiningException("No such HOA with this name: " + request.hoaName);
            }
            HOA hoa = hoaService.findHOAByName(request.hoaName).get();
            if (memberManagementService
                    .findByUsernameAndHoaID(authManager.getNetId(), hoa.getId())
                    .isPresent()) {
                throw new HoaJoiningException("User is already in this HOA"); //need explanation
            }
            Membership membership = new Membership(authManager.getNetId(),
                    hoaService.findHOAByName(request.hoaName).get().getId(), false,
                    request.userCountry, request.userCity, request.userStreet,
                    request.userHouseNumber, request.userPostalCode);
            if (!memberManagementService.addressCheck(hoa, membership)) {
                throw new HoaJoiningException("Invalid address");
            }
            //CREATION
            //weird warning - should be resolved later (probably because of the isPresent() method)
            memberManagementService
                    .addMembership(new Membership(authManager.getNetId(), hoa.getId(), false,
                            request.userCountry, request.userCity,
                            request.userStreet, request.userHouseNumber, request.userPostalCode));
            System.out.println("Member " + authManager.getNetId() + " added successfully to " + request.getHoaName());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }


    /**
     * Find this user's role for a given hoa name.
     *
     * @param request contains hoa name and username
     * @return a boolean value indicate the role of this user (true if boardMember)
     */
    @GetMapping("/findUserRoleByHoaName")
    public ResponseEntity<String> findUserRoleByHoaName(@RequestBody UserNameHoaNameDTO request) {
        try {
            Optional<HOA> hoa = hoaService.findHOAByName(request.hoaName);
            if (hoa.isEmpty()) {
                throw new Exception("No such HOA with this name: " + request.hoaName);
            }

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(request.username, hoa.get().getId());
            if (membership.isEmpty()) {
                throw new Exception("User is not registered in this HOA");
            }

            if (membership.get().isBoardMember()) {
                return ResponseEntity.ok("boardMember");
            }
            return ResponseEntity.ok("normalMember");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Find this user's role for a given hoa ID.
     *
     * @param request contains hoa ID and username
     * @return a boolean value indicate the role of this user (true if boardMember)
     */
    @GetMapping("/findUserRoleByHoaID")
    public ResponseEntity<String> findUserRoleByHoaID(@RequestBody UserNameHoaIDDTO request) {
        try {
            Optional<HOA> hoa = hoaService.findHOAByID(request.hoaID);
            if (hoa.isEmpty()) {
                throw new Exception("No such HOA with this ID: " + request.hoaID);
            }

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(request.username, hoa.get().getId());
            if (membership.isEmpty()) {
                throw new Exception("User is not registered in this HOA");
            }

            if (membership.get().isBoardMember()) {
                return ResponseEntity.ok("boardMember");
            }
            return ResponseEntity.ok("normalMember");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Allows the user to leave an HOA.
     *
     * @param request contains hoa ID and username
     * @return a message that confirms that the user has left the HOA or throws an exception
     */
    @DeleteMapping("/leave")
    public ResponseEntity leaveHOA(@RequestBody UserNameHoaNameDTO request) {
        try {
            if (!request.username.equals(authManager.getNetId())) {
                throw new UsernameNotFoundException("User not found");
            }

            Optional<HOA> hoa = hoaService.findHOAByName(request.hoaName);
            if (hoa.isEmpty()) {
                throw new Exception("No such HOA with this name: " + request.hoaName);
            }

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(request.username, hoa.get().getId());
            if (membership.isEmpty()) {
                throw new Exception("User not found");
            }
            MembershipID toBeRemoved = new MembershipID(request.username, hoa.get().getId());
            memberManagementService.removeMembership(toBeRemoved);
            return ResponseEntity.ok("User has successfully left");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
