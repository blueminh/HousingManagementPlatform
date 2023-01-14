package sem.hoa.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class Membership {
    @Id
    @Column(name = "username", nullable = false)
    private String username;

    @Getter
    @Id
    @Column(name = "hoaId", nullable = false)
    private int hoaId;

    @Setter
    @Getter
    // Enum type is complicated to be persisted in the database -> simply use a boolean value
    @Column(name = "isboardmember")
    private boolean isBoardMember;

    @Getter
    @Column(name = "country")
    private String country;

    @Getter
    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Getter
    @Column(name = "houseNumber")
    private int houseNumber;

    @Getter
    @Column(name = "postalCode")
    private String postalCode;

    @Getter
    @Column(name = "joiningDate")
    private Long joiningDate;

    @Getter
    @Setter
    @Column(name = "joiningBoardDate")
    private Long joiningBoardDate;
}
