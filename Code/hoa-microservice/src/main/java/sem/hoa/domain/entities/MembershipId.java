package sem.hoa.domain.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MembershipId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private int hoaId;
}
