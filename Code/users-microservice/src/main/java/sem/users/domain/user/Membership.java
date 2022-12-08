package sem.users.domain.user;

import lombok.EqualsAndHashCode;

import java.util.List;


/**
 * A DDD value object representing a membership in our domain.
 */
@EqualsAndHashCode
public class Membership {

    private transient List<HoaMembership> membershipList;

    public Membership(List<HoaMembership> membershipList) {
        // validate Username
        this.membershipList = membershipList;
    }

    public List<HoaMembership> getMembershipList() {
        return membershipList;
    }
}