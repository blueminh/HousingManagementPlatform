package sem.hoa.domain.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(MembershipId.class)
@Table(name = "memberManagements")
@NoArgsConstructor
@Data
public class Membership {
    @Id
    @Column(name = "username", nullable = false)
    private String username;

    @Id
    @Column(name = "hoaId", nullable = false)
    private int hoaId;

    // Enum type is complicated to be persisted in the database -> simply use a boolean value
    @Column(name = "isboardmember")
    private boolean isBoardMember;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "joiningDate")
    private Long joiningDate;

    @Column(name = "joiningBoardDate")
    private Long joiningBoardDate;

    /**
     * Constructor of a Membership object.
     *
     * @param username username
     * @param hoaId hoaId
     * @param isBoardMember isBoardMember
     * @param country country
     * @param city city
     * @param joiningDate joiningDate
     * @param joiningBoardDate joiningBoardDate
     */
    public Membership(String username, int hoaId, boolean isBoardMember, String country, String city, Long joiningDate, Long joiningBoardDate) {
        this.username = username;
        this.hoaId = hoaId;
        this.isBoardMember = isBoardMember;
        this.country = country;
        this.city = city;
        this.joiningDate = joiningDate;
        this.joiningBoardDate = joiningBoardDate;
    }
}
