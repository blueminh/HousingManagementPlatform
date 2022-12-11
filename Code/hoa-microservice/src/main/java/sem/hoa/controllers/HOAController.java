package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.entities.MembershipID;
import sem.hoa.domain.services.HOAService;
import sem.hoa.domain.services.MemberManagementService;
import sem.hoa.dtos.UserNameHoaIDDTO;
import sem.hoa.dtos.UserNameHoaNameDTO;

import java.util.Optional;

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
        return ResponseEntity.ok("Hello " + authManager.getNetId() + "! \nWelcome to HOA!") ;

    }
    //should add a check for the address of the hoa and the user
    // Membership
    @PostMapping("/joining")
    public ResponseEntity joiningHOA(@RequestBody UserNameHoaNameDTO request){
        try {
            if (!request.username.equals(authManager.getNetId()))
                throw new Exception("Wrong username");

            Optional<HOA> hoa = hoaService.findHOAByName(request.hoaName);
            if (hoa.isEmpty()) throw new Exception("No such HOA with this name: " + request.hoaName);

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(request.username, hoa.get().getId());
            if (membership.isPresent()) throw new Exception("User is already in this HOA");

            memberManagementService.addMembership(new Membership(request.username, hoa.get().getId(), false));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }


    /**
     * Find this user's role for a given hoa name
     * @param request contains hoa name and username
     * @return a boolean value indicate the role of this user (true if boardMember)
     */
    @GetMapping("/findUserRoleByHoaName")
    public ResponseEntity<String> findUserRoleByHoaName(@RequestBody UserNameHoaNameDTO request) {
        try {
            Optional<HOA> hoa = hoaService.findHOAByName(request.hoaName);
            if (hoa.isEmpty()) throw new Exception("No such HOA with this name: " + request.hoaName);

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(request.username, hoa.get().getId());
            if (membership.isEmpty()) throw new Exception("User is not registered in this HOA");

            if (membership.get().isBoardMember()) return ResponseEntity.ok("boardMember");
            return ResponseEntity.ok("normalMember");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Find this user's role for a given hoa ID
     * @param request contains hoa ID and username
     * @return a boolean value indicate the role of this user (true if boardMember)
     */
    @GetMapping("/findUserRoleByHoaID")
    public ResponseEntity<String> findUserRoleByHoaID(@RequestBody UserNameHoaIDDTO request) {
        try {
            Optional<HOA> hoa = hoaService.findHOAByID(request.hoaID);
            if (hoa.isEmpty()) throw new Exception("No such HOA with this ID: " + request.hoaID);

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(request.username, hoa.get().getId());
            if (membership.isEmpty()) throw new Exception("User is not registered in this HOA");

            if (membership.get().isBoardMember()) return ResponseEntity.ok("boardMember");
            return ResponseEntity.ok("normalMember");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Allows the user to leave an HOA
     * @param request contains hoa ID and username
     * @return a message that confirms that the user has left the HOA or throws an exception
     */
    @DeleteMapping("/leave")
    public ResponseEntity leaveHOA(@RequestBody UserNameHoaNameDTO request) {
        try {
            if(!request.username.equals(authManager.getNetId())) {
                throw new UsernameNotFoundException("User not found");
            }

            Optional<HOA> hoa = hoaService.findHOAByName(request.hoaName);
            if(hoa.isEmpty()) throw new Exception("No such HOA with this name: " + request.hoaName);

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(request.username, hoa.get().getId());
            if(membership.isEmpty()) throw new Exception("User not found");
            MembershipID toBeRemoved = new MembershipID(request.username, hoa.get().getId());
            memberManagementService.removeMembership(toBeRemoved);
            return ResponseEntity.ok("User has successfully left");
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
