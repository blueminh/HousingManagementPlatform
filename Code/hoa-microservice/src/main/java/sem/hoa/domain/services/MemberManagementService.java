package sem.hoa.domain.services;

import org.springframework.stereotype.Service;
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.entities.MembershipID;

import java.util.Optional;

@Service
public class MemberManagementService {
  // TODO to be implemented
  private final transient MemberManagementRepository memberManagementRepository;

  public MemberManagementService(MemberManagementRepository memberManagementRepository) {
    this.memberManagementRepository = memberManagementRepository;
  }

  /***
   * When a user joins a HOA, adds a new membership entry to the database.
   */
  public void addMembership(Membership membership){}

  /***
   * When a user leaves a HOA, remove the membership entry from the database
   * @param membershipID the membershipID consists of username and hoaID
   */
  public void removeMembership(MembershipID membershipID){}

  /***
   * A user can create his/her own HOA
   * @param hoa the new HOA
   */
  public void createHOA(HOA hoa){}

  // TODO: these methods are related to the voting system
//  public void applyForBoard(){}

  public Optional<Membership> findByUsernameAndHoaID(String username, int hoaID){
    return memberManagementRepository.findById(new MembershipID(username, hoaID));
  }

  /**
   * Find the ID of the HOA of which this user is a boardmember
   * @param username
   * @return the ID of the HOA or -1 if user is not a board member of any HOAs
   */
  public Integer isBoardMemberOf(String username){
    Optional<Membership> membership = memberManagementRepository.findMembershipByUsernameAndBoardMemberIsTrue(username);
    if (membership.isEmpty()) return -1;
    return membership.get().getHoaID();
  }
}
