package sem.hoa.domain.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
public class MembershipId implements Serializable {
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private int hoaId;

    public MembershipId() {
    }

    public MembershipId(String username, int hoaId) {
        this.username = username;
        this.hoaId = hoaId;
    }
}
