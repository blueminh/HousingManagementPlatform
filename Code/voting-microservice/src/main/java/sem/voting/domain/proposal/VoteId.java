package sem.voting.domain.proposal;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
public class VoteId implements Serializable {
    private static final long serialVersionUID = 0L;
    private int proposal;
    private int user;

    public VoteId(int proposal, int user) {
        this.proposal = proposal;
        this.user = user;
    }
}
