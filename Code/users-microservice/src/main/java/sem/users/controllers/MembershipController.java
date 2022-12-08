package sem.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.users.authentication.AuthManager;
import sem.users.domain.user.HoaMembership;
import sem.users.domain.user.MembershipService;
import sem.users.domain.user.Username;
import sem.users.models.MembershipRequestModel;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class MembershipController {

    private final transient AuthManager authManager;
    private final transient MembershipService membershipService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager       Spring Security component used to authenticate and authorize the user
     * @param membershipService Service to manage memberships in the database
     */
    @Autowired
    public MembershipController(AuthManager authManager, MembershipService membershipService) {
        this.authManager = authManager;
        this.membershipService = membershipService;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getUsername());
    }


    /**
     * Mapping for post request to update an existing membership.
     *
     * @param request membership to update
     * @return whether it was successful
     */
    @PostMapping("/updatemembership")
    public ResponseEntity updateMembership(@RequestBody MembershipRequestModel request)throws Exception {
        try {
            Username username = new Username(request.getUsername());
            HoaMembership hoaMembership = new HoaMembership(request.getHoaId(), request.getRoleType());
            membershipService.updateMembership(username, hoaMembership);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Mapping for post request to create a new membership for a user.
     *
     * @param request membership to add to a user
     * @return whether it was successful
     */
    @PostMapping("/newmembership")
    public ResponseEntity addMembership(@RequestBody MembershipRequestModel request) {
        try {
            Username username = new Username(request.getUsername());
            HoaMembership hoaMembership = new HoaMembership(request.getHoaId(), request.getRoleType());
            membershipService.addMembership(username, hoaMembership);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();

    }


}
