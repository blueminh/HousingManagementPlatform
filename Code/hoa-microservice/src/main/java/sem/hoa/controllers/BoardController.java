package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.authentication.AuthManager;
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.services.HOAService;
import sem.hoa.domain.services.MemberManagementService;
import sem.hoa.dtos.Pair;


import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/board")
public class BoardController {
  private final transient AuthManager authManager;
  private final transient MemberManagementService memberManagementService;
  private final transient HOAService hoaService;

  /**
   * Instantiates a new controller.
   *
   * @param authManager Spring Security component used to authenticate and authorize the user
   */
  @Autowired
  public BoardController(AuthManager authManager, MemberManagementService memberManagementService, HOAService hoaService) {
    this.authManager = authManager;
    this.memberManagementService = memberManagementService;
    this.hoaService = hoaService;
  }

  /**
   * Any users can apply for board election
   * @param hoaName name of the hoa
   */
  @PostMapping("/apply")
  public ResponseEntity applyForBoard(@RequestBody String hoaName) {
    try {

      Optional<HOA> hoa = hoaService.findHOAByName(hoaName);
      if (hoa.isEmpty()) throw new Exception("No such HOA with this name: " + hoaName);

      Optional<Membership> membership = memberManagementService.findByUsernameAndHoaID(authManager.getNetId(), hoa.get().getId());
      if (membership.isEmpty()) throw new Exception("User is not in this HOA");
      if (membership.get().isBoardMember()) throw new Exception("User is already a board member of this HOA");

      Pair<Long, Long> electionTime = hoaService.findBoardElectionStartTime(null, hoa.get().getId());
      Date now = new Date();
      if (now.getTime() < electionTime.first|| now.getTime() > electionTime.second)
        throw new Exception("This HOA is not having a board election at the moment");

      // TODO request the voting system here
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    return ResponseEntity.ok().build();
  }

//  @PostMapping("/vote")
//  public ResponseEntity voteForBoard(@RequestBody )
}
