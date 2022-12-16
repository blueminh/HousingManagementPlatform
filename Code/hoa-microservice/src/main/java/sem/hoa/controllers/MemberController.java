package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.services.HOAService;
import sem.hoa.domain.services.MemberManagementService;
import sem.hoa.dtos.UserNameHoaIDDTO;
import sem.hoa.dtos.UserNameHoaNameDTO;

import java.util.Optional;

@RestController
@RequestMapping("/members")
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
   * Check if user is a board member of any HOAs
   * If yes, return the ID of the HOA
   */
  @GetMapping("/isaBoardMemberOfAny")
  public ResponseEntity<Integer> isBoardMember(@RequestBody String username) {
    try {
      if (username == null) throw new Exception();
      int hoaID = memberManagementService.isBoardMemberOf(username);
      return ResponseEntity.ok(hoaID);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  /**
   * Check whether a user is a member of a HOA
   * @param request contains username and hoaID
   */
  @GetMapping("/isMemberOf")
  public ResponseEntity<Boolean> isMemberOfHOA(@RequestBody UserNameHoaIDDTO request) {
    try {
      if (request.username == null) throw new Exception();
      Optional<HOA> hoa = hoaService.findHOAByID(request.hoaID);
      if (hoa.isEmpty()) throw new Exception("No such HOA with this ID: " + request.hoaID);

      Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(request.username, request.hoaID);
      return ResponseEntity.ok(membership.isPresent());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  /**
   * Return the joiningDate of a user
   * @param request username and hoaID
   */
  @GetMapping("/joiningDate")
  public ResponseEntity<Long> getJoiningDate(@RequestBody UserNameHoaIDDTO request){
    try {
      if (request.username == null) throw new Exception();
      Optional<HOA> hoa = hoaService.findHOAByID(request.hoaID);
      if (hoa.isEmpty()) throw new Exception("No such HOA with this ID: " + request.hoaID);

      Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(request.username, request.hoaID);
      if (membership.isEmpty()) throw new Exception("User is not registered in this HOA");
      return ResponseEntity.ok(membership.get().getJoiningDate());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping("/joiningDate")
  public ResponseEntity<Long> getBoardJoiningDate(@RequestBody UserNameHoaIDDTO request){
    try {
      if (request.username == null) throw new Exception();
      Optional<HOA> hoa = hoaService.findHOAByID(request.hoaID);
      if (hoa.isEmpty()) throw new Exception("No such HOA with this ID: " + request.hoaID);

      Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(request.username, request.hoaID);
      if (membership.isEmpty()) throw new Exception("User is not registered in this HOA");
      if (membership.get().getJoiningBoardDate() == -1) throw new Exception("User is not a board member of this HOA");
      return ResponseEntity.ok(membership.get().getJoiningBoardDate());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

}
