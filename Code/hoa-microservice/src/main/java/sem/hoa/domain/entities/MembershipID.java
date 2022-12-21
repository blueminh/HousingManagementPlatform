package sem.hoa.domain.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MembershipID implements Serializable {
    private static final long serialVersionUID = 0L;

    @EqualsAndHashCode.Include
    @Getter
    private String username;

    @EqualsAndHashCode.Include
    @Getter
    private int hoaID;
}
