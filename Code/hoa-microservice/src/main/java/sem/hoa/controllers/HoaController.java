package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.entities.MembershipId;
import sem.hoa.domain.services.HoaService;
import sem.hoa.domain.services.MemberManagementService;
import sem.hoa.dtos.HoaModifyDTO;
import sem.hoa.dtos.UserNameHoaNameDto;
import sem.hoa.exceptions.HoaJoiningException;

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
        return ResponseEntity.ok("Hello " + authManager.getUsername() + "! \nWelcome to Hoa!");

    }

    /**
     * Create a new Hoa.
     *
     * @param request information about the HOA
     * @return 200 if successfully created a HOA
     */
    @PostMapping("/createHOA")
    public ResponseEntity<Hoa> createHOA(@RequestBody HoaModifyDTO request) {

        try {
            //CHECKS
            hoaService.checkHoaModifyDTO(request);

            //Creation
            Hoa newHOA = new Hoa(request.hoaName, request.userCountry, request.userCity);

            hoaService.createNewHOA(newHOA);

            memberManagementService
                .addMembership(new Membership(authManager.getUsername(), newHOA.getId(), true,
                    request.userCountry, request.userCity,
                    request.userStreet, request.userHouseNumber, request.userPostalCode,
                    new Date().getTime(), new Date().getTime()));

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
            Hoa hoa = hoaService.findHoaByName(request.hoaName).get();
            if (memberManagementService
                .findByUsernameAndHoaId(authManager.getUsername(), hoa.getId())
                .isPresent()) {
                throw new HoaJoiningException("User is already in this HOA"); //need explanation
            }
            Membership membership = new Membership(authManager.getUsername(),
                hoaService.findHoaByName(request.hoaName).get().getId(), false,
                request.userCountry, request.userCity, request.userStreet,
                request.userHouseNumber, request.userPostalCode,
                new Date().getTime(), -1L);
            if (!memberManagementService.addressCheck(hoa, membership)) {
                throw new HoaJoiningException("Address not compatible with HOA area");
            }
            //CREATION
            memberManagementService.addMembership(membership);
            System.out.println("Member " + authManager.getUsername() + " added successfully to " + request.getHoaName());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Allows the user to leave an HOA.
     *
     * @param request contains hoa ID and username
     * @return a message that confirms that the user has left the HOA or throws an exception
     */
    @DeleteMapping("/leave")
    public ResponseEntity leaveHOA(@RequestBody UserNameHoaNameDto request) {
        try {
            if (!request.username.equals(authManager.getUsername())) {
                throw new UsernameNotFoundException("User not found");
            }

            Optional<Hoa> hoa = hoaService.findHoaByName(request.hoaName);
            if (hoa.isEmpty()) {
                throw new Exception("No such HOA with this name: " + request.hoaName);
            }

            Optional<Membership> membership = memberManagementService.findByUsernameAndHoaId(request.username, hoa.get().getId());
            if (membership.isEmpty()) {
                throw new Exception("User not found");
            }
            MembershipId toBeRemoved = new MembershipId(request.username, hoa.get().getId());
            memberManagementService.removeMembership(toBeRemoved);
            return ResponseEntity.ok("User has successfully left");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
