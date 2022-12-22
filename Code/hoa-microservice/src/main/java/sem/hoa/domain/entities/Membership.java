package sem.hoa.domain.entities;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
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

    @Column(name = "street")
    private String street;

    @Column(name = "houseNumber")
    private int houseNumber;

    @Column(name = "postalCode")
    private String postalCode;

    @Column(name = "joiningDate")
    private Long joiningDate;

    @Column(name = "joiningBoardDate")
    private Long joiningBoardDate;
}
