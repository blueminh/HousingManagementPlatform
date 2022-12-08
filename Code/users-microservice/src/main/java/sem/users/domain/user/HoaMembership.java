package sem.users.domain.user;

import java.util.Objects;

public class HoaMembership {


    public enum RoleType {
        MEMBER,
        BOARD_MEMBER
    }

    private final transient RoleType roleType;
    private final transient String hoaid;

    /** Constructor.
     *
     * @param hoaid the ID of the HOA
     *
     * @param roleType role of the user in the HOA
     *
     */
    public HoaMembership(String hoaid, String roleType) throws Exception {
        if (roleType.equals("MEMBER")) {
            this.roleType = RoleType.MEMBER;
        } else if (roleType.equals("BOARD_MEMBER")) {
            this.roleType = RoleType.BOARD_MEMBER;
        } else {
            throw new WronngRoleTypeException(roleType);
        }
        this.hoaid = hoaid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HoaMembership that = (HoaMembership) o;
        return hoaid.equals(that.hoaid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaid);
    }

    public RoleType getRole() {
        return this.roleType;
    }

    public String getHoaid() {
        return hoaid;
    }

    @Override
    public String toString() {
        return "HOAID=" + hoaid;
    }
}
