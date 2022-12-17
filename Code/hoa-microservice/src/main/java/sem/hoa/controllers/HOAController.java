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
import sem.hoa.dtos.UserHoaCreationDDTO;
import sem.hoa.dtos.UserNameHoaIDDTO;
import sem.hoa.dtos.UserNameHoaNameDTO;

import java.util.Date;
import java.sql.Struct;
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

    @PostMapping("/createHOA")
    public ResponseEntity<HOA> createHOA(@RequestBody UserHoaCreationDDTO request){
        try{
            //System.out.println("ok");
            HOA newHOA = new HOA(request.hoaName, request.country, request.city);
            //System.out.println("ok");
            hoaService.createNewHOA(newHOA);
            //System.out.println("ok");
            memberManagementService.addMembership(new Membership(authManager.getNetId(), newHOA.getId(), true));
            //System.out.println("ok");

            return ResponseEntity.ok(newHOA);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
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
            if (membership.isPresent()) throw new Exception("User is already in this HOA");//need explanation
            if(!memberManagementService.addressCheck(hoa.get(), membership.get())) throw new Exception("Invalid address");
            //weird warning - should be resolved later (probably because of the isPresent() method)
            memberManagementService.addMembership(new Membership(request.username, hoa.get().getId(), false, request.country, request.city));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
    
}
