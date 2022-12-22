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
import sem.hoa.dtos.HoaModifyDTO;
import sem.hoa.exceptions.HoaJoiningException;

import java.util.Date;

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
                .addMembership(new Membership(authManager.getNetId(), newHOA.getId(), true,
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
                .findByUsernameAndHoaId(authManager.getNetId(), hoa.getId())
                .isPresent()) {
                throw new HoaJoiningException("User is already in this HOA"); //need explanation
            }
            Membership membership = new Membership(authManager.getNetId(),
                hoaService.findHoaByName(request.hoaName).get().getId(), false,
                request.userCountry, request.userCity, request.userStreet,
                request.userHouseNumber, request.userPostalCode,
                new Date().getTime(), -1L);
            if (!memberManagementService.addressCheck(hoa, membership)) {
                throw new HoaJoiningException("Invalid address");
            }
            //CREATION
            memberManagementService.addMembership(membership);
            System.out.println("Member " + authManager.getNetId() + " added successfully to " + request.getHoaName());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

}
