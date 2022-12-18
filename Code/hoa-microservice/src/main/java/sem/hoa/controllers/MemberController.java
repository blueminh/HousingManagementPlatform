package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.services.HOAService;
import sem.hoa.domain.services.MemberManagementService;

import java.util.Optional;

@RestController
@RequestMapping("/member")
public class MemberController {
  private final transient AuthManager authManager;
  private final transient MemberManagementService memberManagementService;
  private final transient HOAService hoaService;

  @Autowired
  public MemberController(AuthManager authManager, MemberManagementService memberManagementService, HOAService hoaService) {
    this.authManager = authManager;
    this.memberManagementService = memberManagementService;
    this.hoaService = hoaService;
  }

  /**
   * Find this user's role for a given hoa name
   * Username is extracted from the auth token
   * @param hoaName contains hoa name
   * @return a boolean value indicate the role of this user (true if boardMember)
   */
  @GetMapping("/findUserRoleByHoaName")
  public ResponseEntity<String> findUserRoleByHoaName(@RequestParam(name = "hoaName") String hoaName) {
    try {
      Optional<HOA> hoa = hoaService.findHOAByName(hoaName);
      if (hoa.isEmpty()) throw new BadRequestException("No such HOA with this name: " + hoaName);

      Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(authManager.getNetId(), hoa.get().getId());
      if (membership.isEmpty()) throw new BadRequestException("User is not registered in this HOA");

      if (membership.get().isBoardMember()) return ResponseEntity.ok("boardMember");
      return ResponseEntity.ok("normalMember");
    } catch (BadRequestException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  /**
   * Find this user's role for a given hoa ID
   * Username is extracted from the auth token
   * @param hoaID contains hoa ID
   * @return a boolean value indicate the role of this user (true if boardMember)
   */
  @GetMapping("/findUserRoleByHoaID")
  public ResponseEntity<String> findUserRoleByHoaID(@RequestParam(name = "hoaID") Integer hoaID) {
    try {
      Optional<HOA> hoa = hoaService.findHOAByID(hoaID);
      if (hoa.isEmpty()) throw new BadRequestException("No such HOA with this ID: " + hoaID);

      String username = authManager.getNetId();
      Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(username, hoa.get().getId());
      if (membership.isEmpty()) throw new BadRequestException("User is not registered in this HOA");

      if (membership.get().isBoardMember()) return ResponseEntity.ok("boardMember");
      return ResponseEntity.ok("normalMember");
    } catch (BadRequestException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  /**
   * Check if user is a board member of any HOAs
   * If yes, return the ID of the HOA
   */
  @GetMapping("/isaBoardMemberOfAny")
  public ResponseEntity<Integer> isBoardMember() {
    try {
      String username = authManager.getNetId();
      int hoaID = memberManagementService.isBoardMemberOf(username);
      return ResponseEntity.ok(hoaID);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  /**
   * Check whether a user is a member of a HOA
   * Username is extracted from the auth token
   * @param hoaID contains hoaID
   */
  @GetMapping("/isMemberOf")
  public ResponseEntity<String> isMemberOfHOA(@RequestParam(name = "hoaID") Integer hoaID) {
    try {
      Optional<HOA> hoa = hoaService.findHOAByID(hoaID);
      if (hoa.isEmpty()) throw new BadRequestException("No such HOA with this ID: " + hoaID);

      Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(authManager.getNetId(), hoaID);
      return ResponseEntity.ok(Boolean.toString(membership.isPresent()));
    } catch (BadRequestException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  /**
   * Return the joiningDate of a user
   * Username is extracted from the auth token
   * @param hoaID the hoaID
   */
  @GetMapping("/joiningDate")
  public ResponseEntity<String> getJoiningDate(@RequestParam(name = "hoaID") Integer hoaID){
    try {
      Optional<HOA> hoa = hoaService.findHOAByID(hoaID);
      if (hoa.isEmpty()) throw new BadRequestException("No such HOA with this ID: " + hoaID);

      Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(authManager.getNetId(), hoaID);
      if (membership.isEmpty()) throw new BadRequestException("User is not registered in this HOA");
      return ResponseEntity.ok(membership.get().getJoiningDate().toString());
    } catch (BadRequestException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  /***
   * Return the board joning date of a user given a hoaID
   * Username is taken from auth token
   * @param hoaID the hoaID
   */
  @GetMapping("/joiningBoardDate")
  public ResponseEntity<String> getBoardJoiningDate(@RequestParam(name = "hoaID") Integer hoaID){
    try {
      Optional<HOA> hoa = hoaService.findHOAByID(hoaID);
      if (hoa.isEmpty()) throw new BadRequestException("No such HOA with this ID: " + hoaID);

      Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(authManager.getNetId(), hoaID);
      if (membership.isEmpty()) throw new BadRequestException("User is not registered in this HOA");
      if (membership.get().getJoiningBoardDate() == -1) throw new BadRequestException("User is not a board member of this HOA");
      return ResponseEntity.ok(membership.get().getJoiningBoardDate().toString());
    } catch (BadRequestException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

}
