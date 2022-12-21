package sem.hoa.domain.services;

import org.springframework.stereotype.Service;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.entities.MembershipId;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Optional;

@Service
public class MemberManagementService {
    // TODO to be implemented
    private final transient MemberManagementRepository memberManagementRepository;

    public MemberManagementService(MemberManagementRepository memberManagementRepository) {
        this.memberManagementRepository = memberManagementRepository;
    }

    /**
     * When a user joins a Hoa, adds a new membership entry to the database.
     */
    public void addMembership(Membership membership) {
        memberManagementRepository.save(membership);
    }

    /**
     * When a user leaves a Hoa, remove the membership entry from the database.
     *
     * @param membershipId the membershipId consists of username and hoaID
     */
    public void removeMembership(MembershipId membershipId) throws Exception {
        Optional<Membership> toBeRemoved = findByUsernameAndHoaId(membershipId.getUsername(), membershipId.getHoaId());
        if (toBeRemoved.isPresent()) {
            memberManagementRepository.delete(toBeRemoved.get());
        } else {
            throw new Exception("User not found");
        }
    }

    public Optional<Membership> findByUsernameAndHoaId(String username, int hoaId) {
        return memberManagementRepository.findById(new MembershipId(username, hoaId));
    }

    /**
     * Find all board members of an HOA.
     *
     * @param hoaId id of the hoa
     * @return list of memberships for all the board members.
     */
    public List<Membership> findBoardMembersByHoaId(int hoaId) {
        return memberManagementRepository.findByHoaIdAndIsBoardMemberIsTrue(hoaId);
    }

    public boolean addressCheck(Hoa hoa, Membership membership) {
        return hoa.getCountry().equals(membership.getCountry()) && hoa.getCity().equals(membership.getCity());
    }

    /**
     * Find the ID of the Hoa of which this user is a board-member.
     *
     * @param username username
     * @return the ID of the Hoa or -1 if user is not a board member of any HOAs
     */
    public Integer isBoardMemberOf(String username) {
        Optional<Membership> membership = memberManagementRepository.findMembershipByUsernameAndIsBoardMemberIsTrue(username);
        if (membership.isEmpty()) {
            return -1;
        }
        return membership.get().getHoaId();
    }

    public boolean hasPossibleBoardCandidates(int hoaId) {
        return memberManagementRepository.existsByHoaIdAndJoiningDateLessThanEqual(hoaId,
                Instant.now().minus(Period.of(3, 0, 0)).getEpochSecond());
    }

}
